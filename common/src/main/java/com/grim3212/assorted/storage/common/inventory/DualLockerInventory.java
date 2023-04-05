package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.lib.core.inventory.impl.LockedItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DualLockerInventory extends LockedItemStackStorageHandler {

    private final LockedItemStackStorageHandler topLocker;
    private final LockedItemStackStorageHandler bottomLocker;

    public DualLockerInventory(ILockable lockCode, LockedItemStackStorageHandler bottomLocker, LockedItemStackStorageHandler topLocker) {
        super(lockCode, topLocker != null ? topLocker.getSlots() + bottomLocker.getSlots() : bottomLocker.getSlots());
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

    private LockedItemStackStorageHandler getInvFromSlot(int slot) {
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
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode, boolean ignoreLock) {
        return getInvFromSlot(slot).insertItem(getLocalSlot(slot), stack, simulate, inLockCode, ignoreLock);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode, boolean ignoreLock) {
        return getInvFromSlot(slot).extractItem(getLocalSlot(slot), amount, simulate, inLockCode, ignoreLock);
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
