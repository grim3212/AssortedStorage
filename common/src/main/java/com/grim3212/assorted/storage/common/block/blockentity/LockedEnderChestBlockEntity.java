package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import com.grim3212.assorted.storage.common.save.EnderSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class LockedEnderChestBlockEntity extends BaseStorageBlockEntity {

    private LockedEnderChestInventory inventory;

    public LockedEnderChestBlockEntity(BlockPos pos, BlockState state) {
        super(StorageBlockEntityTypes.LOCKED_ENDER_CHEST.get(), pos, state);
    }

    @Override
    public IPlatformInventoryStorageHandler createStorageHandler() {
        if (level != null && inventory == null && isLocked()) {
            this.inventory = EnderSavedData.get(level).getInventory(this.getStorageLockCode());
            this.inventory.addWeakListener(this);
        }
        return Services.INVENTORY.createStorageInventoryHandler(this.inventory);
    }

    @Override
    public void setLockCode(String s) {
        super.setLockCode(s);
        invalidateInventory();
    }

    private void invalidateInventory() {
        releasePreviousInventory();
    }

    private void releasePreviousInventory() {
        if (inventory != null) {
            inventory.removeWeakListener(this);
        }
        inventory = null;
    }

    @Override
    public int getContainerSize() {
        return this.getStorageHandler().getItemStorageHandler().getSlots();
    }

    @Override
    public boolean isEmpty() {
        IItemStorageHandler handler = this.getStorageHandler().getItemStorageHandler();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {
        this.remove = true;
        this.getStorageHandler().invalidate();
        this.invalidateInventory();
    }

    @Override
    public void clearContent() {
        IItemStorageHandler handler = this.getStorageHandler().getItemStorageHandler();
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public ItemStack getItem(int index) {
        return this.getStorageHandler().getItemStorageHandler().getStackInSlot(index);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory.getItems();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.getStorageHandler().getItemStorageHandler().setStackInSlot(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    protected boolean selfInventory() {
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
        return StorageContainer.createEnderChestContainer(windowId, playerInv, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(Constants.MOD_ID + ".container.locked_ender_chest");
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
        return !this.isLocked();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return !this.isLocked();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction, String code, boolean force) {
        return force || (this.isLocked() ? this.getLockCode().equals(code) : true);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction, String code, boolean force) {
        return force || (this.isLocked() ? this.getLockCode().equals(code) : true);
    }

}
