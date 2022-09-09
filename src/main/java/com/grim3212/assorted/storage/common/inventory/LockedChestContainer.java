package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockedChestContainer extends AbstractContainerMenu {

	private final Container inventory;
	private final StorageMaterial storageMaterial;

	public LockedChestContainer(int windowId, Inventory inventory, StorageMaterial storageMaterial) {
		this(StorageContainerTypes.CHESTS.get(storageMaterial).get(), windowId, inventory, new SimpleContainer(storageMaterial.totalItems()), storageMaterial);
	}

	public LockedChestContainer(MenuType<LockedChestContainer> menuType, int windowId, Inventory playerInventory, Container inventory, StorageMaterial storageMaterial) {
		super(menuType, windowId);
		this.inventory = inventory;
		this.storageMaterial = storageMaterial;

		inventory.startOpen(playerInventory.player);

		for (int chestRow = 0; chestRow < storageMaterial.getXRows(); chestRow++) {
			for (int chestCol = 0; chestCol < storageMaterial.getYCols(); chestCol++) {
				this.addSlot(new Slot(inventory, chestCol + chestRow * storageMaterial.getYCols(), 8 + chestCol * 18, 18 + chestRow * 18));
			}
		}

		int leftOffset = Math.max(storageMaterial.getYCols() - 9, 0) * 9;
		int leftCol = ((184 - 168) / 2) + leftOffset;
		int heighOffset = 113 + storageMaterial.getXRows() * 18;

		for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
			for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
				this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, heighOffset - (4 - playerInvRow) * 18 - 10));
			}

		}

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, heighOffset - 24));
		}
	}

	public StorageMaterial getStorageMaterial() {
		return storageMaterial;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return this.inventory.stillValid(playerIn);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			int maxSlot = this.inventory.getContainerSize();

			if (index < maxSlot) {
				if (!this.moveItemStackTo(itemstack1, maxSlot, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, maxSlot, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		this.inventory.stopOpen(playerIn);
	}

}
