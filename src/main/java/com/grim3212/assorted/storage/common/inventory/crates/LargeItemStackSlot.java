package com.grim3212.assorted.storage.common.inventory.crates;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LargeItemStackSlot extends Slot {

	public LargeItemStackSlot(Container inv, int slot, int x, int y) {
		super(inv, slot, x, y);
	}

	@Override
	public boolean allowModification(Player player) {
		return false;
	}

	@Override
	public boolean mayPickup(Player player) {
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public ItemStack getItem() {
		return super.getItem().copyWithCount(1);
	}

	@Override
	public void set(ItemStack stack) {
	}

	@Override
	public void initialize(ItemStack stack) {
	}

	@Override
	public ItemStack remove(int amount) {
		return ItemStack.EMPTY;
	}
}
