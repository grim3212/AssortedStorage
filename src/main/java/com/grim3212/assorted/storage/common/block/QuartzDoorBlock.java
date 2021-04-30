package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.block.tileentity.BaseLockedTileEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class QuartzDoorBlock extends DoorBlock {

	public static final BooleanProperty LOCKED = BooleanProperty.create("locked");

	public QuartzDoorBlock(Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, false).with(HINGE, DoorHingeSide.LEFT).with(POWERED, false).with(HALF, DoubleBlockHalf.LOWER).with(LOCKED, false));
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		boolean isLocked = !this.canBeLocked(worldIn, currentPos);
		DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.matchesBlock(this) && facingState.get(HALF) != doubleblockhalf ? stateIn.with(FACING, facingState.get(FACING)).with(OPEN, facingState.get(OPEN)).with(HINGE, facingState.get(HINGE)).with(POWERED, facingState.get(POWERED)).with(LOCKED, isLocked) : Blocks.AIR.getDefaultState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn.with(LOCKED, isLocked);
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED, LOCKED);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (blockIn != this && flag != state.get(POWERED) && this.canBeLocked(worldIn, pos)) {
			if (flag != state.get(OPEN)) {
				this.playSound(worldIn, pos, flag);
			}

			worldIn.setBlockState(pos, state.with(POWERED, flag).with(OPEN, flag), 2);
		}

	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getHeldItem(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (tryPlaceLock(worldIn, pos, player, handIn))
				return ActionResultType.SUCCESS;
		}

		if (player.isSneaking() && this.canAccess(worldIn, pos, player)) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof BaseLockedTileEntity) {
				BaseLockedTileEntity teStorage = (BaseLockedTileEntity) worldIn.getTileEntity(pos);

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
	public void openDoor(World worldIn, BlockState state, BlockPos pos, boolean open) {
		if (state.matchesBlock(this) && state.get(OPEN) != open && !state.get(LOCKED)) {
			worldIn.setBlockState(pos, state.with(OPEN, open), 10);
			this.playSound(worldIn, pos, open);
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

	private void playSound(World worldIn, BlockPos pos, boolean isOpening) {
		worldIn.playEvent((PlayerEntity) null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
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

	protected boolean canBeLocked(IWorld worldIn, BlockPos pos) {
		return !((BaseLockedTileEntity) worldIn.getTileEntity(pos)).isLocked();
	}

	private boolean removeLock(World worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BaseLockedTileEntity tileentity = (BaseLockedTileEntity) worldIn.getTileEntity(pos);
		tileentity.setLockCode("");
		tileentity.getWorld().playSound(entityplayer, tileentity.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, tileentity.getWorld().rand.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		BlockState blockstate = Block.getValidBlockForPosition(state, worldIn, pos);
		Block.replaceBlock(state, blockstate, worldIn, pos, 3);

		if (worldIn.getBlockState(pos).get(HALF) == DoubleBlockHalf.UPPER) {
			BaseLockedTileEntity tileentityLower = (BaseLockedTileEntity) worldIn.getTileEntity(pos.down());
			if (tileentityLower != null) {
				tileentityLower.setLockCode("");
			}

			BlockState stateLower = worldIn.getBlockState(pos.down());
			BlockState blockstateLower = Block.getValidBlockForPosition(stateLower, worldIn, pos.down());
			Block.replaceBlock(stateLower, blockstateLower, worldIn, pos.down(), 3);
		} else {
			BaseLockedTileEntity tileentityUpper = (BaseLockedTileEntity) worldIn.getTileEntity(pos.up());
			if (tileentityUpper != null) {
				tileentityUpper.setLockCode("");
			}

			BlockState stateUpper = worldIn.getBlockState(pos.up());
			BlockState blockstateUpper = Block.getValidBlockForPosition(stateUpper, worldIn, pos.up());
			Block.replaceBlock(stateUpper, blockstateUpper, worldIn, pos.up(), 3);
		}

		return true;
	}

	private boolean tryPlaceLock(World worldIn, BlockPos pos, PlayerEntity entityplayer, Hand hand) {
		ItemStack itemstack = entityplayer.getHeldItem(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				BaseLockedTileEntity tileentity = (BaseLockedTileEntity) worldIn.getTileEntity(pos);

				if (!entityplayer.isCreative())
					itemstack.shrink(1);
				tileentity.setLockCode(code);
				tileentity.getWorld().playSound(entityplayer, tileentity.getPos(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, tileentity.getWorld().rand.nextFloat() * 0.1F + 0.9F);

				BlockState state = worldIn.getBlockState(pos);
				BlockState blockstate = Block.getValidBlockForPosition(state, worldIn, pos);
				Block.replaceBlock(state, blockstate, worldIn, pos, 3);
				if (state.get(HALF) == DoubleBlockHalf.UPPER) {
					BaseLockedTileEntity tileentityLower = (BaseLockedTileEntity) worldIn.getTileEntity(pos.down());
					if (tileentityLower != null) {
						tileentityLower.setLockCode(code);
					}

					BlockState stateLower = worldIn.getBlockState(pos.down());
					BlockState blockstateLower = Block.getValidBlockForPosition(stateLower, worldIn, pos.down());
					Block.replaceBlock(stateLower, blockstateLower, worldIn, pos.down(), 3);

				} else {
					BaseLockedTileEntity tileentityUpper = (BaseLockedTileEntity) worldIn.getTileEntity(pos.up());
					if (tileentityUpper != null) {
						tileentityUpper.setLockCode(code);
					}

					BlockState stateUpper = worldIn.getBlockState(pos.up());
					BlockState blockstateUpper = Block.getValidBlockForPosition(stateUpper, worldIn, pos.up());
					Block.replaceBlock(stateUpper, blockstateUpper, worldIn, pos.up(), 3);
				}

				return true;
			}

		}

		return false;
	}

	private boolean canAccess(IBlockReader worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BaseLockedTileEntity tileentity = (BaseLockedTileEntity) worldIn.getTileEntity(pos);

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
