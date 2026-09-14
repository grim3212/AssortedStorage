package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.LockedStorageHandler;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity.NetworkSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A crate controller as one inventory: one slot per slot of every crate it reaches. The slot count
 * moves as the network changes.
 */
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
        return this.inv.getNetworkSlots().size();
    }

    /** The crate slot a handler slot stands for; null once the network no longer reaches it. */
    private @Nullable Target target(int slot) {
        List<NetworkSlot> networkSlots = this.inv.getNetworkSlots();
        if (slot < 0 || slot >= networkSlots.size() || this.inv.getLevel() == null) {
            return null;
        }

        NetworkSlot networkSlot = networkSlots.get(slot);
        if (this.inv.getLevel().getBlockEntity(networkSlot.pos()) instanceof CrateBlockEntity crate) {
            return new Target(crate.getItemStackStorageHandler(), networkSlot.slot());
        }

        return null;
    }

    private record Target(CrateSidedInv crate, int slot) {
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        Target target = target(slot);
        return target == null ? ItemStack.EMPTY : target.crate().getStackInSlot(target.slot());
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

        Target target = target(slot);
        if (target == null)
            return stack;

        return inLockCode.isEmpty()
                ? target.crate().insertItem(target.slot(), stack, simulate)
                : target.crate().insertItem(target.slot(), stack, simulate, inLockCode, ignoreLock);
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

        Target target = target(slot);
        if (target == null)
            return ItemStack.EMPTY;

        return inLockCode.isEmpty()
                ? target.crate().extractItem(target.slot(), amount, simulate)
                : target.crate().extractItem(target.slot(), amount, simulate, inLockCode, ignoreLock);
    }

    private boolean codeMatches(String s) {
        return !this.inv.isLocked() || this.inv.getLockCode().equals(s);
    }

    @Override
    public int getSlotLimit(int slot) {
        Target target = target(slot);
        return target == null ? 0 : target.crate().getSlotLimit(target.slot());
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        Target target = target(slot);
        return target != null && target.crate().isItemValid(target.slot(), stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        Target target = target(slot);
        if (target != null) {
            target.crate().setStackInSlot(target.slot(), stack);
        }
    }

    /** Delegated to the crate, so a rollback through a controller is lossless too. */
    @Override
    @NotNull
    public Runnable captureSlot(int slot) {
        Target target = target(slot);
        return target == null ? () -> {
        } : target.crate().captureSlot(target.slot());
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
