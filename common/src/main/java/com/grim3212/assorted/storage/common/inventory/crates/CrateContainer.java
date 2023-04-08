package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrateContainer extends AbstractContainerMenu {

    private final CrateBlockEntity crateBlockEntity;

    public static CrateContainer createCrateContainer(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        CrateBlockEntity crate = getCrateBlockEntity(playerInventory, data.readBlockPos());
        return new CrateContainer(StorageContainerTypes.CRATE.get(), windowId, playerInventory, crate);
    }

    protected static CrateBlockEntity getCrateBlockEntity(Inventory playerInv, BlockPos pos) {
        Level level = playerInv.player.getCommandSenderWorld();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrateBlockEntity crate) {
            return crate;
        }
        return null;
    }

    public CrateContainer(MenuType<? extends CrateContainer> menuType, int windowId, Inventory playerInventory, CrateBlockEntity crate) {
        super(menuType, windowId);
        this.crateBlockEntity = crate;
        IItemStorageHandler inventory = crate.getItemStackStorageHandler();

        inventory.startOpen(playerInventory.player);

        // Lock slot
        this.addSlot(new CrateLockSlot(inventory, 0, 180, 18));

        for (int enhancement = 1; enhancement < 9; enhancement++) {
            this.addSlot(new EnhancementSlot(inventory, enhancement, (18 * enhancement) - 1, 74));
        }

        switch (crate.getLayout()) {
            case SINGLE:
                this.addSlot(new LargeItemStackSlot(inventory, 0, 80, 34));
                break;
            case DOUBLE:
                this.addSlot(new LargeItemStackSlot(inventory, 0, 80, 23));
                this.addSlot(new LargeItemStackSlot(inventory, 1, 80, 45));
                break;
            case TRIPLE:
                this.addSlot(new LargeItemStackSlot(inventory, 0, 80, 23));
                this.addSlot(new LargeItemStackSlot(inventory, 1, 69, 45));
                this.addSlot(new LargeItemStackSlot(inventory, 2, 91, 45));
                break;
            case QUADRUPLE:
                this.addSlot(new LargeItemStackSlot(inventory, 0, 69, 23));
                this.addSlot(new LargeItemStackSlot(inventory, 1, 91, 23));
                this.addSlot(new LargeItemStackSlot(inventory, 2, 69, 45));
                this.addSlot(new LargeItemStackSlot(inventory, 3, 91, 45));
                break;
        }

        int leftOffset = 8;
        int heighOffset = 188;

        for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
            for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
                this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, leftOffset + playerInvCol * 18, heighOffset - (4 - playerInvRow) * 18 - 10));
            }

        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, leftOffset + hotbarSlot * 18, heighOffset - 24));
        }
    }

    public CrateBlockEntity getCrateBlockEntity() {
        return this.crateBlockEntity;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return this.getCrateBlockEntity().getItemStackStorageHandler().stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int maxSlot = this.getCrateBlockEntity().getItemStackStorageHandler().getEnhancements().size();

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
        this.getCrateBlockEntity().getItemStackStorageHandler().stopOpen(playerIn);
    }

}
