package com.grim3212.assorted.storage.common.inventory.enderbag;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.item.EnderBagItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnderBagSlot extends SlotStorageHandler {
    private final String lockCode;
    private final int index;

    public EnderBagSlot(IItemStorageHandler itemHandler, int index, int xPosition, int yPosition, String lockCode) {
        super(itemHandler, index, xPosition, yPosition);
        this.lockCode = lockCode;
        this.index = index;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !(stack.getItem() instanceof EnderBagItem) && !stack.isEmpty();
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        if (this.getItemHandler() instanceof LockedEnderChestInventory enderChest) {

            ItemStack maxAdd = stack.copy();
            int maxInput = stack.getMaxStackSize();
            maxAdd.setCount(maxInput);

            ItemStack currentStack = enderChest.getStackInSlot(index);

            enderChest.setStackInSlot(index, ItemStack.EMPTY);

            ItemStack remainder = enderChest.insertItem(index, maxAdd, true, lockCode);

            enderChest.setStackInSlot(index, currentStack);

            return maxInput - remainder.getCount();

        } else {
            return super.getMaxStackSize(stack);
        }
    }

    private ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = this.getItemHandler().getStackInSlot(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            this.getItemHandler().setStackInSlot(slot, Services.INVENTORY.copyStackWithSize(stackInSlot, stackInSlot.getCount() - amount));
            return Services.INVENTORY.copyStackWithSize(stackInSlot, amount);
        }
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        if (this.getItemHandler() instanceof LockedEnderChestInventory enderChest) {
            return !extractItem(index, 1, true).isEmpty();
        }

        return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
    }

    @Override
    @NotNull
    public ItemStack remove(int amount) {
        if (this.getItemHandler() instanceof LockedEnderChestInventory enderChest) {
            return extractItem(index, amount, false);
        }

        return this.getItemHandler().extractItem(index, amount, false);
    }
}
