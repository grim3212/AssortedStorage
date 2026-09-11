package com.grim3212.assorted.storage.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

/**
 * Base for the storage block models. Which parts draw is set as {@link ModelPart#visible} flags in
 * {@link #setupAnim(StorageModelState)}.
 */
public abstract class BaseStorageModel extends Model<StorageModelState> {

    public BaseStorageModel(ModelPart root, Function<Identifier, RenderType> renderTypeIn) {
        super(root, renderTypeIn);
    }
}
