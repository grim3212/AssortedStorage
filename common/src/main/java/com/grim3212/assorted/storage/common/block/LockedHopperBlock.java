package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import com.grim3212.assorted.storage.common.block.blockentity.LockedHopperBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LockedHopperBlock extends HopperBlock implements IStorageMaterial {

    private final StorageMaterial material;

    public LockedHopperBlock(StorageMaterial material) {
        this(material, material.getProps());
    }

    public LockedHopperBlock(StorageMaterial material, Block.Properties props) {
        super(props);
        this.material = material;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(ENABLED, true));
    }

    @Override
    public StorageMaterial getStorageMaterial() {
        return material;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        if (this.getStorageMaterial() == null) {
            String lockCode = StorageUtil.getCode(worldIn.getBlockEntity(pos));
            ItemStack output = new ItemStack(StorageBlocks.LOCKED_HOPPER.get());
            return StorageUtil.setCodeOnStack(lockCode, output);
        }
        return super.getCloneItemStack(worldIn, pos, state);
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

        ILockable lockeable = (ILockable) worldIn.getBlockEntity(pos);
        if (lockeable != null) {
            lockeable.setLockCode(StorageUtil.getCode(stack));
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);

            if (tileentity instanceof LockedHopperBlockEntity hopperBlockEntity) {

                if (hopperBlockEntity.isLocked()) {
                    ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
                    CompoundTag tag = new CompoundTag();
                    StorageUtil.writeLock(tag, hopperBlockEntity.getLockCode());
                    lockStack.setTag(tag);
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
                }

                StorageUtil.dropContents(worldIn, pos, hopperBlockEntity.getItemStackStorageHandler());
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

        if (StorageAccessUtil.canAccess(worldIn, pos, player)) {
            if (!worldIn.isClientSide) {
                MenuProvider inamedcontainerprovider = this.getMenuProvider(state, worldIn, pos);
                if (inamedcontainerprovider != null) {
                    Services.PLATFORM.openMenu((ServerPlayer) player, inamedcontainerprovider, byteBuf -> {
                        StorageMaterial.write(byteBuf, this.material);
                        byteBuf.writeBlockPos(pos);
                    });
                    player.awardStat(Stats.INSPECT_HOPPER);
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, StorageBlockEntityTypes.LOCKED_HOPPER.get(), LockedHopperBlockEntity::pushItemsTick);
    }

    protected boolean canBeLocked(Level worldIn, BlockPos pos) {
        return !((ILockable) worldIn.getBlockEntity(pos)).isLocked();
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        String code = StorageUtil.getCode(stack);

        if (!code.isEmpty()) {
            tooltip.add(Component.translatable(Constants.MOD_ID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
        }

        tooltip.add(Component.translatable(Constants.MOD_ID + ".info.level_upgrade_level", Component.literal("" + (material == null ? 0 : material.getStorageLevel())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
    }

    protected boolean removeLock(Level worldIn, BlockPos pos, Player entityplayer) {
        if (this.getStorageMaterial() != null) {
            return BaseStorageBlock.tryRemoveLock(worldIn, pos, entityplayer);
        }

        worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);

        BlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() instanceof LockedHopperBlock && worldIn.getBlockEntity(pos) instanceof LockedHopperBlockEntity hopperBE) {
            NonNullList<ItemStack> chestItems = NonNullList.withSize(hopperBE.getItemStackStorageHandler().getSlots(), ItemStack.EMPTY);
            for (int i = 0; i < hopperBE.getItemStackStorageHandler().getSlots(); i++) {
                chestItems.set(i, hopperBE.getItemStackStorageHandler().getStackInSlot(i).copy());
                hopperBE.getItemStackStorageHandler().setStackInSlot(i, ItemStack.EMPTY);
            }
            hopperBE.setLockCode(null);

            worldIn.setBlock(pos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, state.getValue(LockedHopperBlock.FACING)), 3);
            if (worldIn.getBlockEntity(pos) instanceof HopperBlockEntity newHopperBE) {
                for (int i = 0; i < newHopperBE.getContainerSize(); i++) {
                    newHopperBE.setItem(i, chestItems.get(i));
                }
            }
        }
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockedHopperBlockEntity(pos, state);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof LockedHopperBlockEntity) {
            LockedHopperBlockEntity.entityInside(level, pos, state, entity, (LockedHopperBlockEntity) blockentity);
        }
    }
}