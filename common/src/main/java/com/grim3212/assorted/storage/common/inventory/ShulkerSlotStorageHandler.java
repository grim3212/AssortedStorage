package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.slot.SlotStorageHandler;
import net.minecraft.world.item.ItemStack;

public class ShulkerSlotStorageHandler extends SlotStorageHandler {
    public ShulkerSlotStorageHandler(IItemStorageHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean mayPlace(ItemStack itemStack) {
        return itemStack.getItem().canFitInsideContainerItems();
    }
}
