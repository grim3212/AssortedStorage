package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemTowerInventory extends ItemStackStorageHandler {

    private final NonNullList<ItemTowerBlockEntity> itemTowers;
    private final BlockPos openedFrom;

    public ItemTowerInventory(NonNullList<ItemTowerBlockEntity> itemTowers, BlockPos openedFrom) {
        this.itemTowers = itemTowers;
        this.openedFrom = openedFrom;
        // Grab the first element
        if (itemTowers.size() < 1) {
            Constants.LOG.error("Opened tower without any tileentities! Something went wrong!");
        } else {
            setSize(this.getSlots());
        }
    }

    public StorageItemStackStorageHandler getMainInventory() {
        return this.itemTowers.get(0).getItemStackStorageHandler();
    }

    public void setAnimation(int animID) {
        for (ItemTowerBlockEntity inventory : this.itemTowers)
            inventory.animate(animID);
    }

    private int getLocalSlot(int slot) {
        return slot % this.getMainInventory().getSlots();
    }

    private StorageItemStackStorageHandler getInvFromSlot(int slot) {
        int inventoryIndex = (int) Math.floor(slot / this.getMainInventory().getSlots());
        return this.itemTowers.get(inventoryIndex).getItemStackStorageHandler();
    }

    @Override
    public boolean isEmpty() {
        for (ItemTowerBlockEntity inv : itemTowers) {
            if (!inv.getItemStackStorageHandler().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getSlots() {
        return this.getMainInventory().getSlots() * this.itemTowers.size();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return getInvFromSlot(index).getStackInSlot(getLocalSlot(index));
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return getInvFromSlot(slot).insertItem(getLocalSlot(slot), stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return getInvFromSlot(slot).extractItem(getLocalSlot(slot), amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInvFromSlot(slot).getSlotLimit(getLocalSlot(slot));
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return getInvFromSlot(slot).isItemValid(getLocalSlot(slot), stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        getInvFromSlot(slot).setStackInSlot(getLocalSlot(slot), stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return !(player.distanceToSqr((double) this.openedFrom.getX() + 0.5D, (double) this.openedFrom.getY() + 0.5D, (double) this.openedFrom.getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void startOpen(Player player) {
        for (ItemTowerBlockEntity inventory : this.itemTowers)
            inventory.getItemStackStorageHandler().startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        for (ItemTowerBlockEntity inventory : this.itemTowers)
            inventory.getItemStackStorageHandler().stopOpen(player);
    }
}
