package com.grim3212.assorted.storage.common.inventory;

import java.util.stream.IntStream;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class DualLockerInventory implements ISidedInventory {

	private final ISidedInventory topLocker;
	private final ISidedInventory bottomLocker;

	public DualLockerInventory(ISidedInventory bottomLocker, ISidedInventory topLocker) {
		this.bottomLocker = bottomLocker;
		this.topLocker = topLocker;
	}

	private boolean hasTopLocker() {
		return this.topLocker != null;
	}

	private int getLocalSlot(int slot) {
		if (!hasTopLocker() || getInvFromSlot(slot) == this.topLocker)
			return slot;
		return slot - this.topLocker.getSizeInventory();
	}

	private ISidedInventory getInvFromSlot(int slot) {
		return !hasTopLocker() ? this.bottomLocker : slot < this.topLocker.getSizeInventory() ? this.topLocker : this.bottomLocker;
	}

	@Override
	public int getSizeInventory() {
		return hasTopLocker() ? this.topLocker.getSizeInventory() : 0 + this.bottomLocker.getSizeInventory();
	}

	@Override
	public boolean isEmpty() {
		return this.bottomLocker.isEmpty() && hasTopLocker() ? this.topLocker.isEmpty() : true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return getInvFromSlot(index).getStackInSlot(getLocalSlot(index));
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return getInvFromSlot(index).decrStackSize(getLocalSlot(index), count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return this.getInvFromSlot(index).removeStackFromSlot(getLocalSlot(index));
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		getInvFromSlot(index).setInventorySlotContents(getLocalSlot(index), stack);
	}

	@Override
	public void markDirty() {
		this.bottomLocker.markDirty();
		if (hasTopLocker())
			this.topLocker.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return hasTopLocker() ? this.topLocker.isUsableByPlayer(player) : true && this.bottomLocker.isUsableByPlayer(player);
	}

	@Override
	public void openInventory(PlayerEntity player) {
		this.bottomLocker.openInventory(player);
		if (hasTopLocker())
			this.topLocker.openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		this.bottomLocker.closeInventory(player);
		if (hasTopLocker())
			this.topLocker.closeInventory(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return getInvFromSlot(index).isItemValidForSlot(getLocalSlot(index), stack);
	}

	@Override
	public void clear() {
		this.bottomLocker.clear();
		if (hasTopLocker())
			this.topLocker.clear();
	}

	private static final int[] ONE_LOCKER = IntStream.range(0, 45).toArray();
	private static final int[] TWO_LOCKER = IntStream.range(0, 90).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (hasTopLocker())
			return TWO_LOCKER;
		return ONE_LOCKER;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return this.bottomLocker.canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return this.bottomLocker.canExtractItem(index, stack, direction);
	}

}
