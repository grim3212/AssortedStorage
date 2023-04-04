package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DualLockerInventory extends ItemStackStorageHandler {

    private final IItemStorageHandler topLocker;
    private final IItemStorageHandler bottomLocker;

    public DualLockerInventory(IItemStorageHandler bottomLocker, IItemStorageHandler topLocker) {
        super(topLocker != null ? topLocker.getSlots() + bottomLocker.getSlots() : bottomLocker.getSlots());
        this.bottomLocker = bottomLocker;
        this.topLocker = topLocker;
    }

    private boolean hasTopLocker() {
        return this.topLocker != null;
    }

    private int getLocalSlot(int slot) {
        if (!hasTopLocker() || getInvFromSlot(slot) == this.topLocker)
            return slot;
        return slot - this.topLocker.getSlots();
    }

    private IItemStorageHandler getInvFromSlot(int slot) {
        return !hasTopLocker() ? this.bottomLocker : slot < this.topLocker.getSlots() ? this.topLocker : this.bottomLocker;
    }

    @Override
    public boolean stillValid(Player player) {
        return hasTopLocker() ? this.topLocker.stillValid(player) : true && this.bottomLocker.stillValid(player);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return getInvFromSlot(slot).getStackInSlot(getLocalSlot(slot));
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
    public void startOpen(Player player) {
        this.bottomLocker.startOpen(player);
        if (hasTopLocker())
            this.topLocker.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        this.bottomLocker.stopOpen(player);
        if (hasTopLocker())
            this.topLocker.stopOpen(player);
    }

}
