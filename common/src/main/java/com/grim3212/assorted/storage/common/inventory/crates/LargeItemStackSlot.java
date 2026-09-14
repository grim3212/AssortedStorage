package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LargeItemStackSlot extends SlotStorageHandler {

    public LargeItemStackSlot(IItemStorageHandler itemHandler, int slot, int x, int y) {
        super(itemHandler, slot, x, y);
    }

    @Override
    public boolean allowModification(Player player) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    /** Off the slot, not the handler: a locked slot that has run empty still shows its item here. */
    @Override
    public ItemStack getItem() {
        if (this.getItemHandler() instanceof CrateSidedInv crate) {
            return crate.getLargeItemStack(this.getContainerSlot()).getStack().copyWithCount(1);
        }

        return super.getItem().copyWithCount(1);
    }

    @Override
    public void set(ItemStack stack) {
    }

    @Override
    public void setByPlayer(ItemStack stack) {
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }
}
