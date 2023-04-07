package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import net.minecraft.world.item.ItemStack;

public class CrateLockSlot extends EnhancementSlot {

    public CrateLockSlot(IItemStorageHandler itemHandler, int slot, int x, int y) {
        super(itemHandler, slot, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof PadlockItem && !StorageUtil.getCode(stack).isEmpty();
    }
}
