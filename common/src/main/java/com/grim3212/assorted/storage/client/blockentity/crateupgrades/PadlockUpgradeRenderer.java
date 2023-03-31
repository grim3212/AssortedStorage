package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class PadlockUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final PadlockUpgradeRenderer INSTANCE = new PadlockUpgradeRenderer();

    @Override
    public void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Direction facing = tileEntityIn.getBlockState().getValue(CrateBlock.FACING);

        matrixStack.pushPose();
        float rot = facing.toYRot();
        matrixStack.translate(0.5F, 0.5F, 0.5F);
        if (facing.getAxis().isHorizontal()) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(-rot));
        } else {
            float f = facing == Direction.DOWN ? 90F : 270F;
            matrixStack.mulPose(Axis.XP.rotationDegrees(f));
        }

        matrixStack.translate(-0.5D, 0.5D, 0.455D);
        float scale = 0.052F * 0.6666667F;
        matrixStack.scale(scale, -scale, scale);
        matrixStack.translate(0D, -0.5D, -0.455D);
        int yLoc = tileEntityIn.getLayout() != CrateLayout.SINGLE ? 13 : 1;
        RenderSystem.enableBlend();
        VertexConsumer vertexConsumer = bufferIn.getBuffer(CrateBlockEntityRenderer.ICONS);
        RenderHelper.lightedBlit(vertexConsumer, matrixStack, 13, yLoc, 2, 0, 4, 3, 6, 16, 16, combinedLightIn);
        RenderSystem.disableDepthTest();
        matrixStack.popPose();
    }
}
