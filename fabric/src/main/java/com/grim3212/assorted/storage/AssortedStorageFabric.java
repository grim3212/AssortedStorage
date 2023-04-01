package com.grim3212.assorted.storage;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import net.fabricmc.api.ModInitializer;

public class AssortedStorageFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StorageCommonMod.init();

        StorageBlocks.initDispenserHandlers();
    }
}
