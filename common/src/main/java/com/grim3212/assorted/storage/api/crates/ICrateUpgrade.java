package com.grim3212.assorted.storage.api.crates;

public interface ICrateUpgrade {

	default int getStorageModifier() {
		return 0;
	}
}
