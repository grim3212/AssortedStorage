package com.grim3212.assorted.storage.common.block;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.api.crates.ICrateSystem;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.ILockable;
import com.grim3212.assorted.storage.common.block.blockentity.INamed;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;

public class CrateControllerBlock extends Block implements EntityBlock, ICrateSystem {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public CrateControllerBlock(Block.Properties props) {
		super(props);

		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);

		if (te instanceof ILockable) {
			ILockable tileentity = (ILockable) te;

			if (tileentity.isLocked() && !StorageUtil.canAccess(worldIn, pos, player))
				return -1.0F;
		}

		return super.getDestroyProgress(state, player, worldIn, pos);
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);

		if (tileentity instanceof INamed) {
			if (stack.hasCustomHoverName()) {
				((INamed) tileentity).setCustomName(stack.getHoverName());
			}
		}

		ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);
		if (lockeable != null) {
			lockeable.setLockCode(StorageUtil.getCode(stack));
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if (tileentity instanceof CrateControllerBlockEntity crate) {
				worldIn.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
			if (BaseStorageBlock.tryPlaceLock(worldIn, pos, player, handIn))
				return InteractionResult.SUCCESS;
		}

		if (StorageUtil.canAccess(worldIn, pos, player)) {
			if (player.isShiftKeyDown()) {
				BlockEntity tileentity = worldIn.getBlockEntity(pos);
				if (tileentity instanceof ILockable) {
					ILockable teStorage = (ILockable) tileentity;

					if (teStorage.isLocked()) {
						ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
						CompoundTag tag = new CompoundTag();
						new StorageLockCode(teStorage.getLockCode()).write(tag);
						lockStack.setTag(tag);

						if (BaseStorageBlock.tryRemoveLock(worldIn, pos, player)) {
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

			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if (tileentity instanceof CrateControllerBlockEntity controller) {
				return controller.use(player, handIn);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.isClientSide)
			return;

		if (level.getBlockEntity(pos)instanceof CrateControllerBlockEntity controller) {
			controller.tick();
			level.scheduleTick(pos, this, 100);
		}
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	protected boolean canBeLocked(Level worldIn, BlockPos pos) {
		return !((ILockable) worldIn.getBlockEntity(pos)).isLocked();
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		String code = StorageUtil.getCode(stack);

		if (!code.isEmpty()) {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrateControllerBlockEntity(pos, state);
	}
}
