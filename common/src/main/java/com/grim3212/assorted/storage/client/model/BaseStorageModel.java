package com.grim3212.assorted.storage.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

/**
 * Base for the storage block models.
 * <p>
 * {@code Model} gained a state type parameter and now takes its root part in the constructor;
 * {@code renderToBuffer} is final and always draws the whole root, so what used to be a hand written
 * {@code renderToBuffer} override picking parts is expressed as {@link ModelPart#visible} flags set
 * from {@link #setupAnim(StorageModelState)}, and what used to be {@code handleRotations()} is that
 * same method.
 */
public abstract class BaseStorageModel extends Model<StorageModelState> {

    public BaseStorageModel(ModelPart root, Function<Identifier, RenderType> renderTypeIn) {
        super(root, renderTypeIn);
    }
}
