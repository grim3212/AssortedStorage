package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.lib.client.util.RenderHelper;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.StreamHelper;
import com.grim3212.assorted.storage.client.StorageClient;
import com.grim3212.assorted.storage.client.blockentity.crateupgrades.AmountUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.crateupgrades.PadlockUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.crateupgrades.VoidUpgradeRenderer;
import com.grim3212.assorted.storage.client.util.ClientResources;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.item.PadlockItem;
import com.grim3212.assorted.storage.common.item.upgrades.AmountUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.VoidUpgradeItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final int viewDistance;

    public static final RenderType ICONS = RenderType.text(ClientResources.CRATE_ICONS_LOCATION);

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.viewDistance = StorageClient.CLIENT_CONFIG.crateMaxRenderDistance.get();
    }

    @Override
    public int getViewDistance() {
        return this.viewDistance;
    }

    private void renderItemInSlot(CrateBlockEntity tileEntityIn, int slot, PoseStack matrixStackIn, double x, double y, int light, int overlay, MultiBufferSource bufferIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(x, y, 0.0D);
        LargeItemStack largeStack = tileEntityIn.getItemStackStorageHandler().getLargeItemStack(slot);
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float) -largeStack.getRotation() * 360.0F / 16.0F));
        this.itemRenderer.renderStatic(largeStack.getStack(), ItemTransforms.TransformType.GUI, light, overlay, matrixStackIn, bufferIn, 0);
        matrixStackIn.popPose();
    }

    @Override
    public void render(CrateBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = tileEntityIn.getBlockState();
        Direction facing = state.getValue(CrateBlock.FACING);

        matrixStackIn.pushPose();
        if (facing.getAxis().isHorizontal()) {
            float f = facing.toYRot();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-f));
            matrixStackIn.translate(0.0D, 0.0D, 0.449D);
        } else {
            float f = facing == Direction.DOWN ? 90F : 270F;
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(f));
            matrixStackIn.translate(0.0D, 0.0D, 0.449D);
        }

        matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        matrixStackIn.mulPoseMatrix(new Matrix4f().scale(1, 1, 0.001f));

        int itemLight = tileEntityIn.getItemStackStorageHandler().hasGlowUpgrade() ? LightTexture.FULL_BRIGHT : combinedLightIn;

        switch (tileEntityIn.getLayout()) {
            case SINGLE:
                renderItemInSlot(tileEntityIn, 0, matrixStackIn, 0.0D, 0.0D, itemLight, combinedOverlayIn, bufferIn);
                break;
            case DOUBLE:
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
                renderItemInSlot(tileEntityIn, 0, matrixStackIn, 0.0D, 0.875D, itemLight, combinedOverlayIn, bufferIn);
                renderItemInSlot(tileEntityIn, 1, matrixStackIn, 0.0D, -0.875D, itemLight, combinedOverlayIn, bufferIn);
                break;
            case TRIPLE:
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
                renderItemInSlot(tileEntityIn, 0, matrixStackIn, 0.0D, 0.875D, itemLight, combinedOverlayIn, bufferIn);
                renderItemInSlot(tileEntityIn, 1, matrixStackIn, -0.875D, -0.875D, itemLight, combinedOverlayIn, bufferIn);
                renderItemInSlot(tileEntityIn, 2, matrixStackIn, 0.875D, -0.875D, itemLight, combinedOverlayIn, bufferIn);
                break;
            case QUADRUPLE:
                matrixStackIn.scale(0.5f, 0.5f, 0.5f);
                renderItemInSlot(tileEntityIn, 0, matrixStackIn, -0.875D, 0.875D, itemLight, combinedOverlayIn, bufferIn);
                renderItemInSlot(tileEntityIn, 1, matrixStackIn, 0.875D, 0.875D, itemLight, combinedOverlayIn, bufferIn);
                renderItemInSlot(tileEntityIn, 2, matrixStackIn, -0.875D, -0.875D, itemLight, combinedOverlayIn, bufferIn);
                renderItemInSlot(tileEntityIn, 3, matrixStackIn, 0.875D, -0.875D, itemLight, combinedOverlayIn, bufferIn);
                break;
        }
        matrixStackIn.popPose();

        matrixStackIn.pushPose();
        // Unique upgrades only
        tileEntityIn.getItemStackStorageHandler().getEnhancements().stream().filter(StreamHelper.distinctByKey(p -> p.getItem())).forEach(stack -> {
            if (stack.getItem() instanceof PadlockItem) {
                PadlockUpgradeRenderer.INSTANCE.render(tileEntityIn, stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            } else if (stack.getItem() instanceof VoidUpgradeItem) {
                VoidUpgradeRenderer.INSTANCE.render(tileEntityIn, stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            } else if (stack.getItem() instanceof AmountUpgradeItem) {
                AmountUpgradeRenderer.INSTANCE.render(tileEntityIn, stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }
        });
        matrixStackIn.popPose();

        // Any slot locks
        matrixStackIn.pushPose();
        boolean anyLocked = tileEntityIn.getItemStackStorageHandler().anySlotsLocked();

        if (anyLocked) {
            float rot = facing.toYRot();
            matrixStackIn.translate(0.5F, 0.5F, 0.5F);
            if (facing.getAxis().isHorizontal()) {
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(-rot));
            } else {
                float f = facing == Direction.DOWN ? 90F : 270F;
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(f));
            }

            matrixStackIn.translate(-0.5D, 0.5D, 0.45D);
            float scale = 0.012F * 0.6666667F;
            matrixStackIn.scale(scale, -scale, scale);
            RenderSystem.enableBlend();
            VertexConsumer vertexConsumer = bufferIn.getBuffer(ICONS);

            switch (tileEntityIn.getLayout()) {
                case SINGLE:
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 18, combinedLightIn, 0);
                    break;
                case DOUBLE:
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 18, combinedLightIn, 0);
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 72, combinedLightIn, 1);
                    break;
                case TRIPLE:
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 18, combinedLightIn, 0);
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 47, 72, combinedLightIn, 1);
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 72, combinedLightIn, 2);
                    break;
                case QUADRUPLE:
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 47, 18, combinedLightIn, 0);
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 18, combinedLightIn, 1);
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 47, 72, combinedLightIn, 2);
                    renderLockIcon(tileEntityIn, vertexConsumer, matrixStackIn, 102, 72, combinedLightIn, 3);
                    break;
            }
            RenderSystem.disableDepthTest();
        }
        matrixStackIn.popPose();
    }

    private void renderLockIcon(CrateBlockEntity tileEntityIn, VertexConsumer vertexConsumer, PoseStack stack, int x, int y, int packedLight, int slot) {
        if (tileEntityIn.getItemStackStorageHandler().isSlotLocked(slot)) {
            RenderHelper.lightedBlit(vertexConsumer, stack, x, y, 0, 2, 0, 5, 5, 8, 8, packedLight);
        }
    }
}
