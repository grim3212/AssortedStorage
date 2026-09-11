package com.grim3212.assorted.storage.client.blockentity.state;

import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CrateRenderState extends BlockEntityRenderState {

    public Direction facing = Direction.NORTH;
    public CrateLayout layout = CrateLayout.SINGLE;
    /** One per displayed slot, in slot order. */
    public List<ItemStackRenderState> items = Collections.emptyList();
    /** The stored item's display rotation, in slot order. */
    public float[] itemRotations = new float[0];
    /** Whether each slot has its item lock set, in slot order. */
    public boolean[] slotLocked = new boolean[0];
    public boolean anySlotLocked;
    public int itemLightCoords;

    /**
     * The unique upgrade stacks whose renderers should run.
     */
    public List<ItemStack> upgrades = Collections.emptyList();

    // TODO(26.2): ICrateUpgradeRenderer still takes the CrateBlockEntity, so the submit pass
    //  carries it. The interface should take an extracted state so the submit pass stops touching
    //  level objects.
    public @Nullable CrateBlockEntity blockEntity;
}
