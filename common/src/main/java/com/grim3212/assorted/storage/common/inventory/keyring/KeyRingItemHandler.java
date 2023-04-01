package com.grim3212.assorted.storage.common.inventory.keyring;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class KeyRingItemHandler extends ItemStackStorageHandler {

    private ItemStack itemStack;
    public static final int KEY_RING_SIZE = 12;

    public KeyRingItemHandler(ItemStack itemStack) {
        super(KEY_RING_SIZE);
        this.itemStack = itemStack;
    }

    @Override
    public void load() {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("Inventory"))
            deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    public void save() {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.put("Inventory", serializeNBT());
    }
}
