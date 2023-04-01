package com.grim3212.assorted.storage.client;

import net.fabricmc.api.ClientModInitializer;

public class AssortedStorageFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StorageClient.init();
    }

}
