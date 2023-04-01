package com.grim3212.assorted.storage.common.inventory.enderbag;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EnderBagLockSlot extends SlotStorageHandler {
    public EnderBagLockSlot(IItemStorageHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof PadlockItem && !StorageUtil.getCode(stack).isEmpty();
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (getItemHandler() instanceof EnderBagItemHandler)
            ((EnderBagItemHandler) getItemHandler()).setDirty();
    }
}
