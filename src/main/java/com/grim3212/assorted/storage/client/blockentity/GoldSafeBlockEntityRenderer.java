package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.SafeModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GoldSafeBlockEntityRenderer<T extends BlockEntity & IStorage> extends StorageBlockEntityRenderer<T> {

	public GoldSafeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new SafeModel(context.getModelSet().bakeLayer(StorageModelLayers.SAFE)), new ResourceLocation(AssortedStorage.MODID, "textures/model/gold_safe.png"));
	}

}
