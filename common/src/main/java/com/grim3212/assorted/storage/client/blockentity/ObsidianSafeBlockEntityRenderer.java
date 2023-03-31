package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.client.model.SafeModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ObsidianSafeBlockEntityRenderer<T extends BlockEntity & IStorage> extends StorageBlockEntityRenderer<T> {

    public ObsidianSafeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new SafeModel(context.getModelSet().bakeLayer(StorageModelLayers.SAFE)), new ResourceLocation(Constants.MOD_ID, "textures/model/obsidian_safe.png"));
    }

}
