package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import net.minecraft.world.item.ItemStack;

public class LockSlot extends SlotStorageHandler {
    public LockSlot(IItemStorageHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof PadlockItem && !StorageUtil.getCode(stack).isEmpty();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
