package com.grim3212.assorted.storage.common.inventory.enderbag;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class EnderBagItemHandler extends ItemStackStorageHandler {

    private ItemStack itemStack;

    public EnderBagItemHandler(ItemStack itemStack) {
        // Single slot for the padlock
        super(1);
        this.itemStack = itemStack;
    }

    @Override
    public void onContentsChanged(int slot) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.put("Inventory", serializeNBT());

        // If we set the storage lock on each save it should be a bit more performant
        // then checking the inventory every render to decide if it is locked or not
        ItemStack storageLock = this.getStackInSlot(0);
        String newLockCode = StorageUtil.getCode(storageLock);
        nbt.putString("Storage_Lock", newLockCode);
    }

    public void load() {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("Inventory"))
            deserializeNBT(tag.getCompound("Inventory"));
    }
}
