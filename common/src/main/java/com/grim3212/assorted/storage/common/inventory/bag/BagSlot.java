package com.grim3212.assorted.storage.common.inventory.bag;

import com.grim3212.assorted.storage.common.item.BagItem;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BagSlot extends SlotItemHandler {
	public BagSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return !(stack.getItem() instanceof BagItem) && super.mayPlace(stack);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getItemHandler() instanceof BagItemHandler)
			((BagItemHandler) getItemHandler()).setDirty();
	}
}
