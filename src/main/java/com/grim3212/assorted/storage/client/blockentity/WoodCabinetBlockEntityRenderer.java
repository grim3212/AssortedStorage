package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.CabinetModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WoodCabinetBlockEntityRenderer<T extends BlockEntity & IStorage> extends StorageBlockEntityRenderer<T> {

	protected static final ResourceLocation CABINET_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/model/cabinet.png");

	public WoodCabinetBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new CabinetModel(context.getModelSet().bakeLayer(StorageModelLayers.CABINET)), CABINET_TEXTURE);
	}

}
