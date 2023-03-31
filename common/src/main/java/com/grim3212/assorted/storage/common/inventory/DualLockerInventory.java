package com.grim3212.assorted.storage.common.inventory;

import java.util.stream.IntStream;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

public class DualLockerInventory implements WorldlyContainer {

	private final WorldlyContainer topLocker;
	private final WorldlyContainer bottomLocker;

	public DualLockerInventory(WorldlyContainer bottomLocker, WorldlyContainer topLocker) {
		this.bottomLocker = bottomLocker;
		this.topLocker = topLocker;
	}

	private boolean hasTopLocker() {
		return this.topLocker != null;
	}

	private int getLocalSlot(int slot) {
		if (!hasTopLocker() || getInvFromSlot(slot) == this.topLocker)
			return slot;
		return slot - this.topLocker.getContainerSize();
	}

	private WorldlyContainer getInvFromSlot(int slot) {
		return !hasTopLocker() ? this.bottomLocker : slot < this.topLocker.getContainerSize() ? this.topLocker : this.bottomLocker;
	}

	@Override
	public int getContainerSize() {
		return hasTopLocker() ? this.topLocker.getContainerSize() : 0 + this.bottomLocker.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return this.bottomLocker.isEmpty() && hasTopLocker() ? this.topLocker.isEmpty() : true;
	}

	@Override
	public ItemStack getItem(int index) {
		return getInvFromSlot(index).getItem(getLocalSlot(index));
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return getInvFromSlot(index).removeItem(getLocalSlot(index), count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return this.getInvFromSlot(index).removeItemNoUpdate(getLocalSlot(index));
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		getInvFromSlot(index).setItem(getLocalSlot(index), stack);
	}

	@Override
	public void setChanged() {
		this.bottomLocker.setChanged();
		if (hasTopLocker())
			this.topLocker.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return hasTopLocker() ? this.topLocker.stillValid(player) : true && this.bottomLocker.stillValid(player);
	}

	@Override
	public void startOpen(Player player) {
		this.bottomLocker.startOpen(player);
		if (hasTopLocker())
			this.topLocker.startOpen(player);
	}

	@Override
	public void stopOpen(Player player) {
		this.bottomLocker.stopOpen(player);
		if (hasTopLocker())
			this.topLocker.stopOpen(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return getInvFromSlot(index).canPlaceItem(getLocalSlot(index), stack);
	}

	@Override
	public void clearContent() {
		this.bottomLocker.clearContent();
		if (hasTopLocker())
			this.topLocker.clearContent();
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
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return this.bottomLocker.canPlaceItemThroughFace(index, itemStackIn, direction);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return this.bottomLocker.canTakeItemThroughFace(index, stack, direction);
	}

}
