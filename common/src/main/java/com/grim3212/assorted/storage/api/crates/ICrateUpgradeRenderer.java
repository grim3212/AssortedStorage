package com.grim3212.assorted.storage.api.crates;

import com.grim3212.assorted.storage.client.blockentity.state.CrateRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;


public interface ICrateUpgradeRenderer {

    /**
     * Submits this upgrade's overlay on the crate face. Everything it draws comes from the state
     * extracted from the crate, so nothing here reaches back into the level.
     */
    void submit(CrateRenderState state, ItemStack upgrade, PoseStack poseStack, SubmitNodeCollector collector);
}
