package com.grim3212.assorted.storage.common.save;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.core.inventory.locking.StorageLockCode;
import com.grim3212.assorted.lib.util.ITagSerializable;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.LockedEnderChestInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import javax.annotation.Nonnull;
import java.util.Map;

public class EnderSavedData extends SavedData implements IEnderData {

    private static final String OLD_ID = new ResourceLocation(Constants.MOD_ID, "ender_saved_data").toString();
    private static final String ID = "locked_ender_saved_data";
    private static final String LOCKED_ENDER_TAG = "LockedEnderChests";

    private EnderData enderData = new EnderData();

    public EnderSavedData() {
    }

    public EnderSavedData(CompoundTag nbt) {
        enderData.deserializeNBT(nbt);
    }

    private static EnderSavedData DUMMY_SAVE = new EnderSavedData() {
        private final LockedEnderChestInventory inv = new LockedEnderChestInventory(this, "", 27) {
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
        EnderSavedData oldData = storage.get(EnderSavedData::new, OLD_ID);
        EnderSavedData newData = storage.get(EnderSavedData::new, ID);

        if (oldData != null && newData == null) {
            return storage.computeIfAbsent(EnderSavedData::new, () -> oldData, ID);
        }

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

    private class EnderData implements IEnderData, ITagSerializable<CompoundTag> {
        private Map<String, LockedEnderChestInventory> enderChests = Maps.newHashMap();

        @Override
        public LockedEnderChestInventory getInventory(StorageLockCode code) {
            LockedEnderChestInventory inventory = enderChests.get(code.getLockCode());

            if (inventory == null) {
                inventory = new LockedEnderChestInventory(this, code.getLockCode(), 27);
                enderChests.put(code.getLockCode(), inventory);
            }

            return inventory;
        }

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

        public void deserializeNBT(CompoundTag nbt) {
            ListTag enderChestsNBT = nbt.getList(LOCKED_ENDER_TAG, Tag.TAG_COMPOUND);

            this.enderChests.clear();

            for (int i = 0; i < enderChestsNBT.size(); ++i) {
                CompoundTag chest = enderChestsNBT.getCompound(i);
                String lockCode = chest.getString("Code");
                LockedEnderChestInventory inventory = new LockedEnderChestInventory(this, lockCode, 27);
                inventory.deserializeNBT(chest.getCompound("Inventory"));
                this.enderChests.put(lockCode, inventory);
            }
        }

        @Override
        public void markDirty() {
            setDirty();
        }
    }
}
