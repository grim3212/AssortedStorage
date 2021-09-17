package com.grim3212.assorted.storage.common.save;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class EnderSavedData extends WorldSavedData implements IEnderData {

	private static final String ID = new ResourceLocation(AssortedStorage.MODID, "ender_saved_data").toString();
	private static final String LOCKED_ENDER_TAG = "LockedEnderChests";

	private EnderData enderData = new EnderData();

	public EnderSavedData() {
		super(ID);
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

	public static EnderSavedData get(World world) {
		if (!(world instanceof ServerWorld)) {
			return DUMMY_SAVE;
		}
		ServerWorld overworld = world.getServer().overworld();

		DimensionSavedDataManager storage = overworld.getDataStorage();
		return storage.computeIfAbsent(EnderSavedData::new, ID);
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
	public void load(CompoundNBT nbt) {
		enderData.deserializeNBT(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		CompoundNBT data = enderData.serializeNBT();

		nbt.put(LOCKED_ENDER_TAG, data.get(LOCKED_ENDER_TAG));

		return nbt;
	}

	private class EnderData implements INBTSerializable<CompoundNBT>, IEnderData {
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
		public CompoundNBT serializeNBT() {
			CompoundNBT tag = new CompoundNBT();
			ListNBT inventories = new ListNBT();

			for (Map.Entry<String, LockedEnderChestInventory> entry : this.enderChests.entrySet()) {
				LockedEnderChestInventory inventory = entry.getValue();

				CompoundNBT chest = new CompoundNBT();
				chest.putString("Code", entry.getKey());
				chest.put("Inventory", inventory.serializeNBT());
				inventories.add(chest);
			}

			tag.put(LOCKED_ENDER_TAG, inventories);
			return tag;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			ListNBT enderChestsNBT = nbt.getList(LOCKED_ENDER_TAG, Constants.NBT.TAG_COMPOUND);

			this.enderChests.clear();

			for (int i = 0; i < enderChestsNBT.size(); ++i) {
				CompoundNBT chest = enderChestsNBT.getCompound(i);
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
