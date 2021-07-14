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
		public void setChanged() {
			super.setChanged();
			LocksmithWorkbenchContainer.this.slotsChanged(this);
		};
	};
	private final CraftResultInventory craftResult = new CraftResultInventory();
	private final IWorldPosCallable worldPosCallable;
	private String lock = "";

	public LocksmithWorkbenchContainer(int id, PlayerInventory playerInventory) {
		this(id, playerInventory, IWorldPosCallable.NULL);
	}

	public LocksmithWorkbenchContainer(int id, PlayerInventory playerInventory, IWorldPosCallable p_i50090_3_) {
		super(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), id);
		this.worldPosCallable = p_i50090_3_;
		
		this.addSlot(new Slot(this.craftResult, 0, 120, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
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
			public boolean mayPlace(ItemStack stack) {
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
		this.craftMatrix.setItem(0, ItemStack.EMPTY);
	}

	@Override
	public void slotsChanged(IInventory inventoryIn) {
		super.slotsChanged(inventoryIn);

		if (inventoryIn == this.craftMatrix) {
			this.updateLock(this.lock);
		}
	}

	@Override
	public void removed(PlayerEntity playerIn) {
		super.removed(playerIn);
		this.worldPosCallable.execute((p_217068_2_, p_217068_3_) -> {
			this.clearContainer(playerIn, p_217068_2_, this.craftMatrix);
		});
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn) {
		return stillValid(this.worldPosCallable, playerIn, StorageBlocks.LOCKSMITH_WORKBENCH.get());
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	@Override
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				this.worldPosCallable.execute((p_217067_2_, p_217067_3_) -> {
					itemstack1.getItem().onCraftedBy(itemstack1, p_217067_2_, playerIn);
				});
				if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (index >= 2 && index < 38) {
				if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
					if (index < 37) {
						if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(itemstack1, 2, 29, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemstack1, 2, 38, false)) {
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

			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			if (index == 0) {
				playerIn.drop(itemstack2, false);
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
					CompoundNBT tag = new CompoundNBT();
					code.write(tag);
					output.setTag(tag);
				}

				this.craftResult.setItem(0, output);
			} else {
				this.craftResult.setItem(0, ItemStack.EMPTY);
			}
		} else {
			this.craftResult.setItem(0, ItemStack.EMPTY);
		}

		this.broadcastChanges();
	}

}
