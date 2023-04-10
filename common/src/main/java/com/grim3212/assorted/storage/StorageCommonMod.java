package com.grim3212.assorted.storage;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.crafting.StorageConditions;
import com.grim3212.assorted.storage.common.crafting.StorageRecipeSerializers;
import com.grim3212.assorted.storage.common.events.StorageEvents;
import com.grim3212.assorted.storage.common.handlers.StorageCreativeItems;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.loot.StorageLootConditions;
import com.grim3212.assorted.storage.common.loot.StorageLootEntries;
import com.grim3212.assorted.storage.common.network.StoragePackets;
import com.grim3212.assorted.storage.config.StorageCommonConfig;

public class StorageCommonMod {

    public static final StorageCommonConfig COMMON_CONFIG = new StorageCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        StorageBlocks.init();
        StorageBlockEntityTypes.init();
        StorageItems.init();
        StorageContainerTypes.init();
        StoragePackets.init();
        StorageConditions.init();
        StorageLootConditions.init();
        StorageLootEntries.init();
        StorageRecipeSerializers.init();
        StorageEvents.init();
        StorageCreativeItems.init();
    }
}
