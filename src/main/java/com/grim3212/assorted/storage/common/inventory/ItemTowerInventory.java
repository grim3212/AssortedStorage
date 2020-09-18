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
		return slot % this.getMainInventory().getSizeInventory();
	}

	private ISidedInventory getInvFromSlot(int slot) {
		int inventoryIndex = (int) Math.floor(slot / this.getMainInventory().getSizeInventory());
		return (ISidedInventory) this.itemTowers.get(inventoryIndex);
	}

	@Override
	public int getSizeInventory() {
		return this.getMainInventory().getSizeInventory() * this.itemTowers.size();
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
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return getInvFromSlot(index).isItemValidForSlot(getLocalSlot(index), stack);
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
		for (ISidedInventory inventory : this.itemTowers)
			inventory.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return !(player.getDistanceSq((double) this.openedFrom.getX() + 0.5D, (double) this.openedFrom.getY() + 0.5D, (double) this.openedFrom.getZ() + 0.5D) > 64.0D);
	}

	@Override
	public void openInventory(PlayerEntity player) {
		for (ISidedInventory inventory : this.itemTowers)
			inventory.openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		for (ISidedInventory inventory : this.itemTowers)
			inventory.closeInventory(player);
	}

	@Override
	public void clear() {
		for (ISidedInventory inv : itemTowers) {
			inv.clear();
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getSizeInventory()).toArray();
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return this.getMainInventory().canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return this.getMainInventory().canInsertItem(index, stack, direction);
	}
}
