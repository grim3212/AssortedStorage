package com.grim3212.assorted.storage.common.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockerContainer extends AbstractContainerMenu {

    private final Container inventory;
    private final int numRows;

    public static LockerContainer createLockerContainer(int windowId, Inventory playerInventory, FriendlyByteBuf byteBuf) {
        return new LockerContainer(StorageContainerTypes.LOCKER.get(), windowId, playerInventory, new SimpleContainer(45), 5);
    }

    public static LockerContainer createLockerContainer(int windowId, Inventory playerInventory, Container inventory) {
        return new LockerContainer(StorageContainerTypes.LOCKER.get(), windowId, playerInventory, inventory, 5);
    }

    public static LockerContainer createDualLockerContainer(int windowId, Inventory playerInventory, FriendlyByteBuf byteBuf) {
        return new LockerContainer(StorageContainerTypes.DUAL_LOCKER.get(), windowId, playerInventory, new SimpleContainer(90), 10);
    }

    public static LockerContainer createDualLockerContainer(int windowId, Inventory playerInventory, Container inventory) {
        return new LockerContainer(StorageContainerTypes.DUAL_LOCKER.get(), windowId, playerInventory, inventory, 10);
    }

    public LockerContainer(MenuType<?> containerType, int windowId, Inventory playerInventory, Container inventory, int numRows) {
        super(containerType, windowId);
        this.inventory = inventory;
        this.numRows = numRows;

        inventory.startOpen(playerInventory.player);

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

        if (playerInventory.player.level.isClientSide)
            setDisplayRow(0);
    }


    public void setDisplayRow(int row) {
        int minSlot = row * 9;
        int maxSlot = (row + 5) * 9;

        for (int slotIndex = 0; slotIndex < this.numRows * 9; slotIndex++) {
            if ((slotIndex >= minSlot) && (slotIndex < maxSlot)) {
                int modRow = (int) Math.floor((slotIndex - minSlot) / 9.0D);
                int modColumn = slotIndex % 9;
                ((MoveableSlot) this.slots.get(slotIndex)).setSlotPosition(8 + modColumn * 18, 18 + modRow * 18);
            } else {
                ((MoveableSlot) this.slots.get(slotIndex)).setSlotDisabled();
            }
        }
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
