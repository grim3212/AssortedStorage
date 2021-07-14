package com.grim3212.assorted.storage.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class StorageContainer extends Container {

	private final IInventory inventory;

	public static StorageContainer createGlassCabinetContainer(int windowId, PlayerInventory playerInventory) {
		return new StorageContainer(StorageContainerTypes.GLASS_CABINET.get(), windowId, playerInventory, new Inventory(27));
	}

	public static StorageContainer createGlassCabinetContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new StorageContainer(StorageContainerTypes.GLASS_CABINET.get(), windowId, playerInventory, inventory);
	}

	public static StorageContainer createWoodCabinetContainer(int windowId, PlayerInventory playerInventory) {
		return new StorageContainer(StorageContainerTypes.WOOD_CABINET.get(), windowId, playerInventory, new Inventory(27));
	}

	public static StorageContainer createWoodCabinetContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new StorageContainer(StorageContainerTypes.WOOD_CABINET.get(), windowId, playerInventory, inventory);
	}

	public static StorageContainer createWarehouseCrateContainer(int windowId, PlayerInventory playerInventory) {
		return new StorageContainer(StorageContainerTypes.WAREHOUSE_CRATE.get(), windowId, playerInventory, new Inventory(27));
	}

	public static StorageContainer createWarehouseCrateContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new StorageContainer(StorageContainerTypes.WAREHOUSE_CRATE.get(), windowId, playerInventory, inventory);
	}

	public static StorageContainer createGoldSafeContainer(int windowId, PlayerInventory playerInventory) {
		return new StorageContainer(StorageContainerTypes.GOLD_SAFE.get(), windowId, playerInventory, new Inventory(36));
	}

	public static StorageContainer createGoldSafeContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new StorageContainer(StorageContainerTypes.GOLD_SAFE.get(), windowId, playerInventory, inventory);
	}

	public static StorageContainer createObsidianSafeContainer(int windowId, PlayerInventory playerInventory) {
		return new StorageContainer(StorageContainerTypes.OBSIDIAN_SAFE.get(), windowId, playerInventory, new Inventory(27));
	}

	public static StorageContainer createObsidianSafeContainer(int windowId, PlayerInventory playerInventory, IInventory inventory) {
		return new StorageContainer(StorageContainerTypes.OBSIDIAN_SAFE.get(), windowId, playerInventory, inventory);
	}
	
	

	public StorageContainer(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, IInventory inventory) {
		super(containerType, windowId);
		this.inventory = inventory;

		int numRows = inventory.getContainerSize() / 9;

		inventory.startOpen(playerInventory.player);

		for (int chestRow = 0; chestRow < numRows; chestRow++) {
			for (int chestCol = 0; chestCol < 9; chestCol++) {
				this.addSlot(new Slot(inventory, chestCol + chestRow * 9, 8 + chestCol * 18, 18 + chestRow * 18));
			}
		}

		int leftCol = (184 - 168) / 2;
		int heighOffset = 113 + numRows * 18;

		for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
			for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
				this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, heighOffset - (4 - playerInvRow) * 18 - 10));
			}

		}

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, heighOffset - 24));
		}
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		return this.inventory.stillValid(playerIn);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
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
	public void removed(PlayerEntity playerIn) {
		super.removed(playerIn);
		this.inventory.stopOpen(playerIn);
	}

}
