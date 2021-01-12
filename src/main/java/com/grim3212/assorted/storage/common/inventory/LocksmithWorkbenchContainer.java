package com.grim3212.assorted.storage.common.inventory;

import org.apache.commons.lang3.StringUtils;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.CombinationItem;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IWorldPosCallable;

public class LocksmithWorkbenchContainer extends Container {

	private final IInventory craftMatrix = new Inventory(1) {
		public void markDirty() {
			super.markDirty();
			LocksmithWorkbenchContainer.this.onCraftMatrixChanged(this);
		};
	};
	private final CraftResultInventory craftResult = new CraftResultInventory();
	private final IWorldPosCallable worldPosCallable;
	private String lock = "";

	public LocksmithWorkbenchContainer(int id, PlayerInventory playerInventory) {
		this(id, playerInventory, IWorldPosCallable.DUMMY);
	}

	public LocksmithWorkbenchContainer(int id, PlayerInventory playerInventory, IWorldPosCallable p_i50090_3_) {
		super(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), id);
		this.worldPosCallable = p_i50090_3_;
		
		this.addSlot(new Slot(this.craftResult, 0, 120, 35) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}

			@Override
			public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
				LocksmithWorkbenchContainer.this.onTake();
				return super.onTake(thePlayer, stack);
			}
		});

		this.addSlot(new Slot(this.craftMatrix, 0, 41, 17 + 18) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() instanceof CombinationItem;
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

	public static LocksmithWorkbenchContainer createContainer(int windowId, PlayerInventory playerInventory) {
		return new LocksmithWorkbenchContainer(windowId, playerInventory);
	}

	public static LocksmithWorkbenchContainer createContainer(int windowId, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
		return new LocksmithWorkbenchContainer(windowId, playerInventory, worldPosCallable);
	}

	private void onTake() {
		this.craftMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		super.onCraftMatrixChanged(inventoryIn);

		if (inventoryIn == this.craftMatrix) {
			this.updateLock(this.lock);
		}
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.worldPosCallable.consume((p_217068_2_, p_217068_3_) -> {
			this.clearContainer(playerIn, p_217068_2_, this.craftMatrix);
		});
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(this.worldPosCallable, playerIn, StorageBlocks.LOCKSMITH_WORKBENCH.get());
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index == 0) {
				this.worldPosCallable.consume((p_217067_2_, p_217067_3_) -> {
					itemstack1.getItem().onCreated(itemstack1, p_217067_2_, playerIn);
				});
				if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (index >= 2 && index < 38) {
				if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
					if (index < 37) {
						if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.mergeItemStack(itemstack1, 2, 29, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			if (index == 0) {
				playerIn.dropItem(itemstack2, false);
			}
		}

		return itemstack;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot that
	 * was double-clicked.
	 */
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}

	public void updateLock(String lock) {
		this.lock = lock;
		ItemStack itemstack = this.craftMatrix.getStackInSlot(0);

		if (!itemstack.isEmpty() && itemstack.getItem() instanceof CombinationItem) {
			if (!StringUtils.isBlank(lock)) {
				StorageLockCode code = new StorageLockCode(lock);

				ItemStack output = itemstack.copy();

				if (output.hasTag()) {
					code.write(output.getTag());
				} else {
					CompoundNBT tag = new CompoundNBT();
					code.write(tag);
					output.setTag(tag);
				}

				this.craftResult.setInventorySlotContents(0, output);
			} else {
				this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
			}
		} else {
			this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
		}

		this.detectAndSendChanges();
	}

}
