package com.grim3212.assorted.storage.client.blockentity.state;

import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Carries the tower's own model, whose scroll counter has to survive between frames; the renderer's
 * shared model is used when there is none, as for the item.
 */
public class ItemTowerRenderState extends BlockEntityRenderState {

    /** False when the block entity's block is not a storage block, in which case nothing is drawn. */
    public boolean renderModel;
    public Direction facing = Direction.SOUTH;
    /** The block entity's own animating model, or null to fall back to the renderer's shared one. */
    public @Nullable ItemTowerModel animatedModel;
    public ItemTowerModel.State model = ItemTowerModel.State.INVENTORY;
}
