package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.common.inventory.StorageContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class GenericStorageScreen extends BaseStorageScreen<StorageContainer> {

    public GenericStorageScreen(StorageContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }
}
