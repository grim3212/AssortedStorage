package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.LockedStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.crates.CrateConnection;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrateControllerInvWrapper implements IItemStorageHandler, LockedStorageHandler {
    protected final CrateControllerBlockEntity inv;

    public CrateControllerInvWrapper(CrateControllerBlockEntity inv) {
        this.inv = inv;
    }

    public CrateControllerBlockEntity getInv() {
        return inv;
    }


    @Override
    public int getSlots() {
        return this.inv.getPossibleSlots().length;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, "", false);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode, boolean ignoreLock) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!codeMatches(inLockCode) && !ignoreLock)
            return stack;

        List<CrateConnection> connections = this.inv.findSlottedCrates(slot);

        for (CrateConnection connection : connections) {
            if (connection == null) {
                continue;
            }

            if (this.inv.getLevel().getBlockEntity(connection.getPos()) instanceof CrateBlockEntity crate) {
                IItemStorageHandler storageHandler = Services.INVENTORY.getItemStorageHandler(crate, null).orElse(null);
                if (storageHandler != null && storageHandler instanceof CrateSidedInv crateInv) {
                    ItemStack response = inLockCode.isEmpty() ? crateInv.insertItem(slot, stack, simulate) : crateInv.insertItem(slot, stack, simulate, inLockCode, ignoreLock);

                    if (response != stack) {
                        return response;
                    }
                }
            }
        }

        return stack;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, "", false);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode, boolean ignoreLock) {
        if (amount == 0 || (!codeMatches(inLockCode) && !ignoreLock))
            return ItemStack.EMPTY;

        List<CrateConnection> connections = this.inv.findSlottedCrates(slot);

        for (CrateConnection connection : connections) {
            if (connection == null) {
                continue;
            }

            if (this.inv.getLevel().getBlockEntity(connection.getPos()) instanceof CrateBlockEntity crate) {
                IItemStorageHandler storageHandler = Services.INVENTORY.getItemStorageHandler(crate, null).orElse(null);
                if (storageHandler != null && storageHandler instanceof CrateSidedInv crateInv) {
                    ItemStack response = inLockCode.isEmpty() ? crateInv.extractItem(slot, amount, simulate) : crateInv.extractItem(slot, amount, simulate, inLockCode, ignoreLock);
                    if (!response.isEmpty()) {
                        return response;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private boolean codeMatches(String s) {
        return !this.inv.isLocked() || this.inv.getLockCode().equals(s);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.insertItem(slot, stack, true, "", true) == ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
    }

    @Override
    public void onContentsChanged(int slot) {
        inv.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos cratePos = this.inv.getBlockPos();
        if (this.inv.getLevel().getBlockEntity(cratePos) != this.inv) {
            return false;
        } else {
            return !(player.distanceToSqr((double) cratePos.getX() + 0.5D, (double) cratePos.getY() + 0.5D, (double) cratePos.getZ() + 0.5D) > 64.0D);
        }
    }
}