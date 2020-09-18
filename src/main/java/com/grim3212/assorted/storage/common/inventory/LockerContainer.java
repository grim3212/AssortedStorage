package com.grim3212.assorted.storage.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LockerContainer extends Container {

	private final IInventory inventory;
	private final int numRows;

	public static LockerContainer createLockerContainer(int windowId, PlayerInventory playerInventory) {
		return new LockerContainer(StorageContainerTypes.LOCKER.get(), windowId, playerInventory, new Inventory(45), 5);
	}

	public static LockerContainer createLockerContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new LockerContainer(StorageContainerTypes.LOCKER.get(), windowId, playerInventory, inventory, 5);
	}

	public static LockerContainer createDualLockerContainer(int windowId, PlayerInventory playerInventory) {
		return new LockerContainer(StorageContainerTypes.DUAL_LOCKER.get(), windowId, playerInventory, new Inventory(90), 10);
	}

	public static LockerContainer createDualLockerContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new LockerContainer(StorageContainerTypes.DUAL_LOCKER.get(), windowId, playerInventory, inventory, 10);
	}

	public LockerContainer(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, IInventory inventory, int numRows) {
		super(containerType, windowId);
		this.inventory = inventory;
		this.numRows = numRows;

		inventory.openInventory(playerInventory.player);

		for (int chestRow = 0; chestRow < numRows; chestRow++) {
			for (int chestCol = 0; chestCol < 9; chestCol++) {
				this.addSlot(new MoveableSlot(inventory, chestCol + chestRow * 9, 8 + chestCol * 18, 18 + chestRow * 18));
			}
		}

		int leftCol = (184 - 168) / 2;
		int heighOffset = (113 + 5 * 18);
		if (this.numRows > 5)
			heighOffset++;

		for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
			for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
				this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, heighOffset - (4 - playerInvRow) * 18 - 10));
			}

		}

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, heighOffset - 24));
		}

		if (playerInventory.player.world.isRemote)
			setDisplayRow(0);
	}

	@OnlyIn(Dist.CLIENT)
	public void setDisplayRow(int row) {
		int minSlot = row * 9;
		int maxSlot = (row + 5) * 9;

		for (int slotIndex = 0; slotIndex < this.numRows * 9; slotIndex++) {
			if ((slotIndex >= minSlot) && (slotIndex < maxSlot)) {
				int modRow = (int) Math.floor((slotIndex - minSlot) / 9.0D);
				int modColumn = slotIndex % 9;
				((MoveableSlot) this.inventorySlots.get(slotIndex)).setSlotPosition(8 + modColumn * 18, 18 + modRow * 18);
			} else {
				((MoveableSlot) this.inventorySlots.get(slotIndex)).setSlotDisabled();
			}
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return this.inventory.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			int maxSlot = this.inventory.getSizeInventory();

			if (index < maxSlot) {
				if (!this.mergeItemStack(itemstack1, maxSlot, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, maxSlot, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.inventory.closeInventory(playerIn);
	}
}
