package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.storage.common.block.blockentity.StorageCrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageCrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StorageCrateCompactingContainer extends StorageCrateContainer {

	public static StorageCrateCompactingContainer createCrateContainer(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
		StorageCrateCompactingBlockEntity crate = getCrateBlockEntity(playerInventory, data.readBlockPos());
		return new StorageCrateCompactingContainer(StorageContainerTypes.STORAGE_CRATES_COMPACTING.get(crate.getStorageMaterial()).get(), windowId, playerInventory, crate);
	}

	protected static StorageCrateCompactingBlockEntity getCrateBlockEntity(Inventory playerInv, BlockPos pos) {
		Level level = playerInv.player.getCommandSenderWorld();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof StorageCrateCompactingBlockEntity crate) {
			return crate;
		}
		return null;
	}

	public StorageCrateCompactingContainer(MenuType<StorageCrateCompactingContainer> menuType, int windowId, Inventory playerInventory, StorageCrateBlockEntity inventory) {
		super(menuType, windowId, playerInventory, inventory);
	}

}
