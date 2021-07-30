package com.grim3212.assorted.storage.common.block;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public abstract class BaseStorageBlock extends Block implements EntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final VoxelShape FAKE_SIDES_AND_BOTTOM = Block.box(0.01D, 0.01D, 0.01D, 16.0D, 16.0D, 16.0D);

	public BaseStorageBlock(Properties properties) {
		super(properties.strength(3.0f, 5.0f));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (context == CollisionContext.empty())
			return FAKE_SIDES_AND_BOTTOM;

		return super.getShape(state, worldIn, pos, context);
	}

	protected boolean isInvalidBlock(LevelAccessor world, BlockPos pos) {
		return !world.isEmptyBlock(pos) && world.getBlockState(pos).isSolidRender(world, pos);
	}

	protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
		return isInvalidBlock(world, pos.relative(world.getBlockState(pos).getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getHorizontalDirection().getOpposite();

		return this.defaultBlockState().setValue(FACING, direction);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);

		if (te instanceof BaseStorageBlockEntity) {
			BaseStorageBlockEntity tileentity = (BaseStorageBlockEntity) te;

			if (tileentity.isLocked() && !this.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);

		if (tileentity instanceof BaseStorageBlockEntity) {
			if (stack.hasCustomHoverName()) {
				((BaseStorageBlockEntity) tileentity).setCustomName(stack.getHoverName());
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if (tileentity instanceof BaseStorageBlockEntity) {
				BaseStorageBlockEntity teStorage = (BaseStorageBlockEntity) tileentity;

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundTag tag = new CompoundTag();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}

				Containers.dropContents(worldIn, pos, (BaseStorageBlockEntity) tileentity);
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	protected boolean canBeLocked(Level worldIn, BlockPos pos) {
		return !((BaseStorageBlockEntity) worldIn.getBlockEntity(pos)).isLocked();
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (tryPlaceLock(worldIn, pos, player, handIn))
				return InteractionResult.SUCCESS;
		}

		if (player.isShiftKeyDown() && this.canAccess(worldIn, pos, player)) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof BaseStorageBlockEntity) {
				BaseStorageBlockEntity teStorage = (BaseStorageBlockEntity) worldIn.getBlockEntity(pos);

				if (teStorage.isLocked()) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundTag tag = new CompoundTag();
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
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		if (!isDoorBlocked(worldIn, pos) && this.canAccess(worldIn, pos, player)) {
			if (!worldIn.isClientSide) {
				MenuProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
				if (inamedcontainerprovider != null) {
					NetworkHooks.openGui((ServerPlayer) player, inamedcontainerprovider, pos);
					player.awardStat(this.getOpenStat());
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	// TODO: Create custom stat for this
	protected Stat<ResourceLocation> getOpenStat() {
		return Stats.CUSTOM.get(Stats.OPEN_CHEST);
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return (level1, blockPos, blockState, t) -> {
			if (t instanceof BaseStorageBlockEntity storage) {
				storage.tick();
			}
		};
	}

	@Override
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		super.triggerEvent(state, worldIn, pos, id, param);
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromContainer((Container) worldIn.getBlockEntity(pos));
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
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}

	private boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
		BaseStorageBlockEntity tileentity = (BaseStorageBlockEntity) worldIn.getBlockEntity(pos);
		tileentity.setLockCode("");
		tileentity.getLevel().playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, tileentity.getLevel().random.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	private boolean tryPlaceLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
		BaseStorageBlockEntity tileentity = (BaseStorageBlockEntity) worldIn.getBlockEntity(pos);
		ItemStack itemstack = entityplayer.getItemInHand(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				if (!entityplayer.isCreative())
					itemstack.shrink(1);
				tileentity.setLockCode(code);
				tileentity.getLevel().playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, tileentity.getLevel().random.nextFloat() * 0.1F + 0.9F);
				return true;
			}

		}

		return false;
	}

	private boolean canAccess(BlockGetter worldIn, BlockPos pos, Player entityplayer) {
		BaseStorageBlockEntity tileentity = (BaseStorageBlockEntity) worldIn.getBlockEntity(pos);

		if (tileentity.isLocked()) {
			for (int slot = 0; slot < entityplayer.getInventory().getContainerSize(); slot++) {
				ItemStack itemstack = entityplayer.getInventory().getItem(slot);

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