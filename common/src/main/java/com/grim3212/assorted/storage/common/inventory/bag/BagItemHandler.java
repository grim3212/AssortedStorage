package com.grim3212.assorted.storage.common.inventory.bag;

import com.grim3212.assorted.lib.core.inventory.impl.ItemStackStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.Nullable;

/**
 * A bag's contents used to live in the stack's free form NBT under an "Inventory" tag. Stack NBT
 * is gone, and the vanilla component for exactly this is {@code DataComponents.CONTAINER}, so the
 * bag stores an {@link ItemContainerContents} instead of hand rolled tags.
 */
public class BagItemHandler extends ItemStackStorageHandler {

    private final ItemStack itemStack;
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
        this.itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getStacks()));

        // If we set the storage lock on each save it should be a bit more performant
        // then checking the inventory every render to decide if it is locked or not
        ItemStack storageLock = this.getStackInSlot(0);
        StorageUtil.setLockOnStack(this.itemStack, StorageUtil.getCode(storageLock));
    }

    public void load() {
        setSize(numStacks(this.material));
        this.itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getStacks());
    }
}
