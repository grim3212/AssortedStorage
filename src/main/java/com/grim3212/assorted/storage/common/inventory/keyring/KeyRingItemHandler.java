package com.grim3212.assorted.storage.common.inventory.keyring;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class KeyRingItemHandler extends ItemStackHandler {

	private ItemStack itemStack;
	private boolean changed = false;
	private boolean loaded = false;
	public static final int KEY_RING_SIZE = 12;

	public KeyRingItemHandler(ItemStack itemStack) {
		super(KEY_RING_SIZE);
		this.itemStack = itemStack;
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		changed = true;
		return super.insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		changed = true;
		return super.extractItem(slot, amount, simulate);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		if (!ItemStack.isSame(stack, stacks.get(slot))) {
			onContentsChanged(slot);
		}
		this.stacks.set(slot, stack);
	}

	public void setDirty() {
		this.changed = true;
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		changed = true;
	}

	public void load() {
		load(itemStack.getOrCreateTag());
	}

	public void loadIfNotLoaded() {
		if (!loaded)
			load();
		loaded = true;
	}

	public void load(@Nonnull CompoundNBT nbt) {
		if (nbt.contains("Inventory"))
			deserializeNBT(nbt.getCompound("Inventory"));
	}

	public void save() {
		if (changed) {
			CompoundNBT nbt = itemStack.getOrCreateTag();
			nbt.put("Inventory", serializeNBT());
			changed = false;
		}
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setSize(KEY_RING_SIZE);
		ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, ItemStack.of(itemTags));
			}
		}
		onLoad();
	}
}
