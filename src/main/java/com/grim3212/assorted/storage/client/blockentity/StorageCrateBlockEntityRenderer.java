package com.grim3212.assorted.storage.client.blockentity;

import org.joml.Matrix4f;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.render.RenderHelper;
import com.grim3212.assorted.storage.common.block.StorageCrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.StorageCrateBlockEntity;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.util.LargeItemStack;
import com.grim3212.assorted.storage.common.util.StreamHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class StorageCrateBlockEntityRenderer implements BlockEntityRenderer<StorageCrateBlockEntity> {

	private final ItemRenderer itemRenderer;
	private final int viewDistance;

	private static ResourceLocation ICONS_LOCATIONS = new ResourceLocation(AssortedStorage.MODID, "textures/block/storage_crates/icons.png");
	private static final RenderType ICONS = RenderType.text(ICONS_LOCATIONS);

	public StorageCrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
		this.viewDistance = StorageConfig.CLIENT.storageCrateMaxRenderDistance.get();
	}

	@Override
	public int getViewDistance() {
		return this.viewDistance;
	}

	private void renderItemInSlot(StorageCrateBlockEntity tileEntityIn, int slot, PoseStack matrixStackIn, double x, double y, int light, int overlay, MultiBufferSource bufferIn) {
		matrixStackIn.pushPose();
		matrixStackIn.translate(x, y, 0.0D);
		LargeItemStack largeStack = tileEntityIn.getLargeItemStack(slot);
		matrixStackIn.mulPose(Axis.ZP.rotationDegrees((float) -largeStack.getRotation() * 360.0F / 16.0F));
		this.itemRenderer.renderStatic(largeStack.getStack(), ItemTransforms.TransformType.GUI, light, overlay, matrixStackIn, bufferIn, 0);
		matrixStackIn.popPose();
	}

	@Override
	public void render(StorageCrateBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BlockState state = tileEntityIn.getBlockState();
		Direction facing = state.getValue(StorageCrateBlock.FACING);

		int newCombinedLight = LevelRenderer.getLightColor(tileEntityIn.getLevel(), tileEntityIn.getBlockPos().relative(facing));

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

		int itemLight = tileEntityIn.hasGlowUpgrade() ? LightTexture.FULL_BRIGHT : newCombinedLight;

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
		tileEntityIn.getEnhancements().stream().filter(StreamHelper.distinctByKey(p -> p.getItem())).forEach(stack -> {
			if (stack.getItem()instanceof ICrateUpgradeRenderer upgrade) {
				upgrade.render(tileEntityIn, stack, partialTicks, matrixStackIn, bufferIn, newCombinedLight, combinedOverlayIn);
			}
		});
		matrixStackIn.popPose();

		// Any slot locks
		matrixStackIn.pushPose();
		boolean anyLocked = tileEntityIn.getItems().stream().anyMatch(stack -> stack.isLocked());

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

	private void renderLockIcon(StorageCrateBlockEntity tileEntityIn, VertexConsumer vertexConsumer, PoseStack stack, int x, int y, int packedLight, int slot) {
		if (tileEntityIn.isSlotLocked(slot)) {
			RenderHelper.lightedBlit(vertexConsumer, stack, x, y, 0, 2, 0, 5, 5, 8, 8, packedLight);
		}
	}
}
