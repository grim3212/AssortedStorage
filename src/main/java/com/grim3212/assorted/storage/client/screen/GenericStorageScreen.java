package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericStorageScreen extends BaseStorageScreen<StorageContainer> {

	public GenericStorageScreen(StorageContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}
}
