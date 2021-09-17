package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.storage.common.util.StorageLockCode;

public interface ILockeable {

	public boolean isLocked();

	public String getLockCode();
	
	public StorageLockCode getStorageLockCode();

	public void setLockCode(String s);

}
