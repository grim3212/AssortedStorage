package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemTowerContainer extends AbstractContainerMenu {

    private final Container inventory;

    public static ItemTowerContainer create(int windowId, Inventory inv, FriendlyByteBuf data) {
        Level world = inv.player.level;
        BlockPos pos = data.readBlockPos();

        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && te instanceof ItemTowerBlockEntity) {
            ItemTowerBlockEntity towerTileEntity = (ItemTowerBlockEntity) te;
            return new ItemTowerContainer(StorageContainerTypes.ITEM_TOWER.get(), windowId, inv, new ItemTowerInventory(towerTileEntity.getItemTowers(), pos));
        }

        return new ItemTowerContainer(StorageContainerTypes.ITEM_TOWER.get(), windowId, inv, new SimpleContainer(18));
    }

    public static ItemTowerContainer createItemTowerContainer(int windowId, Inventory playerInventory, Container inventory) {
        return new ItemTowerContainer(StorageContainerTypes.ITEM_TOWER.get(), windowId, playerInventory, inventory);
    }

    public ItemTowerContainer(MenuType<?> containerType, int windowId, Inventory playerInventory, Container inventory) {
        super(containerType, windowId);
        this.inventory = inventory;

        inventory.startOpen(playerInventory.player);

        int numRows = this.inventory.getContainerSize() / 9;

        for (int chestRow = 0; chestRow < numRows; chestRow++) {
            for (int chestCol = 0; chestCol < 9; chestCol++) {
                this.addSlot(new MoveableSlot(inventory, chestCol + chestRow * 9, 8 + chestCol * 18, 18 + chestRow * 18));
            }
        }

        int leftCol = (184 - 168) / 2;
        int heighOffset = (114 + 2 * 18);

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

    public Container getItemTowerInventory() {
        return inventory;
    }


    public void setDisplayRow(int row) {
        int minSlot = row * 9;
        int maxSlot = (row + 2) * 9;

        int numRows = this.inventory.getContainerSize() / 9;

        for (int slotIndex = 0; slotIndex < numRows * 9; slotIndex++) {
            if (row == (numRows - 1)) {
                if (slotIndex >= minSlot && slotIndex < maxSlot) {
                    int modRow = (int) Math.floor((slotIndex - minSlot) / 9.0D);
                    int modColumn = slotIndex % 9;
                    ((MoveableSlot) this.slots.get(slotIndex)).setSlotPosition(8 + modColumn * 18, 18 + modRow * 18);
                } else if (slotIndex >= 0 && slotIndex < 9) {
                    int modRow = (int) Math.floor((slotIndex + maxSlot - minSlot - 9) / 9.0D);
                    int modColumn = slotIndex % 9;
                    ((MoveableSlot) this.slots.get(slotIndex)).setSlotPosition(8 + modColumn * 18, 18 + modRow * 18);
                } else {
                    ((MoveableSlot) this.slots.get(slotIndex)).setSlotDisabled();
                }
            } else {
                if (slotIndex >= minSlot && slotIndex < maxSlot) {
                    int modRow = (int) Math.floor((slotIndex - minSlot) / 9.0D);
                    int modColumn = slotIndex % 9;
                    ((MoveableSlot) this.slots.get(slotIndex)).setSlotPosition(8 + modColumn * 18, 18 + modRow * 18);
                } else {
                    ((MoveableSlot) this.slots.get(slotIndex)).setSlotDisabled();
                }
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
