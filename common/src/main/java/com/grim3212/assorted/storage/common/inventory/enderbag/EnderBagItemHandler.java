package com.grim3212.assorted.storage.common.inventory.enderbag;

import com.grim3212.assorted.lib.core.inventory.ISerializableItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class EnderBagItemHandler extends ItemStackStorageHandler implements ISerializableItemStorageHandler {

    private ItemStack itemStack;
    private boolean changed = false;

    public EnderBagItemHandler(ItemStack itemStack) {
        // Single slot for the padlock
        super(1);
        this.itemStack = itemStack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        changed = true;
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        changed = true;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);
        if (!ItemStack.isSame(stack, stacks.get(slot))) {
            onContentsChanged(slot);
        }
        this.stacks.set(slot, stack);
    }

    public void setDirty() {
        this.changed = true;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        changed = true;
    }

    @Override
    public void load() {
        load(itemStack.getOrCreateTag());
    }

    public void load(@Nonnull CompoundTag nbt) {
        if (nbt.contains("Inventory"))
            deserializeNBT(nbt.getCompound("Inventory"));
    }

    @Override
    public void save() {
        if (changed) {
            CompoundTag nbt = itemStack.getOrCreateTag();
            nbt.put("Inventory", serializeNBT());

            // If we set the storage lock on each save it should be a bit more performant
            // then checking the inventory every render to decide if it is locked or not
            ItemStack storageLock = this.getStackInSlot(0);
            String newLockCode = StorageUtil.getCode(storageLock);
            nbt.putString("Storage_Lock", newLockCode);
            changed = false;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // Extra slot for the lock slot
        setSize(1);
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
}
