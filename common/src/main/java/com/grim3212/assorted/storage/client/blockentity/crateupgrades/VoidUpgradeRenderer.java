package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.state.CrateRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;

public class VoidUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final VoidUpgradeRenderer INSTANCE = new VoidUpgradeRenderer();

    @Override
    public void submit(CrateRenderState state, ItemStack upgrade, PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.pushPose();
        CrateBlockEntityRenderer.faceCrate(state.facing, poseStack);

        poseStack.translate(-0.5D, 0.5D, 0.45D);
        float scale = 0.012F * 0.6666667F;
        poseStack.scale(scale, -scale, scale);
        int light = state.lightCoords;

        // See PadlockUpgradeRenderer: the quad is drawn against a local copy of the captured pose.
        collector.submitCustomGeometry(poseStack, CrateBlockEntityRenderer.ICONS, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.last().set(pose);
            RenderHelper.lightedBlit(buffer, local, 116, 116, 7, 0, 0, 4, 4, 16, 16, light);
        });

        poseStack.popPose();
    }
}
