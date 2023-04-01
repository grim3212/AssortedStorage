package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.LockedItemHandler;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateControllerInvWrapper implements IItemStorageHandler, LockedItemHandler {
    protected final CrateControllerBlockEntity inv;
    @Nullable
    protected final Direction side;

    public CrateControllerInvWrapper(CrateControllerBlockEntity inv, @Nullable Direction side) {
        this.inv = inv;
        this.side = side;
    }

    public CrateControllerBlockEntity getInv() {
        return inv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CrateControllerInvWrapper that = (CrateControllerInvWrapper) o;

        return inv.equals(that.inv) && side == that.side;
    }

    @Override
    public int hashCode() {
        int result = inv.hashCode();
        result = 31 * result + (side == null ? 0 : side.hashCode());
        return result;
    }

    @Override
    public int getSlots() {
        return inv.getSlotsForFace(null).length;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return inv.insertItem(slot, stack, simulate, "");
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode) {
        return inv.insertItem(slot, stack, simulate, inLockCode);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inv.extractItem(slot, amount, simulate, "");
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode) {
        return inv.extractItem(slot, amount, simulate, inLockCode);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return inv.canPlaceItem(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
    }
}