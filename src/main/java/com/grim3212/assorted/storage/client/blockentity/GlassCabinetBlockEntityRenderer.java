package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.client.model.CabinetModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GlassCabinetBlockEntityRenderer<T extends BlockEntity & IStorage> extends StorageBlockEntityRenderer<T> {

	public GlassCabinetBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new CabinetModel(context.getModelSet().bakeLayer(StorageModelLayers.GLASS_CABINET)), WoodCabinetBlockEntityRenderer.CABINET_TEXTURE);
	}

}
