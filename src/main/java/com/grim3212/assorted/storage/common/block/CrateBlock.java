package com.grim3212.assorted.storage.common.block;

import java.util.List;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.api.crates.ICrateSystem;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.ILockable;
import com.grim3212.assorted.storage.common.block.blockentity.INamed;
import com.grim3212.assorted.storage.common.util.CrateLayout;
import com.grim3212.assorted.storage.common.util.Wood;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CrateBlock extends Block implements EntityBlock, ICrateSystem {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	private final CrateLayout layout;
	private final Wood type;

	private static final VoxelShape TOP_SHAPE = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape INSIDE_SHAPE = Block.box(1.0D, 2.0D, 1.0D, 14.0D, 14.0D, 14.0D);
	private static final VoxelShape BOTTOM_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	private static final VoxelShape SIDE_1 = Block.box(0.0D, 2.0D, 14.0D, 2.0D, 14.0D, 16.0D);
	private static final VoxelShape SIDE_2 = Block.box(0.0D, 2.0D, 0.0D, 2.0D, 14.0D, 2.0D);
	private static final VoxelShape SIDE_3 = Block.box(14.0D, 2.0D, 14.0D, 16.0D, 14.0D, 16.0D);
	private static final VoxelShape SIDE_4 = Block.box(14.0D, 2.0D, 0.0D, 16.0D, 14.0D, 2.0D);
	public static final VoxelShape FINAL_SHAPE = Shapes.or(TOP_SHAPE, INSIDE_SHAPE, BOTTOM_SHAPE, SIDE_1, SIDE_2, SIDE_3, SIDE_4);

	private static final VoxelShape TOP_VERTICAL_SHAPE = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape INSIDE_VERTICAL_SHAPE = Block.box(1.0D, 1.0D, 1.0D, 14.0D, 14.0D, 14.0D);
	private static final VoxelShape BOTTOM_VERTICAL_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
	private static final VoxelShape SIDE_VERTICAL_1 = Block.box(0.0D, 0.0D, 2.0D, 2.0D, 2.0D, 14.0D);
	private static final VoxelShape SIDE_VERTICAL_2 = Block.box(0.0D, 14.0D, 2.0D, 2.0D, 16.0D, 14.0D);
	private static final VoxelShape SIDE_VERTICAL_3 = Block.box(14.0D, 0.0D, 2.0D, 16.0D, 2.0D, 14.0D);
	private static final VoxelShape SIDE_VERTICAL_4 = Block.box(14.0D, 14.0D, 2.0D, 16.0D, 16.0D, 14.0D);
	public static final VoxelShape FINAL_VERTICAL_SHAPE = Shapes.or(TOP_VERTICAL_SHAPE, INSIDE_VERTICAL_SHAPE, BOTTOM_VERTICAL_SHAPE, SIDE_VERTICAL_1, SIDE_VERTICAL_2, SIDE_VERTICAL_3, SIDE_VERTICAL_4);

	public CrateBlock(Wood type, CrateLayout layout, Block.Properties props) {
		super(props);

		this.type = type;
		this.layout = layout;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	public CrateLayout getLayout() {
		return layout;
	}

	public Wood getWoodType() {
		return type;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(FACING).getAxis().isHorizontal() ? FINAL_SHAPE : FINAL_VERTICAL_SHAPE;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		// Because of MineCrafts lighting or model rendering it
		// can make these type of models pretty dark and ugly
		// This makes it look a bit better
		return 1;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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

			if (tileentity instanceof CrateBlockEntity crate) {
				for (int slot = 0; slot < crate.getContainerSize(); slot++) {
					Containers.dropContents(worldIn, pos, crate.getLargeItemStack(slot).asItemStacks());
				}
				Containers.dropContents(worldIn, pos, crate.getEnhancements());

				worldIn.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, worldIn, pos, newState, isMoving);
		}
	}

	// Copied from Item
	protected BlockHitResult getPlayerPOVHitResult(Level level, Player player) {
		float f = player.getXRot();
		float f1 = player.getYRot();
		Vec3 vec3 = player.getEyePosition();
		float f2 = Mth.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f3 = Mth.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
		float f4 = -Mth.cos(-f * ((float) Math.PI / 180F));
		float f5 = Mth.sin(-f * ((float) Math.PI / 180F));
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d0 = player.getReachDistance();
		Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
		return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		BlockEntity tileentity = level.getBlockEntity(pos);

		if (player.isCreative() && tileentity instanceof CrateBlockEntity crate) {
			if (!crate.isEmpty()) {
				// Simulate an attack in the instance that we failed
				if (StorageUtil.canAccess(level, pos, player)) {
					BlockHitResult result = getPlayerPOVHitResult(level, player);
					if (crate.attack(player, result)) {
						return false;
					}
				}
			}
		}

		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (StorageUtil.canAccess(level, pos, player)) {
			BlockHitResult result = getPlayerPOVHitResult(level, player);
			BlockEntity tileentity = level.getBlockEntity(pos);
			if (tileentity instanceof CrateBlockEntity crate) {
				crate.attack(player, result);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (StorageUtil.canAccess(worldIn, pos, player)) {
			if (!player.isShiftKeyDown()) {
				BlockEntity tileentity = worldIn.getBlockEntity(pos);
				if (tileentity instanceof CrateBlockEntity crate) {
					return crate.use(player, handIn, hit);
				}
			}

			if (!worldIn.isClientSide) {
				MenuProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
				if (inamedcontainerprovider != null) {
					NetworkHooks.openScreen((ServerPlayer) player, inamedcontainerprovider, pos);
					PiglinAi.angerNearbyPiglins(player, true);
				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
	}

	@Override
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		super.triggerEvent(state, worldIn, pos, id, param);
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction dir) {
		if (getter.getBlockEntity(pos)instanceof CrateBlockEntity crate) {
			return crate.getSignalStrength();
		}

		return 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
		if (worldIn.getBlockEntity(pos)instanceof CrateBlockEntity crate) {
			return crate.getSignalStrength();
		}

		return 0;
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
		return new CrateBlockEntity(pos, state);
	}

	@Override
	public int numSlots(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos)instanceof CrateBlockEntity crate) {
			return crate.getContainerSize();
		}

		return ICrateSystem.super.numSlots(level, pos);
	}
}
