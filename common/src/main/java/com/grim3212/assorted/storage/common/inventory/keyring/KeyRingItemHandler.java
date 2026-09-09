package com.grim3212.assorted.storage.common.inventory.keyring;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

/**
 * The keys used to be kept in the key ring's stack NBT; they are a
 * {@code DataComponents.CONTAINER} component now, which is the vanilla component for an item that
 * holds other items.
 */
public class KeyRingItemHandler extends ItemStackStorageHandler {

    private final ItemStack itemStack;
    public static final int KEY_RING_SIZE = 12;

    public KeyRingItemHandler(ItemStack itemStack) {
        super(KEY_RING_SIZE);
        this.itemStack = itemStack;
    }

    @Override
    public void onContentsChanged(int slot) {
        this.itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getStacks()));
    }

    public void load() {
        this.itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getStacks());
    }
}
