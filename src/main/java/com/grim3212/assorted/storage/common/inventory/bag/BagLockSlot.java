package com.grim3212.assorted.storage.common.inventory.bag;

import org.jetbrains.annotations.NotNull;

import com.grim3212.assorted.storage.common.item.PadlockItem;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BagLockSlot extends SlotItemHandler {
	public BagLockSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() instanceof PadlockItem && !StorageUtil.getCode(stack).isEmpty();
	}

	@Override
	public int getMaxStackSize(@NotNull ItemStack stack) {
		return 1;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getItemHandler() instanceof BagItemHandler)
			((BagItemHandler) getItemHandler()).setDirty();
	}
}
