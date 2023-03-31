package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class GoldSafeScreen extends BaseStorageScreen<StorageContainer> {

    public GoldSafeScreen(StorageContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 4);
    }
}
