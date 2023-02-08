package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.storage.common.item.PadlockItem;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class LockSlot extends EnhancementSlot {

	public LockSlot(Container inv, int slot, int x, int y) {
		super(inv, slot, x, y);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() instanceof PadlockItem && !StorageUtil.getCode(stack).isEmpty();
	}
}
