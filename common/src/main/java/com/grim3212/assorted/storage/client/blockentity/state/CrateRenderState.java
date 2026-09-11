package com.grim3212.assorted.storage.client.blockentity.state;

import com.grim3212.assorted.storage.api.crates.CrateLayout;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

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
    /** How many items each slot holds, in slot order. */
    public int[] slotAmounts = new int[0];
    /** How many items each slot can hold, in slot order. */
    public int[] slotCapacities = new int[0];
    public boolean anySlotLocked;
    /** The light the stored items and their amounts draw with: full bright with a glow upgrade. */
    public int itemLightCoords;

    /**
     * Copies of the unique upgrade stacks whose renderers should run.
     */
    public List<ItemStack> upgrades = Collections.emptyList();
}
