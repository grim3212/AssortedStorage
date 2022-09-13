package com.grim3212.assorted.storage.common.block.blockentity;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;

public interface LockedItemHandler {

	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode);
	
	public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode);
}
