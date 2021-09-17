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
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER).setValue(LOCKED, false));
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		boolean isLocked = !this.canBeLocked(worldIn, currentPos);
		DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
		if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
			return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED)).setValue(LOCKED, isLocked) : Blocks.AIR.defaultBlockState();
		} else {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn.setValue(LOCKED, isLocked);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED, LOCKED);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		boolean flag = worldIn.hasNeighborSignal(pos) || worldIn.hasNeighborSignal(pos.relative(state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (blockIn != this && flag != state.getValue(POWERED) && this.canBeLocked(worldIn, pos)) {
			if (flag != state.getValue(OPEN)) {
				this.playSound(worldIn, pos, flag);
			}

			worldIn.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
		}

	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (tryPlaceLock(worldIn, pos, player, handIn))
				return ActionResultType.SUCCESS;
		}

		if (player.isShiftKeyDown() && StorageUtil.canAccess(worldIn, pos, player)) {
			TileEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof BaseLockedTileEntity) {
				BaseLockedTileEntity teStorage = (BaseLockedTileEntity) worldIn.getBlockEntity(pos);

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
	public void setOpen(World worldIn, BlockState state, BlockPos pos, boolean open) {
		if (state.is(this) && state.getValue(OPEN) != open && !state.getValue(LOCKED)) {
			worldIn.setBlock(pos, state.setValue(OPEN, open), 10);
			this.playSound(worldIn, pos, open);
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

	private void playSound(World worldIn, BlockPos pos, boolean isOpening) {
		worldIn.levelEvent((PlayerEntity) null, isOpening ? this.getOpenSound() : this.getCloseSound(), pos, 0);
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

	protected boolean canBeLocked(IWorld worldIn, BlockPos pos) {
		return !((BaseLockedTileEntity) worldIn.getBlockEntity(pos)).isLocked();
	}

	private boolean removeLock(World worldIn, BlockPos pos, PlayerEntity entityplayer) {
		BaseLockedTileEntity tileentity = (BaseLockedTileEntity) worldIn.getBlockEntity(pos);
		tileentity.setLockCode("");
		tileentity.getLevel().playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, tileentity.getLevel().random.nextFloat() * 0.1F + 0.9F);

		BlockState state = worldIn.getBlockState(pos);
		BlockState blockstate = Block.updateFromNeighbourShapes(state, worldIn, pos);
		Block.updateOrDestroy(state, blockstate, worldIn, pos, 3);

		if (worldIn.getBlockState(pos).getValue(HALF) == DoubleBlockHalf.UPPER) {
			BaseLockedTileEntity tileentityLower = (BaseLockedTileEntity) worldIn.getBlockEntity(pos.below());
			if (tileentityLower != null) {
				tileentityLower.setLockCode("");
			}

			BlockState stateLower = worldIn.getBlockState(pos.below());
			BlockState blockstateLower = Block.updateFromNeighbourShapes(stateLower, worldIn, pos.below());
			Block.updateOrDestroy(stateLower, blockstateLower, worldIn, pos.below(), 3);
		} else {
			BaseLockedTileEntity tileentityUpper = (BaseLockedTileEntity) worldIn.getBlockEntity(pos.above());
			if (tileentityUpper != null) {
				tileentityUpper.setLockCode("");
			}

			BlockState stateUpper = worldIn.getBlockState(pos.above());
			BlockState blockstateUpper = Block.updateFromNeighbourShapes(stateUpper, worldIn, pos.above());
			Block.updateOrDestroy(stateUpper, blockstateUpper, worldIn, pos.above(), 3);
		}

		return true;
	}

	private boolean tryPlaceLock(World worldIn, BlockPos pos, PlayerEntity entityplayer, Hand hand) {
		ItemStack itemstack = entityplayer.getItemInHand(hand);

		if (itemstack.hasTag()) {
			String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
			if (!code.isEmpty()) {
				BaseLockedTileEntity tileentity = (BaseLockedTileEntity) worldIn.getBlockEntity(pos);

				if (!entityplayer.isCreative())
					itemstack.shrink(1);
				tileentity.setLockCode(code);
				tileentity.getLevel().playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 0.5F, tileentity.getLevel().random.nextFloat() * 0.1F + 0.9F);

				BlockState state = worldIn.getBlockState(pos);
				BlockState blockstate = Block.updateFromNeighbourShapes(state, worldIn, pos);
				Block.updateOrDestroy(state, blockstate, worldIn, pos, 3);
				if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
					BaseLockedTileEntity tileentityLower = (BaseLockedTileEntity) worldIn.getBlockEntity(pos.below());
					if (tileentityLower != null) {
						tileentityLower.setLockCode(code);
					}

					BlockState stateLower = worldIn.getBlockState(pos.below());
					BlockState blockstateLower = Block.updateFromNeighbourShapes(stateLower, worldIn, pos.below());
					Block.updateOrDestroy(stateLower, blockstateLower, worldIn, pos.below(), 3);

				} else {
					BaseLockedTileEntity tileentityUpper = (BaseLockedTileEntity) worldIn.getBlockEntity(pos.above());
					if (tileentityUpper != null) {
						tileentityUpper.setLockCode(code);
					}

					BlockState stateUpper = worldIn.getBlockState(pos.above());
					BlockState blockstateUpper = Block.updateFromNeighbourShapes(stateUpper, worldIn, pos.above());
					Block.updateOrDestroy(stateUpper, blockstateUpper, worldIn, pos.above(), 3);
				}

				return true;
			}

		}

		return false;
	}
}
