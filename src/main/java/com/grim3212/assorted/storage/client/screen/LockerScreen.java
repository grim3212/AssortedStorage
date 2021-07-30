package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.common.inventory.LockerContainer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockerScreen extends BaseStorageScreen<LockerContainer> {

	public LockerScreen(LockerContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title, 5);
	}
}
