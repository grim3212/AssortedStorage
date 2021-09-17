package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.ChestModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LockedEnderChestBlockEntityRenderer<T extends BlockEntity & IStorage> extends StorageBlockEntityRenderer<T> {

	public LockedEnderChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		super(context, new ChestModel(context.getModelSet().bakeLayer(StorageModelLayers.LOCKED_ENDER_CHEST)), new ResourceLocation(AssortedStorage.MODID, "textures/model/locked_ender_chest.png"));
	}

}
