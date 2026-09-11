package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.state.CrateRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;

public class PadlockUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final PadlockUpgradeRenderer INSTANCE = new PadlockUpgradeRenderer();

    @Override
    public void submit(CrateRenderState state, ItemStack upgrade, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        CrateBlockEntityRenderer.faceCrate(state.facing, poseStack);

        poseStack.translate(-0.5D, 0.5D, 0.455D);
        float scale = 0.052F * 0.6666667F;
        poseStack.scale(scale, -scale, scale);
        poseStack.translate(0D, -0.5D, -0.455D);
        int yLoc = state.layout != CrateLayout.SINGLE ? 13 : 1;
        int light = state.lightCoords;

        // submitCustomGeometry hands back the pose it captured here, so the quad is drawn against a
        // local copy rather than this stack.
        collector.submitCustomGeometry(poseStack, CrateBlockEntityRenderer.ICONS, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.last().set(pose);
            RenderHelper.lightedBlit(buffer, local, 13, yLoc, 2, 0, 4, 3, 6, 16, 16, light);
        });

        poseStack.popPose();
    }
}
