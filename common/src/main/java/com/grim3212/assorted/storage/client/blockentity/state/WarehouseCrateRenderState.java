package com.grim3212.assorted.storage.client.blockentity.state;

import net.minecraft.resources.Identifier;

/**
 * Carries the wood type's texture, which used to be looked up off the block during rendering.
 */
public class WarehouseCrateRenderState extends StorageBlockRenderState {

    public Identifier texture = Identifier.withDefaultNamespace("missingno");
}
