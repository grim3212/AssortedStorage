package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.LockedItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.LockedStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.inventory.LockedHopperContainer;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.inventory.StorageItemStackStorageHandler;
import com.grim3212.assorted.storage.common.properties.StorageModelProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LockedHopperBlockEntity extends BaseStorageBlockEntity implements IBlockEntityWithModelData {

    private int cooldownTime = -1;
    private long tickedGameTime;

    private final StorageMaterial storageMaterial;

    public LockedHopperBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_HOPPER.get(), pos, state);

        if (state.getBlock() instanceof LockedHopperBlock lockedHopper) {
            this.storageMaterial = lockedHopper.getStorageMaterial();
        } else {
            // Default to regular hopper
            this.storageMaterial = null;
        }

        this.setStorageHandler(new ItemHandler(this));
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return new LockedHopperContainer(StorageContainerTypes.HOPPERS.get(storageMaterial).get(), windowId, player, this.getItemStackStorageHandler(), storageMaterial);
    }

    public static int getHopperCooldown(StorageMaterial storageMaterial) {
        return storageMaterial == null ? HopperBlockEntity.MOVE_ITEM_SPEED : storageMaterial.hopperCooldown();
    }

    @Override
    protected Component getDefaultName() {
        if (this.storageMaterial == null) {
            return Component.translatable(Constants.MOD_ID + ".container.locked_hopper");
        }

        return Component.translatable(Constants.MOD_ID + ".container.hopper_" + this.storageMaterial.toString());
    }

    @Override
    public @NotNull IBlockModelData getBlockModelData() {
        return IModelDataBuilder.create().withInitial(StorageModelProperties.IS_LOCKED, this.isLocked()).build();
    }

    @Override
    protected SoundEvent openSound() {
        return null;
    }

    @Override
    protected SoundEvent closeSound() {
        return null;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        this.cooldownTime = nbt.getInt("TransferCooldown");
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("TransferCooldown", this.cooldownTime);
    }

    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, LockedHopperBlockEntity hopperBE) {
        --hopperBE.cooldownTime;
        hopperBE.tickedGameTime = level.getGameTime();
        if (!hopperBE.isOnCooldown()) {
            hopperBE.setCooldown(0);
            tryMoveItems(level, pos, state, hopperBE, () -> {
                return suckInItems(level, hopperBE);
            });
        }
    }

    private static boolean tryMoveItems(Level level, BlockPos pos, BlockState state, LockedHopperBlockEntity hopperBE, BooleanSupplier supplier) {
        if (level.isClientSide) {
            return false;
        } else {
            if (!hopperBE.isOnCooldown() && state.getValue(LockedHopperBlock.ENABLED)) {
                boolean flag = false;
                if (!hopperBE.getItemStackStorageHandler().isEmpty()) {
                    flag = ejectItems(level, pos, state, hopperBE);
                }

                if (!hopperBE.inventoryFull()) {
                    flag |= supplier.getAsBoolean();
                }

                if (flag) {
                    hopperBE.setCooldown(getHopperCooldown(hopperBE.storageMaterial));
                    setChanged(level, pos, state);
                    return true;
                }
            }

            return false;
        }
    }

    private boolean inventoryFull() {
        for (ItemStack itemstack : this.getItemStackStorageHandler().getStacks()) {
            if (itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private static boolean isEmpty(IItemStorageHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0) {
                return false;
            }
        }
        return true;
    }

    private static Optional<Pair<IItemStorageHandler, Object>> getItemHandler(Level level, LockedHopperBlockEntity hopper, Direction hopperFacing) {
        double x = hopper.getLevelX() + (double) hopperFacing.getStepX();
        double y = hopper.getLevelY() + (double) hopperFacing.getStepY();
        double z = hopper.getLevelZ() + (double) hopperFacing.getStepZ();
        return getItemHandler(level, x, y, z, hopperFacing.getOpposite());
    }

    private static Optional<Pair<IItemStorageHandler, Object>> getItemHandler(Level worldIn, double x, double y, double z, final Direction side) {
        int i = Mth.floor(x);
        int j = Mth.floor(y);
        int k = Mth.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState state = worldIn.getBlockState(blockpos);

        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
            if (blockEntity != null) {
                return Services.INVENTORY.getItemStorageHandler(blockEntity, side).map(storageHandler -> ImmutablePair.of(storageHandler, blockEntity));
            }
        }

        return Optional.empty();
    }

    private static boolean isFull(IItemStorageHandler itemHandler) {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot)) {
                return false;
            }
        }
        return true;
    }

    private static ItemStack putStackInInventoryAllSlots(LockedHopperBlockEntity source, Object destination, IItemStorageHandler destInventory, ItemStack stack) {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++) {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    private static ItemStack insertStack(LockedHopperBlockEntity source, Object destination, IItemStorageHandler destInventory, ItemStack stack, int slot) {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if (destInventory.insertItem(slot, stack, true).isEmpty()) {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty()) {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            } else if (Services.INVENTORY.canItemStacksStack(itemstack, stack)) {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if (insertedItem) {
                if (inventoryWasEmpty) {
                    if (destination instanceof LockedHopperBlockEntity hopperBE) {
                        if (!hopperBE.isOnCustomCooldown()) {
                            int k = 0;
                            if (source instanceof LockedHopperBlockEntity) {
                                if (hopperBE.getLastUpdateTime() >= ((LockedHopperBlockEntity) source).getLastUpdateTime()) {
                                    k = 1;
                                }
                            }
                            hopperBE.setCooldown(getHopperCooldown(hopperBE.storageMaterial) - k);
                        }
                    }
                }
            }
        } else if (destInventory instanceof LockedStorageHandler lockedInv) {
            if (lockedInv.insertItem(slot, stack, true, source.getLockCode(), false).isEmpty()) {
                boolean insertedItem = false;
                boolean inventoryWasEmpty = isEmpty(destInventory);

                if (itemstack.isEmpty()) {
                    lockedInv.insertItem(slot, stack, false, source.getLockCode(), false);
                    stack = ItemStack.EMPTY;
                    insertedItem = true;
                } else if (Services.INVENTORY.canItemStacksStack(itemstack, stack)) {
                    int originalSize = stack.getCount();
                    stack = lockedInv.insertItem(slot, stack, false, source.getLockCode(), false);
                    insertedItem = originalSize < stack.getCount();
                }

                if (insertedItem) {
                    if (inventoryWasEmpty) {
                        if (destination instanceof LockedHopperBlockEntity hopperBE) {
                            if (!hopperBE.isOnCustomCooldown()) {
                                int k = 0;
                                if (source instanceof LockedHopperBlockEntity) {
                                    if (hopperBE.getLastUpdateTime() >= ((LockedHopperBlockEntity) source).getLastUpdateTime()) {
                                        k = 1;
                                    }
                                }
                                hopperBE.setCooldown(getHopperCooldown(hopperBE.storageMaterial) - k);
                            }
                        }
                    }
                }
            }
        }

        return stack;
    }

    private static boolean insertHook(LockedHopperBlockEntity hopper) {
        Direction hopperFacing = hopper.getBlockState().getValue(LockedHopperBlock.FACING);
        return getItemHandler(hopper.getLevel(), hopper, hopperFacing).map(destinationResult -> {
            IItemStorageHandler itemHandler = destinationResult.getKey();
            Object destination = destinationResult.getValue();
            if (isFull(itemHandler)) {
                return false;
            } else {
                LockedItemStackStorageHandler storageHandler = hopper.getItemStackStorageHandler();
                for (int i = 0; i < storageHandler.getSlots(); ++i) {
                    if (!storageHandler.getStackInSlot(i).isEmpty()) {
                        ItemStack originalSlotContents = storageHandler.getStackInSlot(i).copy();
                        ItemStack insertStack = storageHandler.extractItem(i, 1, false, "", true);
                        ItemStack remainder = putStackInInventoryAllSlots(hopper, destination, itemHandler, insertStack);

                        if (remainder.isEmpty()) {
                            return true;
                        }

                        storageHandler.setStackInSlot(i, originalSlotContents);
                    }
                }

                return false;
            }
        }).orElse(false);
    }

    @Nullable
    public static Boolean extractHook(Level level, LockedHopperBlockEntity dest) {
        return getItemHandler(level, dest, Direction.UP).map(itemHandlerResult -> {
            IItemStorageHandler handler = itemHandlerResult.getKey();
            LockedItemStackStorageHandler storageHandler = dest.getItemStackStorageHandler();

            if (handler instanceof LockedStorageHandler lockedItemHandler) {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack extractItem = lockedItemHandler.extractItem(i, 1, true, dest.getLockCode(), false);
                    if (!extractItem.isEmpty()) {
                        for (int j = 0; j < storageHandler.getSlots(); j++) {
                            ItemStack destStack = storageHandler.getStackInSlot(j);
                            if (storageHandler.isItemValid(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < storageHandler.getSlotLimit(j) && Services.INVENTORY.canItemStacksStack(extractItem, destStack))) {
                                extractItem = lockedItemHandler.extractItem(i, 1, false, dest.getLockCode(), false);
                                if (destStack.isEmpty())
                                    storageHandler.setStackInSlot(j, extractItem);
                                else {
                                    destStack.grow(1);
                                    storageHandler.setStackInSlot(j, destStack);
                                }
                                dest.setChanged();
                                return true;
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack extractItem = handler.extractItem(i, 1, true);
                    if (!extractItem.isEmpty()) {
                        for (int j = 0; j < storageHandler.getSlots(); j++) {
                            ItemStack destStack = storageHandler.getStackInSlot(j);
                            if (storageHandler.isItemValid(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < storageHandler.getSlotLimit(j) && Services.INVENTORY.canItemStacksStack(extractItem, destStack))) {
                                extractItem = handler.extractItem(i, 1, false);
                                if (destStack.isEmpty())
                                    storageHandler.setStackInSlot(j, extractItem);
                                else {
                                    destStack.grow(1);
                                    storageHandler.setStackInSlot(j, destStack);
                                }
                                dest.setChanged();
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }).orElse(null);
    }

    private static boolean ejectItems(Level level, BlockPos pos, BlockState state, LockedHopperBlockEntity hopper) {
        if (insertHook(hopper))
            return true;
        IItemStorageHandler container = getAttachedContainer(level, pos, state);
        if (container == null) {
            return false;
        } else {
            Direction direction = state.getValue(LockedHopperBlock.FACING).getOpposite();
            if (isFullContainer(container)) {
                return false;
            } else {
                LockedItemStackStorageHandler storageHandler = hopper.getItemStackStorageHandler();
                for (int i = 0; i < storageHandler.getSlots(); ++i) {
                    if (!storageHandler.getStackInSlot(i).isEmpty()) {
                        ItemStack itemstack = storageHandler.getStackInSlot(i).copy();
                        ItemStack itemstack1 = addItem(hopper.getItemStackStorageHandler(), container, storageHandler.extractItem(i, 1, false), direction);
                        if (itemstack1.isEmpty()) {
                            container.onContentsChanged(i);
                            return true;
                        }

                        storageHandler.setStackInSlot(i, itemstack);
                    }
                }

                return false;
            }
        }
    }

    private static IntStream getSlots(IItemStorageHandler container) {
        return IntStream.range(0, container.getSlots());
    }

    private static boolean isFullContainer(IItemStorageHandler container) {
        return getSlots(container).allMatch((p_59379_) -> {
            ItemStack itemstack = container.getStackInSlot(p_59379_);
            return itemstack.getCount() >= itemstack.getMaxStackSize();
        });
    }

    private static boolean isEmptyContainer(IItemStorageHandler container) {
        return getSlots(container).allMatch((p_59319_) -> {
            return container.getStackInSlot(p_59319_).isEmpty();
        });
    }

    public static boolean suckInItems(Level level, LockedHopperBlockEntity hopper) {
        Boolean ret = extractHook(level, hopper);
        if (ret != null)
            return ret;
        IItemStorageHandler container = getSourceContainer(level, hopper);
        if (container != null) {
            Direction direction = Direction.DOWN;
            return isEmptyContainer(container) ? false : getSlots(container).anyMatch((p_59363_) -> {
                return tryTakeInItemFromSlot(hopper.getItemStackStorageHandler(), container, p_59363_, direction);
            });
        } else {
            for (ItemEntity itementity : getItemsAtAndAbove(level, hopper)) {
                if (addItem(hopper.getItemStackStorageHandler(), itementity)) {
                    return true;
                }
            }

            return false;
        }
    }

    private static boolean tryTakeInItemFromSlot(IItemStorageHandler self, IItemStorageHandler dest, int slot, Direction dir) {
        ItemStack itemstack = dest.getStackInSlot(slot);
        if (!itemstack.isEmpty() && dest.extractItem(slot, 1, true) != ItemStack.EMPTY) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = addItem(dest, self, dest.extractItem(slot, 1, false), (Direction) null);
            if (itemstack2.isEmpty()) {
                return true;
            }

            dest.setStackInSlot(slot, itemstack1);
        }

        return false;
    }

    public static boolean addItem(IItemStorageHandler p_59332_, ItemEntity p_59333_) {
        boolean flag = false;
        ItemStack itemstack = p_59333_.getItem().copy();
        ItemStack itemstack1 = addItem((IItemStorageHandler) null, p_59332_, itemstack, (Direction) null);
        if (itemstack1.isEmpty()) {
            flag = true;
            p_59333_.discard();
        } else {
            p_59333_.setItem(itemstack1);
        }

        return flag;
    }

    public static ItemStack addItem(@Nullable IItemStorageHandler self, IItemStorageHandler dest, ItemStack stack, @Nullable Direction dir) {
        int i = dest.getSlots();

        for (int j = 0; j < i && !stack.isEmpty(); ++j) {
            stack = tryMoveInItem(self, dest, stack, j, dir);
        }

        return stack;
    }

    private static ItemStack tryMoveInItem(@Nullable IItemStorageHandler self, IItemStorageHandler dest, ItemStack stack, int slot, @Nullable Direction dir) {
        ItemStack itemstack = dest.getStackInSlot(slot);
        if (dest.isItemValid(slot, stack)) {
            boolean flag = false;
            boolean flag1 = dest.isEmpty();
            if (itemstack.isEmpty()) {
                dest.setStackInSlot(slot, stack);
                stack = ItemStack.EMPTY;
                flag = true;
            } else if (canMergeItems(itemstack, stack)) {
                int i = stack.getMaxStackSize() - itemstack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemstack.grow(j);
                flag = j > 0;
            }

            if (flag) {
                if (flag1) {
                    if (dest instanceof LockedHopperBlockEntity) {
                        LockedHopperBlockEntity hopperblockentity1 = (LockedHopperBlockEntity) dest;
                        if (!hopperblockentity1.isOnCustomCooldown()) {
                            int k = 0;
                            if (self instanceof LockedHopperBlockEntity) {
                                LockedHopperBlockEntity hopperblockentity = (LockedHopperBlockEntity) self;
                                if (hopperblockentity1.tickedGameTime >= hopperblockentity.tickedGameTime) {
                                    k = 1;
                                }
                            }

                            hopperblockentity1.setCooldown(getHopperCooldown(hopperblockentity1.storageMaterial) - k);
                        }
                    }
                }
            }
        }

        return stack;
    }

    @Nullable
    private static IItemStorageHandler getAttachedContainer(Level p_155593_, BlockPos p_155594_, BlockState p_155595_) {
        Direction direction = p_155595_.getValue(LockedHopperBlock.FACING);
        return getContainerAt(p_155593_, p_155594_, direction);
    }

    @Nullable
    private static IItemStorageHandler getSourceContainer(Level p_155597_, LockedHopperBlockEntity p_155598_) {
        return getContainerAt(p_155597_, p_155598_.getLevelX(), p_155598_.getLevelY() + 1.0D, p_155598_.getLevelZ(), null);
    }

    public static List<ItemEntity> getItemsAtAndAbove(Level p_155590_, LockedHopperBlockEntity p_155591_) {
        return p_155591_.getSuckShape().toAabbs().stream().flatMap((p_155558_) -> {
            return p_155590_.getEntitiesOfClass(ItemEntity.class, p_155558_.move(p_155591_.getLevelX() - 0.5D, p_155591_.getLevelY() - 0.5D, p_155591_.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
        }).collect(Collectors.toList());
    }

    @Nullable
    public static IItemStorageHandler getContainerAt(Level p_59391_, BlockPos p_59392_, @Nullable Direction direction) {
        return getContainerAt(p_59391_, (double) p_59392_.getX() + 0.5D, (double) p_59392_.getY() + 0.5D, (double) p_59392_.getZ() + 0.5D, direction);
    }

    @Nullable
    private static IItemStorageHandler getContainerAt(Level p_59348_, double p_59349_, double p_59350_, double p_59351_, @Nullable Direction direction) {
        BlockPos blockpos = new BlockPos(p_59349_, p_59350_, p_59351_);
        BlockState blockstate = p_59348_.getBlockState(blockpos);

        if (blockstate.hasBlockEntity()) {
            return Services.INVENTORY.getItemStorageHandler(p_59348_.getBlockEntity(blockpos), direction).orElse(null);
        }

        return null;
    }

    private static boolean canMergeItems(ItemStack p_59345_, ItemStack p_59346_) {
        if (!p_59345_.is(p_59346_.getItem())) {
            return false;
        } else if (p_59345_.getDamageValue() != p_59346_.getDamageValue()) {
            return false;
        } else if (p_59345_.getCount() > p_59345_.getMaxStackSize()) {
            return false;
        } else {
            return ItemStack.tagMatches(p_59345_, p_59346_);
        }
    }

    public double getLevelX() {
        return (double) this.worldPosition.getX() + 0.5D;
    }

    public double getLevelY() {
        return (double) this.worldPosition.getY() + 0.5D;
    }

    public double getLevelZ() {
        return (double) this.worldPosition.getZ() + 0.5D;
    }

    public VoxelShape getSuckShape() {
        return Hopper.SUCK;
    }

    public void setCooldown(int p_59396_) {
        this.cooldownTime = p_59396_;
    }

    private boolean isOnCooldown() {
        return this.cooldownTime > 0;
    }

    public boolean isOnCustomCooldown() {
        return this.cooldownTime > getHopperCooldown(storageMaterial);
    }

    public static void entityInside(Level level, BlockPos pos, BlockState state, Entity entity, LockedHopperBlockEntity hopperBE) {
        if (entity instanceof ItemEntity && Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move((double) (-pos.getX()), (double) (-pos.getY()), (double) (-pos.getZ()))), hopperBE.getSuckShape(), BooleanOp.AND)) {
            tryMoveItems(level, pos, state, hopperBE, () -> {
                return addItem(hopperBE.getItemStackStorageHandler(), (ItemEntity) entity);
            });
        }
    }

    public long getLastUpdateTime() {
        return this.tickedGameTime;
    }

    public static class ItemHandler extends StorageItemStackStorageHandler {
        private final LockedHopperBlockEntity hopper;

        public ItemHandler(LockedHopperBlockEntity hopper) {
            super(hopper, hopper.storageMaterial != null ? hopper.storageMaterial.hopperSize() : 5);
            this.hopper = hopper;
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (simulate) {
                return super.insertItem(slot, stack, simulate);
            } else {
                boolean wasEmpty = this.hopper.getItemStackStorageHandler().isEmpty();

                int originalStackSize = stack.getCount();
                stack = super.insertItem(slot, stack, simulate);

                if (wasEmpty && originalStackSize > stack.getCount()) {
                    if (!hopper.isOnCustomCooldown()) {
                        hopper.setCooldown(getHopperCooldown(hopper.storageMaterial));
                    }
                }

                return stack;
            }
        }
    }
}
