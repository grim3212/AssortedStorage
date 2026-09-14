package com.grim3212.assorted.storage.api;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class LargeItemStack {
	private static int MAX_ROTATION = 15;

	private ItemStack stack;
	private int amount;
	private int rotation;
	private boolean locked;

	public LargeItemStack(ItemStack stack, int amount) {
		this(stack, amount, 0, false);
	}

	public LargeItemStack(ItemStack stack, int amount, int rotation, boolean locked) {
		this.stack = stack;
		this.amount = amount;
		this.rotation = rotation;
		this.locked = locked;
	}

	public static LargeItemStack empty() {
		return new LargeItemStack(ItemStack.EMPTY, 0);
	}

	public LargeItemStack withStack(ItemStack stack) {
		return new LargeItemStack(stack, stack.getCount(), this.getRotation(), this.isLocked());
	}

	/** Detached copy. The only lossless snapshot of a slot; an ItemStack cannot carry the rest. */
	public LargeItemStack copy() {
		return new LargeItemStack(this.stack.copy(), this.amount, this.rotation, this.locked);
	}
	
	public LargeItemStack with(ItemStack stack, int amount) {
		return new LargeItemStack(stack, amount, this.getRotation(), this.isLocked());
	}

	public boolean isLocked() {
		return locked;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
	}

	public void setEmpty() {
		this.stack = ItemStack.EMPTY;
		this.amount = 0;
	}

	public void setLock(boolean lock) {
		this.locked = lock;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		if (rotation > -1 && rotation <= MAX_ROTATION)
			this.rotation = rotation;
	}

	public void cycleRotation() {
		int newRot = this.rotation + 1 > MAX_ROTATION ? 0 : this.rotation + 1;
		setRotation(newRot);
	}

	public boolean isEmpty() {
		return this.locked ? stack.isEmpty() : stack.isEmpty() || amount <= 0;
	}
	
	public ItemStack split(int amount, boolean allowEmpty) {
		// One stack at a time, sized by the item rather than a flat 64.
		int i = Math.min(Math.min(amount, this.amount), this.stack.getMaxStackSize());
		ItemStack itemstack = this.stack.copy();
		itemstack.setCount(i);
		this.amount -= i;

		if (this.amount <= 0) {
			if (!this.locked && allowEmpty)
				this.stack = ItemStack.EMPTY;
			this.amount = 0;
		}

		return itemstack;
	}

	public ItemStack takeStack() {
		return split(this.amount, true);
	}

	public void setItem(ItemStack stack) {
		this.setAmount(stack.getCount());
		this.setStack(stack);
	}

	public NonNullList<ItemStack> asItemStacks() {
		NonNullList<ItemStack> stacks = NonNullList.create();

		int maxStackSize = this.stack.getMaxStackSize();
		
		int numStacks = Math.round(this.amount / maxStackSize);
		int remainder = this.amount % maxStackSize;
		for (int i = 0; i < numStacks; i++) {
			stacks.add(this.stack.copyWithCount(maxStackSize));
		}
		if (remainder > 0) {
			stacks.add(this.stack.copyWithCount(remainder));
		}

		return stacks;
	}
}
