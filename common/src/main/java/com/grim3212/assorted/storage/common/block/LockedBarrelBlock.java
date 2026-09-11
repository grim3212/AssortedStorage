package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedBarrelBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class LockedBarrelBlock extends Block implements EntityBlock, IStorageMaterial {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private final StorageMaterial material;

    public LockedBarrelBlock(StorageMaterial material, Block.Properties props) {
        super(props);
        this.material = material;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    public StorageMaterial getStorageMaterial() {
        return material;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader worldIn, BlockPos pos, BlockState state, boolean includeData) {
        if (this.getStorageMaterial() == null) {
            String lockCode = StorageUtil.getCode(worldIn.getBlockEntity(pos));
            ItemStack output = new ItemStack(StorageBlocks.LOCKED_BARREL.get());
            return StorageUtil.setCodeOnStack(lockCode, output);
        }
        return super.getCloneItemStack(worldIn, pos, state, includeData);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
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
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                ((INamed) tileentity).setCustomName(stack.getHoverName());
            }
        }

        ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);
        if (lockeable != null) {
            lockeable.setLockCode(StorageUtil.getCode(stack));
        }
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
    protected InteractionResult useItemOn(ItemStack heldStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (this.canBeLocked(worldIn, pos) && player.getItemInHand(handIn).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
            if (BaseStorageBlock.tryPlaceLock(worldIn, pos, player, handIn))
                return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown() && StorageAccessUtil.canAccess(worldIn, pos, player)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof ILockable) {
                ILockable teStorage = (ILockable) tileentity;

                if (teStorage.isLocked()) {
                    ItemStack lockStack = StorageUtil.setCodeOnStack(teStorage.getLockCode(), new ItemStack(StorageItems.LOCKSMITH_LOCK.get()));

                    if (removeLock(worldIn, pos, player)) {
                        ItemEntity blockDropped = new ItemEntity(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), lockStack);
                        if (!worldIn.isClientSide()) {
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

        if (StorageAccessUtil.canAccess(worldIn, pos, player)) {
            if (!worldIn.isClientSide()) {
                MenuProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
                if (inamedcontainerprovider != null) {
                    Services.PLATFORM.openMenu((ServerPlayer) player, inamedcontainerprovider);
                    player.awardStat(Stats.OPEN_BARREL);
                    if (worldIn instanceof ServerLevel serverLevel) {
                        PiglinAi.angerNearbyPiglins(serverLevel, player, true);
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    protected MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (level1, blockPos, blockState, t) -> {
            if (t instanceof LockedBarrelBlockEntity storage) {
                storage.tick();
            }
        };
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.triggerEvent(id, param);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos, Direction direction) {
        if (worldIn.getBlockEntity(pos) instanceof BaseStorageBlockEntity storageBlockEntity) {
            return StorageUtil.getRedstoneSignalFromContainer(storageBlockEntity.getItemStackStorageHandler());
        }

        return super.getAnalogOutputSignal(blockState, worldIn, pos, direction);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    protected boolean canBeLocked(Level worldIn, BlockPos pos) {
        return !((ILockable) worldIn.getBlockEntity(pos)).isLocked();
    }

    protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
        if (this.getStorageMaterial() != null) {
            return BaseStorageBlock.tryRemoveLock(worldIn, pos, entityplayer);
        }

        worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);

        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() instanceof LockedBarrelBlock && worldIn.getBlockEntity(pos) instanceof LockedBarrelBlockEntity barrelBE) {
            NonNullList<ItemStack> chestItems = NonNullList.withSize(barrelBE.getItemStackStorageHandler().getSlots(), ItemStack.EMPTY);
            for (int i = 0; i < barrelBE.getItemStackStorageHandler().getSlots(); i++) {
                chestItems.set(i, barrelBE.getItemStackStorageHandler().getStackInSlot(i).copy());
                barrelBE.getItemStackStorageHandler().setStackInSlot(i, ItemStack.EMPTY);
            }
            barrelBE.setLockCode(null);

            worldIn.setBlock(pos, Blocks.BARREL.defaultBlockState().setValue(BarrelBlock.FACING, state.getValue(LockedBarrelBlock.FACING)), 3);
            if (worldIn.getBlockEntity(pos) instanceof BarrelBlockEntity newBarrelBE) {
                for (int i = 0; i < newBarrelBE.getContainerSize(); i++) {
                    newBarrelBE.setItem(i, chestItems.get(i));
                }
            }
        }
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockedBarrelBlockEntity(pos, state);
    }
}