package com.grim3212.assorted.storage.common.inventory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedItemHandler;
import com.grim3212.assorted.storage.common.save.IEnderData;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class LockedEnderChestInventory implements LockedItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {

	private final IEnderData enderData;
	private final List<Reference<? extends LockedEnderChestBlockEntity>> lockedEnderChests = Lists.newArrayList();
	protected NonNullList<ItemStack> stacks;

	public LockedEnderChestInventory(IEnderData enderData, int numSlots) {
		setSize(numSlots);
		this.enderData = enderData;
	}

	public NonNullList<ItemStack> getItems() {
		return stacks;
	}
	
	public void setSize(int size) {
		stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	}

	public void addWeakListener(LockedEnderChestBlockEntity e) {
		lockedEnderChests.add(new WeakReference<>(e));
	}

	public void removeWeakListener(LockedEnderChestBlockEntity e) {
		for (Iterator<Reference<? extends LockedEnderChestBlockEntity>> itr = lockedEnderChests.iterator(); itr.hasNext();) {
			LockedEnderChestBlockEntity tileentity = itr.next().get();
			if (tileentity == null || tileentity.isRemoved() || tileentity == e) {
				itr.remove();
			}
		}
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		validateSlotIndex(slot);
		this.stacks.set(slot, stack);
		onContentsChanged(slot);
	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return this.stacks.get(slot);
	}

	protected void onContentsChanged(int slot) {
		// HA!
		List<LockedEnderChestBlockEntity> dirtyChests = Lists.newArrayList();
		for (Iterator<Reference<? extends LockedEnderChestBlockEntity>> itr = lockedEnderChests.iterator(); itr.hasNext();) {
			LockedEnderChestBlockEntity tileentity = itr.next().get();
			if (tileentity == null || tileentity.isRemoved()) {
				itr.remove();
			} else {
				dirtyChests.add(tileentity);
			}
		}

		dirtyChests.forEach(BlockEntity::setChanged);

		enderData.markDirty();
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	protected int getStackLimit(int slot, @NotNull ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if (lockedEnderChests.size() <= 0) {
			return false;
		}

		LockedEnderChestBlockEntity enderChestInv = lockedEnderChests.get(0).get();
		return enderChestInv.canPlaceItem(slot, stack);
	}

	@Override
	public CompoundTag serializeNBT() {
		ListTag nbtTagList = new ListTag();
		for (int i = 0; i < stacks.size(); i++) {
			if (!stacks.get(i).isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putInt("Slot", i);
				stacks.get(i).save(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		CompoundTag nbt = new CompoundTag();
		nbt.put("Items", nbtTagList);
		nbt.putInt("Size", stacks.size());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.size());
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, ItemStack.of(itemTags));
			}
		}
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= stacks.size())
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		if (lockedEnderChests.size() <= 0) {
			return ItemStack.EMPTY;
		}

		LockedEnderChestBlockEntity enderChestInv = lockedEnderChests.get(0).get();

		ItemStack stackInSlot = this.stacks.get(slot);

		int m;
		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
				return stack;

			if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
				return stack;

			if (!enderChestInv.canPlaceItemThroughFace(slot, stack, null) || !enderChestInv.canPlaceItem(slot, stack))
				return stack;

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m) {
				if (!simulate) {
					ItemStack copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					setStackInSlot(slot, copy);
				}

				return ItemStack.EMPTY;
			} else {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					ItemStack copy = stack.split(m);
					copy.grow(stackInSlot.getCount());
					setStackInSlot(slot, copy);
					return stack;
				} else {
					stack.shrink(m);
					return stack;
				}
			}
		} else {
			if (!enderChestInv.canPlaceItemThroughFace(slot, stack, null) || !enderChestInv.canPlaceItem(slot, stack))
				return stack;

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
			if (m < stack.getCount()) {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					setStackInSlot(slot, stack.split(m));
					return stack;
				} else {
					stack.shrink(m);
					return stack;
				}
			} else {
				if (!simulate)
					setStackInSlot(slot, stack);
				return ItemStack.EMPTY;
			}
		}
	}

	@Override
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		if (lockedEnderChests.size() <= 0) {
			return ItemStack.EMPTY;
		}

		LockedEnderChestBlockEntity enderChestInv = lockedEnderChests.get(0).get();

		ItemStack stackInSlot = this.stacks.get(slot);

		int m;
		if (!stackInSlot.isEmpty()) {
			if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot)))
				return stack;

			if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
				return stack;

			if (!enderChestInv.canPlaceItemThroughFace(slot, stack, null, inLockCode, false) || !enderChestInv.canPlaceItem(slot, stack))
				return stack;

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

			if (stack.getCount() <= m) {
				if (!simulate) {
					ItemStack copy = stack.copy();
					copy.grow(stackInSlot.getCount());
					setStackInSlot(slot, copy);
				}

				return ItemStack.EMPTY;
			} else {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					ItemStack copy = stack.split(m);
					copy.grow(stackInSlot.getCount());
					setStackInSlot(slot, copy);
					return stack;
				} else {
					stack.shrink(m);
					return stack;
				}
			}
		} else {
			if (!enderChestInv.canPlaceItemThroughFace(slot, stack, null, inLockCode, false) || !enderChestInv.canPlaceItem(slot, stack))
				return stack;

			m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
			if (m < stack.getCount()) {
				// copy the stack to not modify the original one
				stack = stack.copy();
				if (!simulate) {
					setStackInSlot(slot, stack.split(m));
					return stack;
				} else {
					stack.shrink(m);
					return stack;
				}
			} else {
				if (!simulate)
					setStackInSlot(slot, stack);
				return ItemStack.EMPTY;
			}
		}
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		if (lockedEnderChests.size() <= 0) {
			return ItemStack.EMPTY;
		}

		LockedEnderChestBlockEntity enderChestInv = lockedEnderChests.get(0).get();

		ItemStack stackInSlot = this.stacks.get(slot);

		if (stackInSlot.isEmpty())
			return ItemStack.EMPTY;

		if (!enderChestInv.canTakeItemThroughFace(slot, stackInSlot, null))
			return ItemStack.EMPTY;

		if (simulate) {
			if (stackInSlot.getCount() < amount) {
				return stackInSlot.copy();
			} else {
				ItemStack copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		} else {
			this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stackInSlot, stackInSlot.getCount() - amount));
			onContentsChanged(slot);
			return ItemHandlerHelper.copyStackWithSize(stackInSlot, amount);
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode) {
		if (amount == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		if (lockedEnderChests.size() <= 0) {
			return ItemStack.EMPTY;
		}

		LockedEnderChestBlockEntity enderChestInv = lockedEnderChests.get(0).get();

		ItemStack stackInSlot = this.stacks.get(slot);

		if (stackInSlot.isEmpty())
			return ItemStack.EMPTY;

		if (!enderChestInv.canTakeItemThroughFace(slot, stackInSlot, null, inLockCode, false))
			return ItemStack.EMPTY;

		if (simulate) {
			if (stackInSlot.getCount() < amount) {
				return stackInSlot.copy();
			} else {
				ItemStack copy = stackInSlot.copy();
				copy.setCount(amount);
				return copy;
			}
		} else {
			this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(stackInSlot, stackInSlot.getCount() - amount));
			onContentsChanged(slot);
			return ItemHandlerHelper.copyStackWithSize(stackInSlot, amount);
		}
	}

}
