package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.common.util.StorageMaterial;

public interface IStorageMaterial {
	
	public static enum StorageType {
		CHEST,
		BARREL,
		SHULKER_BOX
	}

	public StorageMaterial getStorageMaterial();
	
	public StorageType getStorageType();
}
