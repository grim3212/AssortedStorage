package com.grim3212.assorted.storage.common.inventory;

import java.util.stream.IntStream;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.tileentity.ItemTowerTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class ItemTowerInventory implements ISidedInventory {

	private final NonNullList<ItemTowerTileEntity> itemTowers;
	private final BlockPos openedFrom;

	public ItemTowerInventory(NonNullList<ItemTowerTileEntity> itemTowers, BlockPos openedFrom) {
		this.itemTowers = itemTowers;
		this.openedFrom = openedFrom;
		// Grab the first element
		if (itemTowers.size() < 1) {
			AssortedStorage.LOGGER.error("Opened tower without any tileentities! Something went wrong!");
		}
	}

	public ISidedInventory getMainInventory() {
		return this.itemTowers.get(0);
	}

	public void setAnimation(int animID) {
		for (ItemTowerTileEntity inventory : this.itemTowers)
			inventory.animate(animID);
	}

	private int getLocalSlot(int slot) {
		return slot % this.getMainInventory().getContainerSize();
	}

	private ISidedInventory getInvFromSlot(int slot) {
		int inventoryIndex = (int) Math.floor(slot / this.getMainInventory().getContainerSize());
		return (ISidedInventory) this.itemTowers.get(inventoryIndex);
	}

	@Override
	public int getContainerSize() {
		return this.getMainInventory().getContainerSize() * this.itemTowers.size();
	}

	@Override
	public boolean isEmpty() {
		for (ISidedInventory inv : itemTowers) {
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
		for (ISidedInventory inventory : this.itemTowers)
			inventory.setChanged();
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return !(player.distanceToSqr((double) this.openedFrom.getX() + 0.5D, (double) this.openedFrom.getY() + 0.5D, (double) this.openedFrom.getZ() + 0.5D) > 64.0D);
	}

	@Override
	public void startOpen(PlayerEntity player) {
		for (ISidedInventory inventory : this.itemTowers)
			inventory.startOpen(player);
	}

	@Override
	public void stopOpen(PlayerEntity player) {
		for (ISidedInventory inventory : this.itemTowers)
			inventory.stopOpen(player);
	}

	@Override
	public void clearContent() {
		for (ISidedInventory inv : itemTowers) {
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
