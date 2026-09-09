package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.client.model.CabinetModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WoodCabinetBlockEntityRenderer<T extends BlockEntity & IStorage> extends StorageBlockEntityRenderer<T> {

    protected static final Identifier CABINET_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/cabinet.png");

    public WoodCabinetBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new CabinetModel(context.bakeLayer(StorageModelLayers.CABINET)), CABINET_TEXTURE);
    }

}
