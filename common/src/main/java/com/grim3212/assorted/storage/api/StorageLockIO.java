package com.grim3212.assorted.storage.api;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Lock code IO for the places {@link StorageUtil} does not cover.
 * <p>
 * AssortedLib writes a lock into an {@link ItemStack}'s {@code CUSTOM_DATA} component through a
 * {@code CompoundTag}. Two things it cannot do are needed here: block entity state moved onto
 * {@link ValueOutput} / {@link ValueInput} in 26.x and there is no way to bridge those back to a
 * bare tag, and clearing a lock needs the key removed rather than skipped.
 * <p>
 * TODO(26.2): these belong on AssortedLib's {@code StorageUtil} beside the CompoundTag form so the
 * key is only written down once. They live here because AssortedLib was outside this slice.
 */
public final class StorageLockIO {

    /** Must stay in sync with the key {@link StorageUtil} reads and writes. */
    private static final String LOCK_KEY = "Storage_Lock";

    private StorageLockIO() {
    }

    public static void writeLock(ValueOutput output, String lock) {
        if (!lock.isEmpty()) {
            output.putString(LOCK_KEY, lock);
        }
    }

    public static String readLock(ValueInput input) {
        return input.getStringOr(LOCK_KEY, "");
    }

    /**
     * Sets, or clears when the code is empty, the lock cached on an item stack.
     * <p>
     * {@code StorageUtil.writeCodeToStack} only ever writes, so a lock that has been taken back off
     * would otherwise stay behind in the component.
     */
    public static void setLockOnStack(ItemStack stack, String lock) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, (tag) -> {
            if (lock.isEmpty()) {
                tag.remove(LOCK_KEY);
            } else {
                tag.putString(LOCK_KEY, lock);
            }
        });
    }
}
