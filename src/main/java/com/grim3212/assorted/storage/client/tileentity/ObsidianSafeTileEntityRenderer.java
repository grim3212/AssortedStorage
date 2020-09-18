package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.SafeModel;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class ObsidianSafeTileEntityRenderer<T extends TileEntity & IStorage> extends StorageTileEntityRenderer<T> {

	public ObsidianSafeTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn, new SafeModel(), new ResourceLocation(AssortedStorage.MODID, "textures/model/obsidian_safe.png"));
	}

}
