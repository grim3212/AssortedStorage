package com.grim3212.assorted.storage.client.blockentity.state;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;

/**
 * For the storage blocks whose texture lives on one of the entity atlases rather than being a
 * standalone png. 1.20.1's {@code Material(atlas, texture)} is {@link SpriteId} now, and the sprite is
 * handed to {@code SubmitNodeCollector#submitModel} together with the {@code SpriteGetter} that
 * resolves it, instead of being used to open a {@code VertexConsumer}.
 */
public class SpriteStorageRenderState extends StorageBlockRenderState {

    public SpriteId sprite = Sheets.ENDER_CHEST_LOCATION;
}
