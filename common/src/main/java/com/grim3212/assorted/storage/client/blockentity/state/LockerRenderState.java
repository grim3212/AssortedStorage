package com.grim3212.assorted.storage.client.blockentity.state;

/**
 * Adds "is this the bottom half of a double locker" to the shared storage state, so the submit pass
 * can pick between the single and dual locker models without touching the block entity.
 */
public class LockerRenderState extends StorageBlockRenderState {

    public boolean dual;
}
