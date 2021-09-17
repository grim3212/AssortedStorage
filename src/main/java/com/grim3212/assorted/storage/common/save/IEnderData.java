package com.grim3212.assorted.storage.common.save;

import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

public interface IEnderData {
	void markDirty();
	
	LockedEnderChestInventory getInventory(StorageLockCode code);
}
