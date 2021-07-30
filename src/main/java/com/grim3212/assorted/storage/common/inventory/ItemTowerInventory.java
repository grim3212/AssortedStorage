package com.grim3212.assorted.storage.common.inventory;

import java.util.stream.IntStream;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;

public class ItemTowerInventory implements WorldlyContainer {

	private final NonNullList<ItemTowerBlockEntity> itemTowers;
	private final BlockPos openedFrom;

	public ItemTowerInventory(NonNullList<ItemTowerBlockEntity> itemTowers, BlockPos openedFrom) {
		this.itemTowers = itemTowers;
		this.openedFrom = openedFrom;
		// Grab the first element
		if (itemTowers.size() < 1) {
			AssortedStorage.LOGGER.error("Opened tower without any tileentities! Something went wrong!");
		}
	}

	public WorldlyContainer getMainInventory() {
		return this.itemTowers.get(0);
	}

	public void setAnimation(int animID) {
		for (ItemTowerBlockEntity inventory : this.itemTowers)
			inventory.animate(animID);
	}

	private int getLocalSlot(int slot) {
		return slot % this.getMainInventory().getContainerSize();
	}

	private WorldlyContainer getInvFromSlot(int slot) {
		int inventoryIndex = (int) Math.floor(slot / this.getMainInventory().getContainerSize());
		return (WorldlyContainer) this.itemTowers.get(inventoryIndex);
	}

	@Override
	public int getContainerSize() {
		return this.getMainInventory().getContainerSize() * this.itemTowers.size();
	}

	@Override
	public boolean isEmpty() {
		for (WorldlyContainer inv : itemTowers) {
			if (!inv.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return getInvFromSlot(index).canPlaceItem(getLocalSlot(index), stack);
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
		for (WorldlyContainer inventory : this.itemTowers)
			inventory.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return !(player.distanceToSqr((double) this.openedFrom.getX() + 0.5D, (double) this.openedFrom.getY() + 0.5D, (double) this.openedFrom.getZ() + 0.5D) > 64.0D);
	}

	@Override
	public void startOpen(Player player) {
		for (WorldlyContainer inventory : this.itemTowers)
			inventory.startOpen(player);
	}

	@Override
	public void stopOpen(Player player) {
		for (WorldlyContainer inventory : this.itemTowers)
			inventory.stopOpen(player);
	}

	@Override
	public void clearContent() {
		for (WorldlyContainer inv : itemTowers) {
			inv.clearContent();
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return this.getMainInventory().canPlaceItemThroughFace(index, itemStackIn, direction);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return this.getMainInventory().canPlaceItemThroughFace(index, stack, direction);
	}
}
