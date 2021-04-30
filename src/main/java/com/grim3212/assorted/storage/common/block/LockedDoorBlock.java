package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.BaseLockedTileEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

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
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHingeSide.LEFT).with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER));
		this.parent = parent;
	}

	public LockedDoorBlock(ResourceLocation parent, Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHingeSide.LEFT).with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER));
		this.parent = Registry.BLOCK.getOrDefault(parent);
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
	public void openDoor(World worldIn, BlockState state, BlockPos pos, boolean open) {
		// AI can't open locked doors
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {

			boolean isValidBlock = doubleblockhalf == DoubleBlockHalf.UPPER ? worldIn.getBlockState(currentPos.down()).getBlock() instanceof DoorBlock : worldIn.getBlockState(currentPos.up()).getBlock() instanceof DoorBlock;

			return facingState.matchesBlock(this) && facingState.get(HALF) != doubleblockhalf ? stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(HINGE, facingState.get(HINGE)).with(POWERED, facingState.get(POWERED)) : isValidBlock ? stateIn : Blocks.AIR.getDefaultState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (player.isSneaking() && this.canAccess(worldIn, pos, player)) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof BaseLockedTileEntity) {
				BaseLockedTileEntity teStorage = (BaseLockedTileEntity) worldIn.getTileEntity(pos);

				if (teStorage.isLocked()) {
					if (removeLock(worldIn, pos, player)) {
						return ActionResultType.SUCCESS;
					}
				}
			}
		}

		if (this.canAccess(worldIn, pos, player)) {
			state = state.cycleValue(OPEN);
			worldIn.setBlockState(pos, state, 10);
			worldIn.playEvent(player, state.get(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
			return ActionResultType.SUCCESS;
		} else {
			return ActionResultType.PASS;
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);

		if (te instanceof BaseLockedTileEntity) {
			BaseLockedTileEntity tileentity = (BaseLockedTileEntity) te;

			if (tileentity.isLocked() && !this.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof BaseLockedTileEntity) {
				BaseLockedTileEntity teStorage = (BaseLockedTileEntity) tileentity;

				if (teStorage.isLocked() && state.get(HALF) == DoubleBlockHalf.UPPER) {
					ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
					CompoundNBT tag = new CompoundNBT();
					new StorageLockCode(teStorage.getLockCode()).write(tag);
					lockStack.setTag(tag);
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
				}
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	private int getCloseSound() {
		return this.material == Material.IRON ? 1011 : 1012;
	}

	private int getOpenSound() {
		return this.material == Material.IRON ? 1005 : 1006;
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
		Direction dir = state.get(FACING);
		boolean open = state.get(OPEN);
		DoorHingeSide hinge = state.get(HINGE);
		DoubleBlockHalf half = state.get(HALF);
		BlockState toPlace = this.parent.getDefaultState().with(FACING, dir).with(OPEN, open).with(HINGE, hinge);

		worldIn.setBlockState(pos, toPlace.with(HALF, half), 3);
		worldIn.playSound(entityplayer, pos, SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

		if (half == DoubleBlockHalf.UPPER) {
			worldIn.setBlockState(pos.down(), toPlace.with(HALF, DoubleBlockHalf.LOWER), 3);
		} else {
			worldIn.setBlockState(pos.up(), toPlace.with(HALF, DoubleBlockHalf.UPPER), 3);
		}

		return true;
	}

	private boolean canAccess(IBlockReader worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BaseLockedTileEntity tileentity = (BaseLockedTileEntity) worldIn.getTileEntity(pos);

		if (tileentity != null && tileentity.isLocked()) {
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
