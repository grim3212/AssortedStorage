package com.grim3212.assorted.storage.common.block;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class LockedDoorBlock extends DoorBlock implements EntityBlock {

	private final Block parent;

	public LockedDoorBlock(DoorBlock parent, Properties builder) {
		super(builder, parent.closeSound, parent.openSound);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
		this.parent = parent;
	}

	public LockedDoorBlock(ResourceLocation parent, SoundEvent closeSound, SoundEvent openSound, Properties builder) {
		super(builder, closeSound, openSound);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
		this.parent = ForgeRegistries.BLOCKS.getValue(parent);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return this.parent.getCloneItemStack(state, target, world, pos, player);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		// redstone doesn't work with locked doors
	}

	@Override
	public void setOpen(Entity entity, Level worldIn, BlockState state, BlockPos pos, boolean open) {
		// AI can't open locked doors
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {

			boolean isValidBlock = doubleblockhalf == DoubleBlockHalf.UPPER ? worldIn.getBlockState(currentPos.below()).getBlock() instanceof DoorBlock : worldIn.getBlockState(currentPos.above()).getBlock() instanceof DoorBlock;

			return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED)) : isValidBlock ? stateIn : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (player.isShiftKeyDown() && StorageUtil.canAccess(worldIn, pos, player)) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof BaseLockedBlockEntity) {
				BaseLockedBlockEntity teStorage = (BaseLockedBlockEntity) worldIn.getBlockEntity(pos);

				if (teStorage.isLocked()) {
					if (removeLock(worldIn, pos, player)) {
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		if (StorageUtil.canAccess(worldIn, pos, player)) {
			state = state.cycle(OPEN);
			worldIn.setBlock(pos, state, 10);
			this.playSound(player, worldIn, pos, state.getValue(OPEN));
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean isOpen) {
		level.playSound(entity, pos, isOpen ? this.openSound : this.closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
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

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BaseLockedBlockEntity(pos, state);
	}

	private boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
		BlockState state = worldIn.getBlockState(pos);
		Direction dir = state.getValue(FACING);
		boolean open = state.getValue(OPEN);
		DoorHingeSide hinge = state.getValue(HINGE);
		DoubleBlockHalf half = state.getValue(HALF);
		BlockState toPlace = this.parent.defaultBlockState().setValue(FACING, dir).setValue(OPEN, open).setValue(HINGE, hinge);

		worldIn.setBlock(pos, toPlace.setValue(HALF, half), 3);
		worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		if (half == DoubleBlockHalf.UPPER) {
			worldIn.setBlock(pos.below(), toPlace.setValue(HALF, DoubleBlockHalf.LOWER), 3);
		} else {
			worldIn.setBlock(pos.above(), toPlace.setValue(HALF, DoubleBlockHalf.UPPER), 3);
		}

		return true;
	}
}
