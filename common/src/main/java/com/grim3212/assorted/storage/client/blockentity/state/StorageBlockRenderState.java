package com.grim3212.assorted.storage.client.blockentity.state;

import com.grim3212.assorted.storage.client.model.StorageModelState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

/**
 * Render state for the storage blocks drawn with a {@code BaseStorageModel}.
 * <p>
 * 26.2 split a block entity renderer into an extract pass, which is the only place the
 * {@code BlockEntity} may be read, and a submit pass that only sees this object. Everything the old
 * {@code render} method pulled off the block entity therefore has to be copied here first -
 * including the facing, because {@code BlockEntityRenderState} keeps its own block state private.
 */
public class StorageBlockRenderState extends BlockEntityRenderState {

    /** False when the block entity's block is not a storage block, in which case nothing is drawn. */
    public boolean renderModel;
    public Direction facing = Direction.SOUTH;
    public StorageModelState model = StorageModelState.CLOSED_UNLOCKED;
}
