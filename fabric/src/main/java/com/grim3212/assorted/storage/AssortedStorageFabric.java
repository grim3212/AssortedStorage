package com.grim3212.assorted.storage;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandler;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class AssortedStorageFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        StorageCommonMod.init();

        StorageBlocks.initDispenserHandlers();

        ItemStorage.SIDED.registerForBlockEntities((be, direction) ->
                {
                    if (be instanceof IInventoryBlockEntity inv)
                        return ((FabricPlatformInventoryStorageHandler) inv.getStorageHandler()).getFabricInventory();
                    return null;
                },
                StorageBlockEntityTypes.WOOD_CABINET.get(),
                StorageBlockEntityTypes.GLASS_CABINET.get(),
                StorageBlockEntityTypes.GOLD_SAFE.get(),
                StorageBlockEntityTypes.OBSIDIAN_SAFE.get(),
                StorageBlockEntityTypes.LOCKER.get(),
                StorageBlockEntityTypes.ITEM_TOWER.get(),
                StorageBlockEntityTypes.WAREHOUSE_CRATE.get(),
                StorageBlockEntityTypes.LOCKED_CHEST.get(),
                StorageBlockEntityTypes.LOCKED_ENDER_CHEST.get(),
                StorageBlockEntityTypes.LOCKED_HOPPER.get(),
                StorageBlockEntityTypes.LOCKED_BARREL.get(),
                StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(),
                StorageBlockEntityTypes.CRATE.get(),
                StorageBlockEntityTypes.CRATE_CONTROLLER.get(),
                StorageBlockEntityTypes.CRATE_COMPACTING.get()
        );
    }
}
