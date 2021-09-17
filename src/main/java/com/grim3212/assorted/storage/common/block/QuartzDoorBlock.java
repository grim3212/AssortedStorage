package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;

public class QuartzDoorBlock extends DoorBlock implements EntityBlock {

	public static final BooleanProperty LOCKED = BooleanProperty.create("locked");

	public QuartzDoorBlock(Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER).setValue(LOCKED, false));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		boolean isLocked = !this.canBeLocked(worldIn, currentPos);
		DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED)).setValue(LOCKED, isLocked) : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn.setValue(LOCKED, isLocked);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED, LOCKED);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (blockIn != this && flag != state.getValue(POWERED) && this.canBeLocked(worldIn, pos)) {
			if (flag != state.getValue(OPEN)) {
				this.playSound(worldIn, pos, flag);
			}

			worldIn.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
		}

	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (tryPlaceLock(worldIn, pos, player, handIn))
				return InteractionResult.SUCCESS;
		}

		if (player.isShiftKeyDown() && StorageUtil.canAccess(worldIn, pos, player)) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof BaseLockedBlockEntity) {
				BaseLockedBlockEntity teStorage = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos);

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

		if (StorageUtil.canAccess(worldIn, pos, player)) {
			state = state.cycle(OPEN);
			worldIn.setBlock(pos, state, 10);
			worldIn.levelEvent(player, state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	@Override
	public void setOpen(Entity entity, Level worldIn, BlockState state, BlockPos pos, boolean open) {
		if (state.is(this) && state.getValue(OPEN) != open && !state.getValue(LOCKED)) {
			worldIn.setBlock(pos, state.setValue(OPEN, open), 10);
			this.playSound(worldIn, pos, open);
			worldIn.gameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		}
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);

		if (te instanceof BaseLockedBlockEntity) {
			BaseLockedBlockEntity tileentity = (BaseLockedBlockEntity) te;

			if (tileentity.isLocked() && !StorageUtil.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if (tileentity instanceof BaseLockedBlockEntity) {
				BaseLockedBlockEntity teStorage = (BaseLockedBlockEntity) tileentity;

				if (teStorage.isLocked() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundTag tag = new CompoundTag();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	private void playSound(Level worldIn, BlockPos pos, boolean isOpening) {
		worldIn.levelEvent((Player) null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
	}

	private int getCloseSound() {
		return this.material == Material.METAL ? 1011 : 1012;
	}

	private int getOpenSound() {
		return this.material == Material.METAL ? 1005 : 1006;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BaseLockedBlockEntity(pos, state);
	}

	protected boolean canBeLocked(LevelAccessor worldIn, BlockPos pos) {
		return !((BaseLockedBlockEntity) worldIn.getBlockEntity(pos)).isLocked();
	}

	private boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
		BaseLockedBlockEntity tileentity = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos);
		tileentity.setLockCode("");
		worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		BlockState blockstate = Block.updateFromNeighbourShapes(state, worldIn, pos);
		Block.updateOrDestroy(state, blockstate, worldIn, pos, 3);

		if (worldIn.getBlockState(pos).getValue(HALF) == DoubleBlockHalf.UPPER) {
			BaseLockedBlockEntity tileentityLower = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos.below());
			if (tileentityLower != null) {
				tileentityLower.setLockCode("");
			}

			BlockState stateLower = worldIn.getBlockState(pos.below());
			BlockState blockstateLower = Block.updateFromNeighbourShapes(stateLower, worldIn, pos.below());
			Block.updateOrDestroy(stateLower, blockstateLower, worldIn, pos.below(), 3);
		} else {
			BaseLockedBlockEntity tileentityUpper = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos.above());
			if (tileentityUpper != null) {
				tileentityUpper.setLockCode("");
			}

			BlockState stateUpper = worldIn.getBlockState(pos.above());
			BlockState blockstateUpper = Block.updateFromNeighbourShapes(stateUpper, worldIn, pos.above());
			Block.updateOrDestroy(stateUpper, blockstateUpper, worldIn, pos.above(), 3);
		}

		return true;
	}

	private boolean tryPlaceLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
		ItemStack itemstack = entityplayer.getItemInHand(hand);
		String code = StorageUtil.getCode(itemstack);

		if (!code.isEmpty()) {
			BaseLockedBlockEntity tileentity = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos);

			if (!entityplayer.isCreative())
				itemstack.shrink(1);
			tileentity.setLockCode(code);
			worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

			BlockState state = worldIn.getBlockState(pos);
			BlockState blockstate = Block.updateFromNeighbourShapes(state, worldIn, pos);
			Block.updateOrDestroy(state, blockstate, worldIn, pos, 3);
			if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
				BaseLockedBlockEntity tileentityLower = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos.below());
				if (tileentityLower != null) {
					tileentityLower.setLockCode(code);
				}

				BlockState stateLower = worldIn.getBlockState(pos.below());
				BlockState blockstateLower = Block.updateFromNeighbourShapes(stateLower, worldIn, pos.below());
				Block.updateOrDestroy(stateLower, blockstateLower, worldIn, pos.below(), 3);

			} else {
				BaseLockedBlockEntity tileentityUpper = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos.above());
				if (tileentityUpper != null) {
					tileentityUpper.setLockCode(code);
				}

				BlockState stateUpper = worldIn.getBlockState(pos.above());
				BlockState blockstateUpper = Block.updateFromNeighbourShapes(stateUpper, worldIn, pos.above());
				Block.updateOrDestroy(stateUpper, blockstateUpper, worldIn, pos.above(), 3);
			}

			return true;

		}

		return false;
	}
}
