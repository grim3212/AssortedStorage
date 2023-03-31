package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.lib.core.inventory.locking.StorageLockCode;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.CombinationItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class LocksmithWorkbenchContainer extends AbstractContainerMenu {

    private final Container craftMatrix = new SimpleContainer(1) {
        public void setChanged() {
            super.setChanged();
            LocksmithWorkbenchContainer.this.slotsChanged(this);
        }

        ;
    };
    private final ResultContainer craftResult = new ResultContainer();
    private final ContainerLevelAccess worldPosCallable;
    private String lock = "";
    private final Slot resultSlot;

    public LocksmithWorkbenchContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL);
    }

    public LocksmithWorkbenchContainer(int id, Inventory playerInventory, ContainerLevelAccess p_i50090_3_) {
        super(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), id);
        this.worldPosCallable = p_i50090_3_;

        this.addSlot(new Slot(this.craftMatrix, 0, 41, 17 + 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CombinationItem;
            }
        });

        this.resultSlot = this.addSlot(new Slot(this.craftResult, 1, 120, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player thePlayer, ItemStack stack) {
                LocksmithWorkbenchContainer.this.onTake();
                super.onTake(thePlayer, stack);
            }
        });

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

    }

    public static LocksmithWorkbenchContainer createContainer(int windowId, Inventory playerInventory, FriendlyByteBuf byteBuf) {
        return new LocksmithWorkbenchContainer(windowId, playerInventory);
    }

    public static LocksmithWorkbenchContainer createContainer(int windowId, Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        return new LocksmithWorkbenchContainer(windowId, playerInventory, worldPosCallable);
    }

    private void onTake() {
        this.craftMatrix.setItem(0, ItemStack.EMPTY);
    }

    @Override
    public void slotsChanged(Container inventoryIn) {
        super.slotsChanged(inventoryIn);

        if (inventoryIn == this.craftMatrix) {
            this.updateLock(this.lock);
        }
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.worldPosCallable.execute((p_217068_2_, p_217068_3_) -> {
            this.clearContainer(playerIn, craftMatrix);
        });
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(this.worldPosCallable, playerIn, StorageBlocks.LOCKSMITH_WORKBENCH.get());
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this
     * moves the stack between the player inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 1) {
                this.worldPosCallable.execute((p_217067_2_, p_217067_3_) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, p_217067_2_, playerIn);
                });
                if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.getItem() instanceof CombinationItem) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38 && !this.moveItemStackTo(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging
     * (double-click) code. The stack passed in is null for the initial slot that
     * was double-clicked.
     */
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    public void updateLock(String lock) {
        this.lock = lock;
        ItemStack itemstack = this.craftMatrix.getItem(0);

        if (!itemstack.isEmpty() && itemstack.getItem() instanceof CombinationItem) {
            if (!StringUtils.isBlank(lock)) {
                StorageLockCode code = new StorageLockCode(lock);

                ItemStack output = itemstack.copy();

                if (output.hasTag()) {
                    code.write(output.getTag());
                } else {
                    CompoundTag tag = new CompoundTag();
                    code.write(tag);
                    output.setTag(tag);
                }

                this.resultSlot.set(output);
            } else {
                this.resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }

        this.broadcastChanges();
    }

}
