package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class PadlockUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final PadlockUpgradeRenderer INSTANCE = new PadlockUpgradeRenderer();

    @Override
    public void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStack, SubmitNodeCollector collectorIn, int combinedLightIn, int combinedOverlayIn) {
        Direction facing = tileEntityIn.getBlockState().getValue(CrateBlock.FACING);

        matrixStack.pushPose();
        CrateBlockEntityRenderer.faceCrate(facing, matrixStack);

        matrixStack.translate(-0.5D, 0.5D, 0.455D);
        float scale = 0.052F * 0.6666667F;
        matrixStack.scale(scale, -scale, scale);
        matrixStack.translate(0D, -0.5D, -0.455D);
        int yLoc = tileEntityIn.getLayout() != CrateLayout.SINGLE ? 13 : 1;

        // MultiBufferSource is gone: raw quads go through submitCustomGeometry, which hands back the
        // pose it captured here rather than letting us keep drawing against this stack.
        collectorIn.submitCustomGeometry(matrixStack, CrateBlockEntityRenderer.ICONS, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.last().set(pose);
            RenderHelper.lightedBlit(buffer, local, 13, yLoc, 2, 0, 4, 3, 6, 16, 16, combinedLightIn);
        });

        matrixStack.popPose();
    }
}
