package com.grim3212.assorted.storage.common.inventory.bag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.StorageUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BagItemHandler extends ItemStackHandler {

	private ItemStack itemStack;
	private boolean changed = false;
	private boolean loaded = false;
	private final StorageMaterial material;

	public BagItemHandler(ItemStack itemStack, @Nullable StorageMaterial material) {
		// Extra slot for the lock slot
		super(material == null ? 28 : material.totalItems() + 1);
		this.itemStack = itemStack;
		this.material = material;
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

	public void load(@Nonnull CompoundTag nbt) {
		if (nbt.contains("Inventory"))
			deserializeNBT(nbt.getCompound("Inventory"));
	}

	public void save() {
		if (changed) {
			CompoundTag nbt = itemStack.getOrCreateTag();
			nbt.put("Inventory", serializeNBT());

			// If we set the storage lock on each save it should be a bit more performant
			// then checking the inventory every render to decide if it is locked or not
			ItemStack storageLock = this.getStackInSlot(0);
			String newLockCode = StorageUtil.getCode(storageLock);
			nbt.putString("Storage_Lock", newLockCode);
			changed = false;
		}
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// Extra slot for the lock slot
		setSize(this.material == null ? 28 : this.material.totalItems() + 1);
		ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
		for (int i = 0; i < tagList.size(); i++) {
			CompoundTag itemTags = tagList.getCompound(i);
			int slot = itemTags.getInt("Slot");

			if (slot >= 0 && slot < stacks.size()) {
				stacks.set(slot, ItemStack.of(itemTags));
			}
		}
		onLoad();
	}
}
