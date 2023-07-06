package com.grim3212.assorted.storage.common.inventory.bag;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BagItemHandler extends ItemStackStorageHandler {

    private ItemStack itemStack;
    private final StorageMaterial material;

    public BagItemHandler(ItemStack itemStack, @Nullable StorageMaterial material) {
        // Extra slot for the lock slot
        super(numStacks(material));
        this.itemStack = itemStack;
        this.material = material;
    }

    private static int numStacks(@Nullable StorageMaterial material) {
        return material == null ? 28 : material.totalItems() + 1;
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

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setSize(numStacks(this.material));
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, ItemStack.of(itemTags));
            }
        }
        onLoad();
    }

    public void load() {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("Inventory"))
            deserializeNBT(tag.getCompound("Inventory"));
    }
}
