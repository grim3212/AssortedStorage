package com.grim3212.assorted.storage.common.block;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.common.block.tileentity.BaseStorageTileEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

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

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final VoxelShape FAKE_SIDES_AND_BOTTOM = Block.makeCuboidShape(0.01D, 0.01D, 0.01D, 16.0D, 16.0D, 16.0D);

	public BaseStorageBlock(Properties properties) {
		super(properties.hardnessAndResistance(3.0f, 5.0f));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (context == ISelectionContext.dummy())
			return FAKE_SIDES_AND_BOTTOM;

		return super.getShape(state, worldIn, pos, context);
	}

	protected boolean isInvalidBlock(IWorld world, BlockPos pos) {
		return !world.isAirBlock(pos) && world.getBlockState(pos).isOpaqueCube(world, pos);
	}

	protected boolean isDoorBlocked(IWorld world, BlockPos pos) {
		return isInvalidBlock(world, pos.offset(world.getBlockState(pos).get(FACING)));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getPlacementHorizontalFacing().getOpposite();

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);

		if (te instanceof BaseStorageTileEntity) {
			BaseStorageTileEntity tileentity = (BaseStorageTileEntity) te;

			if (tileentity.isLocked() && !this.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof BaseStorageTileEntity) {
			if (stack.hasDisplayName()) {
				((BaseStorageTileEntity) tileentity).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof BaseStorageTileEntity) {
				BaseStorageTileEntity teStorage = (BaseStorageTileEntity) tileentity;

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}

				InventoryHelper.dropInventoryItems(worldIn, pos, (BaseStorageTileEntity) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	protected boolean canBeLocked(World worldIn, BlockPos pos) {
		return !((BaseStorageTileEntity) worldIn.getTileEntity(pos)).isLocked();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getHeldItem(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (tryPlaceLock(worldIn, pos, player, handIn))
				return ActionResultType.SUCCESS;
		}

		if (player.isSneaking() && this.canAccess(worldIn, pos, player)) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof BaseStorageTileEntity) {
				BaseStorageTileEntity teStorage = (BaseStorageTileEntity) worldIn.getTileEntity(pos);

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);

					if (removeLock(worldIn, pos, player)) {
						ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), lockStack);
						if (!worldIn.isRemote) {
							worldIn.addEntity(blockDropped);
							if (!(player instanceof FakePlayer)) {
								blockDropped.onCollideWithPlayer(player);
							}
						}
						return ActionResultType.SUCCESS;
					}
				}
			}
		}

		if (!isDoorBlocked(worldIn, pos) && this.canAccess(worldIn, pos, player)) {
			if (!worldIn.isRemote) {
				INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
				if (inamedcontainerprovider != null) {
					NetworkHooks.openGui((ServerPlayerEntity) player, inamedcontainerprovider, pos);
					player.addStat(this.getOpenStat());
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
	public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider) tileentity : null;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstoneFromInventory((IInventory) worldIn.getTileEntity(pos));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	private boolean removeLock(World worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BaseStorageTileEntity tileentity = (BaseStorageTileEntity) worldIn.getTileEntity(pos);
		tileentity.setLockCode("");
		tileentity.getWorld().playSound(entityplayer, tileentity.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, tileentity.getWorld().rand.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	private boolean tryPlaceLock(World worldIn, BlockPos pos, PlayerEntity entityplayer, Hand hand) {
		BaseStorageTileEntity tileentity = (BaseStorageTileEntity) worldIn.getTileEntity(pos);
		ItemStack itemstack = entityplayer.getHeldItem(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				if (!entityplayer.isCreative())
					itemstack.shrink(1);
				tileentity.setLockCode(code);
				tileentity.getWorld().playSound(entityplayer, tileentity.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, tileentity.getWorld().rand.nextFloat() * 0.1F + 0.9F);
				return true;
			}

		}

		return false;
	}

	private boolean canAccess(IBlockReader worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BaseStorageTileEntity tileentity = (BaseStorageTileEntity) worldIn.getTileEntity(pos);

		if (tileentity.isLocked()) {
			for (int slot = 0; slot < entityplayer.inventory.getSizeInventory(); slot++) {
				ItemStack itemstack = entityplayer.inventory.getStackInSlot(slot);

				if ((!itemstack.isEmpty()) && (itemstack.getItem() == StorageItems.LOCKSMITH_KEY.get())) {
					if (itemstack.hasTag()) {
						String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
						if (!code.isEmpty()) {
							return tileentity.getLockCode().equals(code);
						}
					}
				}
			}

			return false;
		}

		return true;
	}
}