package com.grim3212.assorted.storage.common.inventory.keyring;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.world.item.ItemStack;

public class KeyRingSlot extends SlotStorageHandler {
    public KeyRingSlot(IItemStorageHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() == StorageItems.LOCKSMITH_KEY.get();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (getItemHandler() instanceof KeyRingItemHandler)
            ((KeyRingItemHandler) getItemHandler()).setDirty();
    }
}
