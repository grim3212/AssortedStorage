package com.grim3212.assorted.storage.common.block;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.common.block.tileentity.ILockeable;
import com.grim3212.assorted.storage.common.block.tileentity.INamed;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class BaseStorageBlock extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final VoxelShape FAKE_SIDES_AND_BOTTOM = Block.box(0.01D, 0.01D, 0.01D, 16.0D, 16.0D, 16.0D);

	public BaseStorageBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (context == ISelectionContext.empty())
			return FAKE_SIDES_AND_BOTTOM;

		return super.getShape(state, worldIn, pos, context);
	}

	protected boolean isInvalidBlock(IWorld world, BlockPos pos) {
		return !world.isEmptyBlock(pos) && world.getBlockState(pos).isSolidRender(world, pos);
	}

	protected boolean isDoorBlocked(IWorld world, BlockPos pos) {
		return isInvalidBlock(world, pos.relative(world.getBlockState(pos).getValue(FACING)));
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getHorizontalDirection().getOpposite();

		return this.defaultBlockState().setValue(FACING, direction);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getBlockEntity(pos);

		if (te instanceof ILockeable) {
			ILockeable tileentity = (ILockeable) te;

			if (tileentity.isLocked() && !StorageUtil.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);

		if (tileentity instanceof INamed) {
			if (stack.hasCustomHoverName()) {
				((INamed) tileentity).setCustomName(stack.getHoverName());
			}
		}
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getBlockEntity(pos);

			if (tileentity instanceof ILockeable) {
				ILockeable teStorage = (ILockeable) tileentity;

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}

				InventoryHelper.dropContents(worldIn, pos, (IInventory) tileentity);
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	protected boolean canBeLocked(World worldIn, BlockPos pos) {
		return !((ILockeable) worldIn.getBlockEntity(pos)).isLocked();
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (tryPlaceLock(worldIn, pos, player, handIn))
				return ActionResultType.SUCCESS;
		}

		if (player.isShiftKeyDown() && StorageUtil.canAccess(worldIn, pos, player)) {
			TileEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof ILockeable) {
				ILockeable teStorage = (ILockeable) worldIn.getBlockEntity(pos);

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);

					if (removeLock(worldIn, pos, player)) {
						ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), lockStack);
						if (!worldIn.isClientSide) {
							worldIn.addFreshEntity(blockDropped);
							if (!(player instanceof FakePlayer)) {
								blockDropped.playerTouch(player);
							}
						}
						return ActionResultType.SUCCESS;
					}
				}
			}
		}

		if (!isDoorBlocked(worldIn, pos) && StorageUtil.canAccess(worldIn, pos, player)) {
			if (!worldIn.isClientSide) {
				INamedContainerProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
				if (inamedcontainerprovider != null) {
					NetworkHooks.openGui((ServerPlayerEntity) player, inamedcontainerprovider, pos);
					player.awardStat(this.getOpenStat());
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	// TODO: Create custom stat for this
	protected Stat<ResourceLocation> getOpenStat() {
		return Stats.CUSTOM.get(Stats.OPEN_CHEST);
	}

	@Override
	@Nullable
	public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
		TileEntity tileentity = world.getBlockEntity(pos);
		return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider) tileentity : null;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.triggerEvent(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos) {
		return Container.getRedstoneSignalFromContainer((IInventory) worldIn.getBlockEntity(pos));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	protected boolean removeLock(World worldIn, BlockPos pos, PlayerEntity entityplayer) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		ILockeable lockeable = (ILockeable) tileentity;
		lockeable.setLockCode("");
		worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	protected boolean tryPlaceLock(World worldIn, BlockPos pos, PlayerEntity entityplayer, Hand hand) {
		TileEntity tileentity = worldIn.getBlockEntity(pos);
		ILockeable lockeable = (ILockeable) tileentity;
		ItemStack itemstack = entityplayer.getItemInHand(hand);

		String code = StorageUtil.getCode(itemstack);

		if (!code.isEmpty()) {
			if (!entityplayer.isCreative())
				itemstack.shrink(1);
			lockeable.setLockCode(code);
			worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
			return true;
		}

		return false;
	}

}