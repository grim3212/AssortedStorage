package com.grim3212.assorted.storage.common.save;

import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;

public interface IEnderData {
    void markDirty();

    LockedEnderChestInventory getInventory(String code);
}
