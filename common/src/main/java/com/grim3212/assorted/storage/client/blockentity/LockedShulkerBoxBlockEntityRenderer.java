package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.client.model.ShulkerBoxModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.StorageModels;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.blockentity.LockedShulkerBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LockedShulkerBoxBlockEntityRenderer implements BlockEntityRenderer<LockedShulkerBoxBlockEntity> {

	private final ShulkerBoxModel model;

	public LockedShulkerBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new ShulkerBoxModel(context.bakeLayer(StorageModelLayers.LOCKED_SHULKER_BOX));
	}

	@Override
	public void render(LockedShulkerBoxBlockEntity shulkerBE, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Level world = shulkerBE.getLevel();
		boolean flag = world != null;
		BlockState blockstate = flag ? shulkerBE.getBlockState() : (BlockState) shulkerBE.getBlockState().setValue(LockedShulkerBoxBlock.FACING, Direction.UP);
		Block block = blockstate.getBlock();

		if (block instanceof LockedShulkerBoxBlock shulker) {
			Direction direction = blockstate.getValue(LockedShulkerBoxBlock.FACING);
			DyeColor savedColor = shulkerBE.getColor();

			Material storageMaterial = new Material(Sheets.SHULKER_SHEET, StorageModels.SHULKER_LOCATIONS.get(shulker.getStorageMaterial()));

			Material material;
			if (savedColor == null) {
				material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
			} else {
				material = Sheets.SHULKER_TEXTURE_LOCATION.get(savedColor.getId());
			}

			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			float f = 0.9995F;
			poseStack.scale(f, f, f);
			poseStack.mulPose(direction.getRotation());
			poseStack.scale(1.0F, -1.0F, -1.0F);
			poseStack.translate(0.0D, -1.0D, 0.0D);

			this.model.isLocked = shulkerBE.isLocked();
			this.model.updateLid(0.0F, shulkerBE.getProgress(partialTicks) * 0.5F * 16.0F, 0.0F, 270.0F * shulkerBE.getProgress(partialTicks) * ((float) Math.PI / 180F));
			VertexConsumer vertexconsumer = material.buffer(bufferIn, RenderType::entityCutoutNoCull);
			this.model.renderToBuffer(poseStack, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
			// Literally just render the model again with our texture that is just the
			// overlay
			VertexConsumer storageVertexConsumer = storageMaterial.buffer(bufferIn, RenderType::entityCutoutNoCull);
			this.model.renderToBuffer(poseStack, storageVertexConsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
			poseStack.popPose();
		}
	}
}
