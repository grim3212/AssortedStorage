package com.grim3212.assorted.storage.common.inventory.keyring;

import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.item.KeyRingItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeyRingContainer extends Container {

	private int keyRingSlotId;
	public KeyRingItemHandler handler;

	public KeyRingContainer(final int windowId, final PlayerInventory playerInventory, PacketBuffer extra) {
		this(windowId, playerInventory.player.level, playerInventory.player.blockPosition(), playerInventory, playerInventory.player);
	}

	public KeyRingContainer(int openType, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		this(windowId, world, pos, playerInventory, playerEntity);
	}

	public KeyRingContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		super(StorageContainerTypes.KEY_RING.get(), windowId);

		ItemStack stack = findKeyRing(playerEntity);

		if (stack == null || stack.isEmpty()) {
			playerEntity.closeContainer();
			return;
		}

		IItemHandler itemHandler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);

		if (itemHandler instanceof KeyRingItemHandler) {
			handler = (KeyRingItemHandler) itemHandler;
			handler.load();

			int slotindex = 0;
			for (int chestRow = 0; chestRow < 3; chestRow++) {
				for (int chestCol = 0; chestCol < 4; chestCol++) {
					this.addSlot(new KeyRingSlot(handler, slotindex++, 53 + chestCol * 18, 18 + chestRow * 18));
				}
			}

			int leftCol = (184 - 168) / 2;
			int heighOffset = 113 + 3 * 18;

			for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
				for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
					this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, heighOffset - (4 - playerInvRow) * 18 - 9));
				}

			}

			for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
				this.addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, heighOffset - 23));
			}

		} else {
			playerEntity.closeContainer();
		}
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		if (keyRingSlotId == -106)
			return playerIn.getOffhandItem().getItem() instanceof KeyRingItem;
		return playerIn.inventory.getItem(keyRingSlotId).getItem() instanceof KeyRingItem;
	}

	@Override
	public ItemStack clicked(int slot, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		if (slot >= 0) {
			if (getSlot(slot).getItem().getItem() instanceof KeyRingItem)
				return ItemStack.EMPTY;
		}
		if (clickTypeIn == ClickType.SWAP)
			return ItemStack.EMPTY;

		if (slot >= 0)
			getSlot(slot).container.setChanged();
		return super.clicked(slot, dragType, clickTypeIn, player);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.getSlot(index);

		if (slot != null && slot.hasItem()) {
			int keyringSlotCount = slots.size() - playerIn.inventory.items.size();
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < keyringSlotCount) {
				if (!this.moveItemStackTo(itemstack1, keyringSlotCount, this.slots.size(), true))
					return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(itemstack1, 0, keyringSlotCount, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
		}
		return itemstack;
	}

	private ItemStack findKeyRing(PlayerEntity playerEntity) {
		PlayerInventory inv = playerEntity.inventory;

		if (playerEntity.getMainHandItem().getItem() instanceof KeyRingItem) {
			for (int i = 0; i <= 35; i++) {
				ItemStack stack = inv.getItem(i);
				if (stack == playerEntity.getMainHandItem()) {
					keyRingSlotId = i;
					return stack;
				}
			}
		} else if (playerEntity.getOffhandItem().getItem() instanceof KeyRingItem) {
			keyRingSlotId = -106;
			return playerEntity.getOffhandItem();
		} else {
			for (int i = 0; i <= 35; i++) {
				ItemStack stack = inv.getItem(i);
				if (stack.getItem() instanceof KeyRingItem) {
					keyRingSlotId = i;
					return stack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

}
