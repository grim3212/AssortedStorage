package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.network.SyncCrate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrateBlockEntity extends BlockEntity implements MenuProvider, INamed, ILockable, IInventoryBlockEntity {

    private final CrateLayout layout;
    private Component customName;
    private UUID playerTimerUUID;
    private long playerTimerMillis;
    private ItemStack playerTimerStack = ItemStack.EMPTY;
    private IPlatformInventoryStorageHandler handler;
    protected CrateSidedInv storageHandler;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        this(StorageBlockEntityTypes.CRATE.get(), pos, state);
    }

    public CrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof CrateBlock storageCrate) {
            layout = storageCrate.getLayout();
        } else {
            this.layout = CrateLayout.SINGLE;
        }

        this.storageHandler = new CrateSidedInv(this);
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.handler == null) {
            this.handler = this.createStorageHandler();
        }

        return this.handler;
    }

    private IPlatformInventoryStorageHandler createStorageHandler() {
        return Services.INVENTORY.createStorageInventoryHandler(this.storageHandler);
    }

    public CrateSidedInv getItemStackStorageHandler() {
        return this.storageHandler;
    }

    public CrateLayout getLayout() {
        return layout;
    }

    @Override
    public boolean isLocked() {
        return StorageUtil.hasCode(this.getItemStackStorageHandler().getLockStack());
    }

    @Override
    public String getLockCode() {
        return StorageUtil.getCode(this.getItemStackStorageHandler().getLockStack());
    }

    @Override
    public void setLockCode(String s) {
        if (s != null && !s.isEmpty()) {
            this.getItemStackStorageHandler().setLockStack(StorageUtil.setCodeOnStack(s, new ItemStack(StorageItems.LOCKSMITH_LOCK.get())));

            this.setChanged();
            this.modelUpdate();
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        if (nbt.contains("Inventory")) {
            this.getItemStackStorageHandler().deserializeNBT(nbt.getCompound("Inventory"));
        } else if (nbt.contains("Items") || nbt.contains("Enhancements")) {
            this.legacyLoad(nbt);
        }

        if (nbt.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }
    }

    // TODO: remove in future versions
    private void legacyLoad(CompoundTag tag) {
        ListTag items = tag.getList("Items", 10);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag slot = items.getCompound(i);
            int slotIdx = slot.getByte("Slot") & 255;
            if (slotIdx >= 0 && slotIdx < this.getItemStackStorageHandler().getSlots()) {
                int amount = slot.getInt("SlotAmount");
                int rotation = slot.getInt("SlotRotation");
                boolean locked = slot.getBoolean("SlotLocked");
                ItemStack stack = ItemStack.of(slot);
                this.getItemStackStorageHandler().getSlotContents().set(slotIdx, new LargeItemStack(stack, amount, rotation, locked));
            }
        }

        ListTag enhancementItems = tag.getList("Enhancements", 10);
        for (int i = 0; i < enhancementItems.size(); i++) {
            CompoundTag slot = enhancementItems.getCompound(i);
            int slotIdx = slot.getByte("Slot") & 255;
            if (slotIdx >= 0 && slotIdx < this.getItemStackStorageHandler().getEnhancements().size()) {
                this.getItemStackStorageHandler().getEnhancements().set(slotIdx, ItemStack.of(slot));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        // Save all the items
        compound.put("Inventory", this.getItemStackStorageHandler().serializeNBT());

        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void modelUpdate() {
        this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.storage_crate");
    }


    public void setSlotChanged(int slot) {
        this.setChanged();
        this.level.blockUpdated(getBlockPos(), getBlockState().getBlock());

        if (this.level != null && !this.level.isClientSide()) {
            Services.NETWORK.sendToNearby(level, getBlockPos(), new SyncCrate(getBlockPos(), slot, this.getItemStackStorageHandler().getLargeItemStack(slot)));
        }
    }


    /**
     * Attacking (left-click) will drop items if they are contained in the slot hit
     * <p>
     * if player is shifting then drop a stack else drop 1
     *
     * @param player
     * @param hit
     */
    public boolean attack(Player player, BlockHitResult hit) {
        int hitSlot = getHitSlot(hit);
        if (hitSlot < 0)
            return false;

        int maxAmount = player.isShiftKeyDown() ? 64 : 1;

        ItemStack stack = this.getItemStackStorageHandler().removeItem(hitSlot, maxAmount);
        if (!stack.isEmpty()) {
            if (!player.getInventory().add(stack)) {
                Containers.dropItemStack(level, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), stack);
            }
            return true;
        }
        return !stack.isEmpty();
    }

    /**
     * On use (right-click) we will try and do a number of things depending on how
     * it was interacted
     * <p>
     * if rotate item is used rotate hit slot
     * <p>
     * <p>
     * if first use add the current item by 1 if second use add the current item by
     * all found in players inventory
     *
     * @param player
     * @param handIn
     * @param hit
     */
    public InteractionResult use(Player player, InteractionHand handIn, BlockHitResult hit) {
        int hitSlot = getHitSlot(hit);

        if (hitSlot < 0) {
            return InteractionResult.PASS;
        }

        ItemStack playerStack = player.getItemInHand(handIn).copyWithCount(1);

        if (playerStack.getItem() == StorageItems.ROTATOR_MAJIG.get()) {
            this.getItemStackStorageHandler().cycleSlotRotation(hitSlot);
            this.setSlotChanged(hitSlot);
            return InteractionResult.SUCCESS;
        }

        if (player.getUUID().equals(playerTimerUUID) && Util.getMillis() - playerTimerMillis < 275 && playerTimerStack != ItemStack.EMPTY) {
            List<Integer> slots = findMatchingStacks(player, playerTimerStack);
            for (int slot : slots) {
                ItemStack slotItem = player.getInventory().getItem(slot).copy();
                int addRet = this.getItemStackStorageHandler().addItem(hitSlot, slotItem);
                if (addRet < 0) {
                    // We can't add anymore to this storage crate
                    break;
                } else if (addRet == 0) {
                    player.getInventory().setItem(slot, ItemStack.EMPTY);
                } else {
                    player.getInventory().getItem(slot).shrink(slotItem.getCount() - addRet);
                }
            }

            playerTimerUUID = null;
            playerTimerMillis = 0;
            playerTimerStack = ItemStack.EMPTY;
            return InteractionResult.SUCCESS;
        }

        ItemStack playerItem = player.getItemInHand(handIn).copy();
        int addRet = this.getItemStackStorageHandler().addItem(hitSlot, playerItem);
        if (addRet < 0) {
            // We can't add anymore to this storage crate
            return InteractionResult.SUCCESS;
        } else if (addRet == 0) {
            player.getItemInHand(handIn).shrink(playerItem.getCount());

            playerTimerUUID = player.getUUID();
            playerTimerMillis = Util.getMillis();
            playerTimerStack = playerStack;
        } else {
            player.getItemInHand(handIn).shrink(playerItem.getCount() - addRet);
        }

        return InteractionResult.SUCCESS;
    }

    public static List<Integer> findMatchingStacks(Player player, ItemStack stack) {
        List<Integer> slots = new ArrayList<>();

        for (int slot = 0; slot < player.getInventory().getContainerSize(); ++slot) {
            ItemStack itemstack = player.getInventory().getItem(slot);

            if (!itemstack.isEmpty()) {
                if (Services.INVENTORY.canItemStacksStack(stack, itemstack)) {
                    slots.add(slot);
                }
            }
        }

        return slots;
    }

    private int getHitSlot(BlockHitResult hit) {
        if (hit.getDirection() != this.getBlockState().getValue(CrateBlock.FACING)) {
            return -1;
        }

        return this.layout.getHitSlot(hit);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.handler != null) {
            this.handler.invalidate();
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
        return new CrateContainer(StorageContainerTypes.CRATE.get(), windowId, player, this);
    }
}
