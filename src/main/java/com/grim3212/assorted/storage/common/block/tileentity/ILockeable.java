package com.grim3212.assorted.storage.common.block.tileentity;

public interface ILockeable {

	public boolean isLocked();

	public String getLockCode();

	public void setLockCode(String s);

}
