package com.grim3212.assorted.storage.client.blockentity.state;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;

/**
 * For the storage blocks whose texture is on an entity atlas: the {@link SpriteId} goes to
 * {@code submitModel} with the {@code SpriteGetter} that resolves it.
 */
public class SpriteStorageRenderState extends StorageBlockRenderState {

    public SpriteId sprite = Sheets.ENDER_CHEST_LOCATION;
}
