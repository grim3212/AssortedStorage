package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.client.model.CabinetModel;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

public class GlassCabinetTileEntityRenderer<T extends TileEntity & IStorage> extends StorageTileEntityRenderer<T> {

	public GlassCabinetTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn, new CabinetModel(true), WoodCabinetTileEntityRenderer.CABINET_TEXTURE);
	}

}
