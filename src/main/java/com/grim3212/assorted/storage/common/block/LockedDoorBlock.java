package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.BaseLockedTileEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class LockedDoorBlock extends DoorBlock {

	private final Block parent;

	public LockedDoorBlock(Block parent, Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
		this.parent = parent;
	}

	public LockedDoorBlock(ResourceLocation parent, Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
		this.parent = Registry.BLOCK.get(parent);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return this.parent.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		// redstone doesn't work with locked doors
	}

	@Override
	public void setOpen(World worldIn, BlockState state, BlockPos pos, boolean open) {
		// AI can't open locked doors
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {

			boolean isValidBlock = doubleblockhalf == DoubleBlockHalf.UPPER ? worldIn.getBlockState(currentPos.below()).getBlock() instanceof DoorBlock : worldIn.getBlockState(currentPos.above()).getBlock() instanceof DoorBlock;

			return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED)) : isValidBlock ? stateIn : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (player.isShiftKeyDown() && StorageUtil.canAccess(worldIn, pos, player)) {
			TileEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof BaseLockedTileEntity) {
				BaseLockedTileEntity teStorage = (BaseLockedTileEntity) worldIn.getBlockEntity(pos);

				if (teStorage.isLocked()) {
					if (removeLock(worldIn, pos, player)) {
						return ActionResultType.SUCCESS;
					}
				}
			}
		}

		if (StorageUtil.canAccess(worldIn, pos, player)) {
			state = state.cycle(OPEN);
			worldIn.setBlock(pos, state, 10);
			worldIn.levelEvent(player, state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getBlockEntity(pos);

		if (te instanceof BaseLockedTileEntity) {
			BaseLockedTileEntity tileentity = (BaseLockedTileEntity) te;

			if (tileentity.isLocked() && !StorageUtil.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getBlockEntity(pos);

			if (tileentity instanceof BaseLockedTileEntity) {
				BaseLockedTileEntity teStorage = (BaseLockedTileEntity) tileentity;

				if (teStorage.isLocked() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	private int getCloseSound() {
		return this.material == Material.METAL ? 1011 : 1012;
	}

	private int getOpenSound() {
		return this.material == Material.METAL ? 1005 : 1006;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new BaseLockedTileEntity();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	private boolean removeLock(World worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BlockState state = worldIn.getBlockState(pos);
		Direction dir = state.getValue(FACING);
		boolean open = state.getValue(OPEN);
		DoorHingeSide hinge = state.getValue(HINGE);
		DoubleBlockHalf half = state.getValue(HALF);
		BlockState toPlace = this.parent.defaultBlockState().setValue(FACING, dir).setValue(OPEN, open).setValue(HINGE, hinge);

		worldIn.setBlock(pos, toPlace.setValue(HALF, half), 3);
		worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

		if (half == DoubleBlockHalf.UPPER) {
			worldIn.setBlock(pos.below(), toPlace.setValue(HALF, DoubleBlockHalf.LOWER), 3);
		} else {
			worldIn.setBlock(pos.above(), toPlace.setValue(HALF, DoubleBlockHalf.UPPER), 3);
		}

		return true;
	}
}
