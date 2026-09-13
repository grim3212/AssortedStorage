package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.common.block.blockentity.BaseLockedBlockEntity;
import com.grim3212.assorted.storage.mixin.block.DoorBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class LockedDoorBlock extends DoorBlock implements EntityBlock {

    private final Block parent;

    public LockedDoorBlock(DoorBlock parent, Properties builder) {
        super(((DoorBlockAccessor) parent).getType(), builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
        this.parent = parent;
    }

    public LockedDoorBlock(Identifier parent, BlockSetType type, Properties builder) {
        super(type, builder);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, false).setValue(HALF, DoubleBlockHalf.LOWER));
        this.parent = Services.PLATFORM.getRegistry(Registries.BLOCK).getValue(parent).orElse(Blocks.AIR);
    }

    /** The vanilla door this one stands in for: what it reverts to, drops and is picked as. */
    public Block getParent() {
        return this.parent;
    }

    /** A locked door has no item of its own; it is picked as the door the padlock went on. */
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return this.parent.defaultBlockState().getCloneItemStack(level, pos, includeData);
    }

    /**
     * Keep the block entity, and with it the lock, when one locked door becomes another in place - a
     * copper door oxidising, being waxed, or being scraped back. Every locked door shares the one
     * {@code BASE_LOCKED} block entity type, so the one already there is still the right one. Without
     * this the lock is lost and {@code BaseLockedBlockEntity#preRemoveSideEffects} drops the padlock
     * on every oxidation step.
     */
    @Override
    protected boolean shouldChangedStateKeepBlockEntity(BlockState oldState) {
        return oldState.getBlock() instanceof LockedDoorBlock;
    }

    @Override
    protected void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, Orientation orientation, boolean isMoving) {
        // redstone doesn't work with locked doors
    }

    @Override
    public void setOpen(Entity entity, Level worldIn, BlockState state, BlockPos pos, boolean open) {
        // AI can't open locked doors
    }

    @Override
    protected BlockState updateShape(BlockState stateIn, LevelReader worldIn, ScheduledTickAccess scheduledTickAccess, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource randomSource) {
        DoubleBlockHalf doubleblockhalf = stateIn.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && doubleblockhalf == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {

            // A copper door oxidises, waxes and scrapes one half at a time, so the other half follows
            // the half that changed - which is what vanilla's DoorBlock does for its own copper doors.
            // Only for a neighbour that is a *different* locked door: putting a padlock on a door and
            // taking it off again both pass through a half that has not been converted yet, and those
            // must fall through to the tolerance below rather than adopt.
            if (facingState.getBlock() instanceof LockedDoorBlock && !facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf) {
                return facingState.setValue(HALF, doubleblockhalf);
            }

            boolean isValidBlock = doubleblockhalf == DoubleBlockHalf.UPPER ? worldIn.getBlockState(currentPos.below()).getBlock() instanceof DoorBlock : worldIn.getBlockState(currentPos.above()).getBlock() instanceof DoorBlock;

            return facingState.is(this) && facingState.getValue(HALF) != doubleblockhalf ? stateIn.setValue(FACING, facingState.getValue(FACING)).setValue(OPEN, facingState.getValue(OPEN)).setValue(HINGE, facingState.getValue(HINGE)).setValue(POWERED, facingState.getValue(POWERED)) : isValidBlock ? stateIn : Blocks.AIR.defaultBlockState();
        } else {
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.isShiftKeyDown() && StorageAccessUtil.canAccess(worldIn, pos, player)) {
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

        if (StorageAccessUtil.canAccess(worldIn, pos, player)) {
            state = state.cycle(OPEN);
            worldIn.setBlock(pos, state, 10);
            this.playSound(player, worldIn, pos, state.getValue(OPEN));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    private void playSound(@Nullable Entity entity, Level level, BlockPos pos, boolean isOpen) {
        level.playSound(entity, pos, isOpen ? ((DoorBlockAccessor) this).getType().doorOpen() : ((DoorBlockAccessor) this).getType().doorClose(), SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof BaseLockedBlockEntity) {
            BaseLockedBlockEntity tileentity = (BaseLockedBlockEntity) te;

            if (tileentity.isLocked() && !StorageAccessUtil.canAccess(worldIn, pos, player))
                return -1.0F;
        }

        return super.getDestroyProgress(state, player, worldIn, pos);
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
        worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);

        if (half == DoubleBlockHalf.UPPER) {
            worldIn.setBlock(pos.below(), toPlace.setValue(HALF, DoubleBlockHalf.LOWER), 3);
        } else {
            worldIn.setBlock(pos.above(), toPlace.setValue(HALF, DoubleBlockHalf.UPPER), 3);
        }

        return true;
    }
}
