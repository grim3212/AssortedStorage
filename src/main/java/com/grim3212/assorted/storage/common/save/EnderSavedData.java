package com.grim3212.assorted.storage.common.save;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class EnderSavedData extends SavedData implements IEnderData {

	private static final String ID = new ResourceLocation(AssortedStorage.MODID, "ender_saved_data").toString();
	private static final String LOCKED_ENDER_TAG = "LockedEnderChests";

	private EnderData enderData = new EnderData();

	public EnderSavedData() {
	}

	public EnderSavedData(CompoundTag nbt) {
		enderData.deserializeNBT(nbt);
	}

	private static EnderSavedData DUMMY_SAVE = new EnderSavedData() {
		private final LockedEnderChestInventory inv = new LockedEnderChestInventory(this, 27) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return stack;
			}

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return false;
			}
		};

		@Override
		public LockedEnderChestInventory getInventory(StorageLockCode code) {
			return inv;
		}
	};

	public static EnderSavedData get(Level world) {
		if (!(world instanceof ServerLevel)) {
			return DUMMY_SAVE;
		}
		ServerLevel overworld = world.getServer().overworld();

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(EnderSavedData::new, EnderSavedData::new, ID);
	}

	@Override
	public void markDirty() {
		setDirty();
	}

	@Override
	public LockedEnderChestInventory getInventory(StorageLockCode code) {
		return enderData.getInventory(code);
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		CompoundTag data = enderData.serializeNBT();

		nbt.put(LOCKED_ENDER_TAG, data.get(LOCKED_ENDER_TAG));

		return nbt;
	}

	private class EnderData implements INBTSerializable<CompoundTag>, IEnderData {
		private Map<String, LockedEnderChestInventory> enderChests = Maps.newHashMap();

		@Override
		public LockedEnderChestInventory getInventory(StorageLockCode code) {
			LockedEnderChestInventory inventory = enderChests.get(code.getLockCode());

			if (inventory == null) {
				inventory = new LockedEnderChestInventory(this, 27);
				enderChests.put(code.getLockCode(), inventory);
			}

			return inventory;
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag tag = new CompoundTag();
			ListTag inventories = new ListTag();

			for (Map.Entry<String, LockedEnderChestInventory> entry : this.enderChests.entrySet()) {
				LockedEnderChestInventory inventory = entry.getValue();

				CompoundTag chest = new CompoundTag();
				chest.putString("Code", entry.getKey());
				chest.put("Inventory", inventory.serializeNBT());
				inventories.add(chest);
			}

			tag.put(LOCKED_ENDER_TAG, inventories);
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			ListTag enderChestsNBT = nbt.getList(LOCKED_ENDER_TAG, Constants.NBT.TAG_COMPOUND);

			this.enderChests.clear();

			for (int i = 0; i < enderChestsNBT.size(); ++i) {
				CompoundTag chest = enderChestsNBT.getCompound(i);
				LockedEnderChestInventory inventory = new LockedEnderChestInventory(this, 27);
				inventory.deserializeNBT(chest.getCompound("Inventory"));
				this.enderChests.put(chest.getString("Code"), inventory);
			}
		}

		@Override
		public void markDirty() {
			setDirty();
		}
	}
}
