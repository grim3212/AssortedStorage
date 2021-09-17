package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.ChestModel;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class LockedEnderChestTileEntityRenderer<T extends TileEntity & IStorage> extends StorageTileEntityRenderer<T> {

	public LockedEnderChestTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn, new ChestModel(), new ResourceLocation(AssortedStorage.MODID, "textures/model/locked_ender_chest.png"));
	}

}
