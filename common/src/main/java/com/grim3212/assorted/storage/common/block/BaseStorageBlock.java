package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class BaseStorageBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final VoxelShape FAKE_SIDES_AND_BOTTOM = Block.box(0.01D, 0.01D, 0.01D, 16.0D, 16.0D, 16.0D);

    public BaseStorageBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (context == CollisionContext.empty())
            return FAKE_SIDES_AND_BOTTOM;

        return super.getShape(state, worldIn, pos, context);
    }

    protected boolean isInvalidBlock(LevelAccessor world, BlockPos pos) {
        return !world.isEmptyBlock(pos) && world.getBlockState(pos).isSolidRender(world, pos);
    }

    protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
        return isInvalidBlock(world, pos.relative(world.getBlockState(pos).getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
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
        Direction direction = context.getHorizontalDirection().getOpposite();
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

        return this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof ILockable) {
            ILockable tileentity = (ILockable) te;

            if (tileentity.isLocked() && !StorageAccessUtil.canAccess(worldIn, pos, player))
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
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);

            if (tileentity instanceof BaseStorageBlockEntity storageBlockEntity) {

                if (storageBlockEntity.isLocked()) {
                    ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
                    CompoundTag tag = new CompoundTag();
                    StorageUtil.writeLock(tag, storageBlockEntity.getLockCode());
                    lockStack.setTag(tag);
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
                }

                StorageUtil.dropContents(worldIn, pos, storageBlockEntity.getItemStackStorageHandler());
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
            if (this.placeLock(worldIn, pos, player, handIn))
                return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown() && StorageAccessUtil.canAccess(worldIn, pos, player)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ILockable) {
                ILockable teStorage = (ILockable) tileentity;

                if (teStorage.isLocked()) {
                    ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
                    CompoundTag tag = new CompoundTag();
                    StorageUtil.writeLock(tag, teStorage.getLockCode());
                    lockStack.setTag(tag);

                    if (removeLock(worldIn, pos, player)) {
                        ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), lockStack);
                        if (!worldIn.isClientSide) {
                            worldIn.addFreshEntity(blockDropped);
                            if (!Services.PLATFORM.isFakePlayer(player)) {
                                blockDropped.playerTouch(player);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if (!isDoorBlocked(worldIn, pos) && StorageAccessUtil.canAccess(worldIn, pos, player)) {
            if (!worldIn.isClientSide) {
                MenuProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
                if (inamedcontainerprovider != null) {
                    this.openMenu(player, inamedcontainerprovider, pos);
                    player.awardStat(this.getOpenStat());
                    PiglinAi.angerNearbyPiglins(player, true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected void openMenu(Player player, MenuProvider provider, BlockPos pos) {
        Services.PLATFORM.openMenu((ServerPlayer) player, provider, byteBuf -> byteBuf.writeBlockPos(pos));
    }

    // TODO: Create custom stat for this
    protected Stat<ResourceLocation> getOpenStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof BaseStorageBlockEntity storage) {
                storage.tick();
            }
        };
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.triggerEvent(id, param);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        if (worldIn.getBlockEntity(pos) instanceof BaseStorageBlockEntity storageBlockEntity) {
            return StorageUtil.getRedstoneSignalFromContainer(storageBlockEntity.getItemStackStorageHandler());
        }

        return super.getAnalogOutputSignal(blockState, worldIn, pos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    protected boolean canBeLocked(Level worldIn, BlockPos pos) {
        return !((ILockable) worldIn.getBlockEntity(pos)).isLocked();
    }

    protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
        return tryRemoveLock(worldIn, pos, entityplayer);
    }

    public static boolean tryRemoveLock(Level worldIn, BlockPos pos, Player entityplayer) {
        ILockable tileentity = (ILockable) worldIn.getBlockEntity(pos);
        tileentity.setLockCode("");
        worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
        return true;
    }

    protected boolean placeLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        return tryPlaceLock(worldIn, pos, entityplayer, hand);
    }

    public static boolean tryPlaceLock(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);
        String code = StorageUtil.getCode(itemstack);
        if (code.isEmpty()) {
            return false;
        }

        BlockEntity tileentity = worldIn.getBlockEntity(pos);

        if (tileentity instanceof ILockable lockable) {
            if (!entityplayer.isCreative())
                itemstack.shrink(1);
            lockable.setLockCode(code);
            worldIn.playSound(entityplayer, tileentity.getBlockPos(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
            return true;
        }

        return false;
    }
}