package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrateCompactingContainer extends CrateContainer {

	public static CrateCompactingContainer createCrateContainer(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
		CrateCompactingBlockEntity crate = getCrateBlockEntity(playerInventory, data.readBlockPos());
		return new CrateCompactingContainer(StorageContainerTypes.CRATE_COMPACTING.get(), windowId, playerInventory, crate);
	}

	protected static CrateCompactingBlockEntity getCrateBlockEntity(Inventory playerInv, BlockPos pos) {
		Level level = playerInv.player.getCommandSenderWorld();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof CrateCompactingBlockEntity crate) {
			return crate;
		}
		return null;
	}

	public CrateCompactingContainer(MenuType<CrateCompactingContainer> menuType, int windowId, Inventory playerInventory, CrateBlockEntity inventory) {
		super(menuType, windowId, playerInventory, inventory);
	}

}
