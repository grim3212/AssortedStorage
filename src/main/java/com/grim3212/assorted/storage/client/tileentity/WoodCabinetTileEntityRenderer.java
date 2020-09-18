package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.CabinetModel;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class WoodCabinetTileEntityRenderer<T extends TileEntity & IStorage> extends StorageTileEntityRenderer<T> {

	protected static final ResourceLocation CABINET_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/model/cabinet.png");

	public WoodCabinetTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn, new CabinetModel(false), CABINET_TEXTURE);
	}

}
