package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import net.minecraft.world.item.ItemStack;

public class EnhancementSlot extends SlotStorageHandler {

    public EnhancementSlot(IItemStorageHandler itemHandler, int slot, int x, int y) {
        super(itemHandler, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof ICrateUpgrade;
    }

    @Override
    public ItemStack getItem() {
        if (this.getItemHandler() instanceof CrateSidedInv crate) {
            return crate.getEnhancements().get(this.getContainerSlot());
        }

        return super.getItem();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void set(ItemStack stack) {
        if (this.getItemHandler() instanceof CrateSidedInv crate) {
            crate.getEnhancements().set(this.getContainerSlot(), stack);
            this.setChanged();
        } else {
            super.set(stack);
        }
    }

    @Override
    public void initialize(ItemStack stack) {
        if (this.getItemHandler() instanceof CrateSidedInv crate) {
            crate.getEnhancements().set(this.getContainerSlot(), stack);
            this.setChanged();
        } else {
            super.initialize(stack);
        }
    }

    @Override
    public ItemStack remove(int amount) {
        if (this.getItemHandler() instanceof CrateSidedInv crate) {
            return crate.removeEnhancement(this.getContainerSlot(), amount);
        }

        return super.remove(amount);
    }
}
