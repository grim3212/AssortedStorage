package com.grim3212.assorted.storage.client.blockentity.state;

import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * The item tower's shelves scroll, and the scroll position is per block entity: opening the screen
 * calls {@code ItemTowerBlockEntity#animate}, which drives a counter that has to survive between
 * frames. That counter lives on an {@code ItemTowerModel} the block entity owns, so the model itself
 * is what gets extracted here - the renderer's own shared model is only used when a tower has no
 * animating instance of its own (and for the block item, which has no block entity in a level).
 */
public class ItemTowerRenderState extends BlockEntityRenderState {

    /** False when the block entity's block is not a storage block, in which case nothing is drawn. */
    public boolean renderModel;
    public Direction facing = Direction.SOUTH;
    /** The block entity's own animating model, or null to fall back to the renderer's shared one. */
    public @Nullable ItemTowerModel animatedModel;
    public ItemTowerModel.State model = ItemTowerModel.State.INVENTORY;
}
