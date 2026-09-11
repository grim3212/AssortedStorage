package com.grim3212.assorted.storage.common.inventory.enderbag;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * Holds only the ender bag's padlock slot. Kept on the stack as a
 * {@code DataComponents.CONTAINER} component now that stack NBT is gone.
 */
public class EnderBagItemHandler extends ItemStackStorageHandler {

    private final ItemStack itemStack;

    public EnderBagItemHandler(ItemStack itemStack) {
        // Single slot for the padlock
        super(1);
        this.itemStack = itemStack;
    }

    @Override
    public void onContentsChanged(int slot) {
        this.itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getStacks()));

        // If we set the storage lock on each save it should be a bit more performant
        // then checking the inventory every render to decide if it is locked or not
        ItemStack storageLock = this.getStackInSlot(0);
        StorageUtil.setLockOnStack(this.itemStack, StorageUtil.getCode(storageLock));
    }

    public void load() {
        this.itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getStacks());
    }
}
