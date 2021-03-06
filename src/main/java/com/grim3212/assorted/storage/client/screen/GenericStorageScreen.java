package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.common.inventory.StorageContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericStorageScreen extends BaseStorageScreen<StorageContainer> {

	public GenericStorageScreen(StorageContainer container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);
	}
}
