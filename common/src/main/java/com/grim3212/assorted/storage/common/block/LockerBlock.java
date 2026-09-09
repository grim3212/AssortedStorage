package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.api.LockerHalf;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LockerBlock extends BaseStorageBlock {

    public static final EnumProperty<LockerHalf> HALF = EnumProperty.create("half", LockerHalf.class);

    public LockerBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops().strength(3.0F, 6.0F));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(HALF, LockerHalf.SINGLE));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockerBlockEntity(pos, state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction dir, BlockPos neighborPos, BlockState neighborState, RandomSource randomSource) {
        if (neighborState.is(this) && dir.getAxis().isVertical()) {
            LockerHalf half = neighborState.getValue(HALF);
            if (state.getValue(HALF) == LockerHalf.SINGLE && half != LockerHalf.SINGLE && state.getValue(FACING) == neighborState.getValue(FACING) && getConnectedDirection(neighborState) == dir.getOpposite()) {
                return state.setValue(HALF, half.getOpposite());
            }
        } else if (getConnectedDirection(state) == dir) {
            return state.setValue(HALF, LockerHalf.SINGLE);
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, dir, neighborPos, neighborState, randomSource);
    }

    public static Direction getConnectedDirection(BlockState state) {
        return state.getValue(HALF) == LockerHalf.BOTTOM ? Direction.UP : Direction.DOWN;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState upState = level.getBlockState(pos.above());
        if (upState.is(this) && upState.getValue(HALF) == LockerHalf.SINGLE) {
            return this.defaultBlockState().setValue(FACING, upState.getValue(FACING)).setValue(HALF, LockerHalf.BOTTOM).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        BlockState bottomState = level.getBlockState(pos.below());
        if (bottomState.is(this) && bottomState.getValue(HALF) == LockerHalf.SINGLE) {
            return this.defaultBlockState().setValue(FACING, bottomState.getValue(FACING)).setValue(HALF, LockerHalf.TOP).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, LockerHalf.SINGLE).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, HALF, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(HALF) == LockerHalf.TOP)
            return super.getShape(state, worldIn, pos, context);

        return ObsidianSafeBlock.SAFE_SHAPE;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.below()) == state)
            return super.getDestroyProgress(state, player, worldIn, pos.below());

        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    /**
     * {@code onRemove} split in two: this only fires for a real removal, and the block entity is
     * already gone by now - anything that needed it moved onto the block entity's
     * {@code preRemoveSideEffects}.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel worldIn, BlockPos pos, boolean movedByPiston) {
        worldIn.updateNeighbourForOutputSignal(pos, this);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if ((worldIn.getBlockState(pos.below()).getBlock() == this && worldIn.getBlockState(pos.below(2)).getBlock() == this) || (worldIn.getBlockState(pos.above()).getBlock() == this && worldIn.getBlockState(pos.above(2)).getBlock() == this)) {
            return;
        }

        super.setPlacedBy(worldIn, pos, state, placer, stack);

        LockerBlockEntity tileentitythis = (LockerBlockEntity) worldIn.getBlockEntity(pos);
        if (tileentitythis == null) {
            return;
        }

        if (state.getValue(HALF) == LockerHalf.BOTTOM) {
            BlockEntity tileentitytop = worldIn.getBlockEntity(pos.above());
            if (tileentitytop != null && tileentitytop instanceof LockerBlockEntity topLocker) {

                tileentitythis.refreshStorageHandler();
                topLocker.refreshStorageHandler();

                if (topLocker.isLocked()) {
                    tileentitythis.setLockCode(topLocker.getLockCode());
                }
            }
        } else if (state.getValue(HALF) == LockerHalf.TOP) {
            BlockEntity tileEntityBelow = worldIn.getBlockEntity(pos.below());
            if (tileEntityBelow != null && tileEntityBelow instanceof LockerBlockEntity belowLocker) {

                tileentitythis.refreshStorageHandler();
                belowLocker.refreshStorageHandler();

                if (belowLocker.isLocked()) {
                    tileentitythis.setLockCode(belowLocker.getLockCode());
                }
            }
        }
    }

    @Override
    protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LockerBlockEntity lockerBlockEntity) {
            if (worldIn.getBlockEntity(pos.above()) instanceof LockerBlockEntity topLocker) {

                lockerBlockEntity.setLockCode("");
                topLocker.setLockCode("");

                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
                return true;

            } else if (worldIn.getBlockEntity(pos.below()) instanceof LockerBlockEntity bottomLocker) {

                lockerBlockEntity.setLockCode("");
                bottomLocker.setLockCode("");

                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
                return true;
            }
        }

        return super.removeLock(worldIn, pos, entityplayer);
    }

    @Override
    protected boolean placeLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);
        String code = StorageUtil.getCode(itemstack);
        if (code.isEmpty()) {
            return false;
        }

        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LockerBlockEntity lockerBlockEntity) {
            if (worldIn.getBlockEntity(pos.above()) instanceof LockerBlockEntity topLocker) {
                if (!entityplayer.isCreative())
                    itemstack.shrink(1);
                lockerBlockEntity.setLockCode(code);
                topLocker.setLockCode(code);
                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
                return true;

            } else if (worldIn.getBlockEntity(pos.below()) instanceof LockerBlockEntity bottomLocker) {
                if (!entityplayer.isCreative())
                    itemstack.shrink(1);
                lockerBlockEntity.setLockCode(code);
                bottomLocker.setLockCode(code);
                worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
                return true;
            }
        }

        return super.placeLock(worldIn, pos, entityplayer, hand);
    }
}
