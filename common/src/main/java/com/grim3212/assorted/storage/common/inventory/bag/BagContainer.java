package com.grim3212.assorted.storage.common.inventory.bag;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.item.BagItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BagContainer extends AbstractContainerMenu {

    private int bagSlotId;
    public BagItemHandler handler;
    private StorageMaterial storageMaterial;

    public BagContainer(final int windowId, final Inventory playerInventory, FriendlyByteBuf extra) {
        this(windowId, playerInventory.player.level, playerInventory.player.blockPosition(), playerInventory, playerInventory.player);
    }

    public BagContainer(int openType, int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        this(windowId, world, pos, playerInventory, playerEntity);
    }

    public BagContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        super(StorageContainerTypes.BAG.get(), windowId);

        ItemStack stack = findBag(playerEntity);

        if (stack == null || stack.isEmpty()) {
            playerEntity.closeContainer();
            return;
        }

        this.storageMaterial = ((BagItem) stack.getItem()).getStorageMaterial();

        IItemStorageHandler storageHandler = Services.INVENTORY.getItemStorageHandler(stack).orElse(null);

        if (storageHandler instanceof BagItemHandler) {
            handler = (BagItemHandler) storageHandler;
            handler.load();

            int xRows = 3;
            int yCols = 9;

            if (this.storageMaterial != null) {
                xRows = storageMaterial.getXRows();
                yCols = storageMaterial.getYCols();
            }

            this.addSlot(new BagLockSlot(storageHandler, 0, 18 + yCols * 18, 18));

            for (int chestRow = 0; chestRow < xRows; chestRow++) {
                for (int chestCol = 0; chestCol < yCols; chestCol++) {
                    this.addSlot(new BagSlot(storageHandler, (chestCol + chestRow * yCols) + 1, 8 + chestCol * 18, 18 + chestRow * 18));
                }
            }

            int leftOffset = Math.max(yCols - 9, 0) * 9;
            int leftCol = ((184 - 168) / 2) + leftOffset;
            int heighOffset = 113 + xRows * 18;

            for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
                for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                    this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftCol + playerInvCol * 18, heighOffset - (4 - playerInvRow) * 18 - 10));
                }

            }

            for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
                this.addSlot(new Slot(playerInventory, hotbarSlot, leftCol + hotbarSlot * 18, heighOffset - 24));
            }

        } else {
            playerEntity.closeContainer();
        }
    }

    public StorageMaterial getStorageMaterial() {
        return storageMaterial;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        if (bagSlotId == -106)
            return playerIn.getOffhandItem().getItem() instanceof BagItem;
        return playerIn.getInventory().getItem(bagSlotId).getItem() instanceof BagItem;
    }

    @Override
    public void clicked(int slot, int dragType, ClickType clickTypeIn, Player player) {
        if (slot >= 0) {
            if (getSlot(slot).getItem().getItem() instanceof BagItem)
                return;
        }
        if (clickTypeIn == ClickType.SWAP)
            return;

        if (slot >= 0)
            getSlot(slot).container.setChanged();

        super.clicked(slot, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;

        Slot slot = this.getSlot(index);

        if (slot != null && slot.hasItem()) {
            // -1 to account for the lock slot we don't want to put items in
            int totalSlotSize = slots.size() - 1;
            int bagSlotCount = totalSlotSize - playerIn.getInventory().items.size();
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < bagSlotCount) {
                if (!this.moveItemStackTo(itemstack1, bagSlotCount, totalSlotSize, true))
                    return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(itemstack1, 1, bagSlotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }
        return itemstack;
    }

    private ItemStack findBag(Player playerEntity) {
        Inventory inv = playerEntity.getInventory();

        if (playerEntity.getMainHandItem().getItem() instanceof BagItem) {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack == playerEntity.getMainHandItem()) {
                    bagSlotId = i;
                    return stack;
                }
            }
        } else if (playerEntity.getOffhandItem().getItem() instanceof BagItem) {
            bagSlotId = -106;
            return playerEntity.getOffhandItem();
        } else {
            for (int i = 0; i <= 35; i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.getItem() instanceof BagItem) {
                    bagSlotId = i;
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

}
