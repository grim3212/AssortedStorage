package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class VoidUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final VoidUpgradeRenderer INSTANCE = new VoidUpgradeRenderer();

    @Override
    public void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStack, SubmitNodeCollector collectorIn, int combinedLightIn, int combinedOverlayIn) {
        Direction facing = tileEntityIn.getBlockState().getValue(CrateBlock.FACING);

        matrixStack.pushPose();
        CrateBlockEntityRenderer.faceCrate(facing, matrixStack);

        matrixStack.translate(-0.5D, 0.5D, 0.45D);
        float scale = 0.012F * 0.6666667F;
        matrixStack.scale(scale, -scale, scale);

        // See PadlockUpgradeRenderer: raw quads go through submitCustomGeometry now.
        collectorIn.submitCustomGeometry(matrixStack, CrateBlockEntityRenderer.ICONS, (pose, buffer) -> {
            PoseStack local = new PoseStack();
            local.last().set(pose);
            RenderHelper.lightedBlit(buffer, local, 116, 116, 7, 0, 0, 4, 4, 16, 16, combinedLightIn);
        });

        matrixStack.popPose();
    }
}
