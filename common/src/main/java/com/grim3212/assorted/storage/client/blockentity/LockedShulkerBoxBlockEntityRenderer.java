package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.client.blockentity.state.LockedShulkerBoxRenderState;
import com.grim3212.assorted.storage.client.model.ShulkerBoxModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.StorageModels;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.blockentity.LockedShulkerBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LockedShulkerBoxBlockEntityRenderer implements BlockEntityRenderer<LockedShulkerBoxBlockEntity, LockedShulkerBoxRenderState> {

	private final ShulkerBoxModel model;
	private final SpriteGetter sprites;

	public LockedShulkerBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new ShulkerBoxModel(context.bakeLayer(StorageModelLayers.LOCKED_SHULKER_BOX));
		this.sprites = context.sprites();
	}

	@Override
	public LockedShulkerBoxRenderState createRenderState() {
		return new LockedShulkerBoxRenderState();
	}

	@Override
	public void extractRenderState(LockedShulkerBoxBlockEntity shulkerBE, LockedShulkerBoxRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(shulkerBE, state, partialTicks, cameraPosition, breakProgress);

		boolean placedInLevel = shulkerBE.getLevel() != null;
		BlockState blockstate = placedInLevel ? shulkerBE.getBlockState() : shulkerBE.getBlockState().setValue(LockedShulkerBoxBlock.FACING, Direction.UP);

		state.renderModel = blockstate.getBlock() instanceof LockedShulkerBoxBlock;
		if (!state.renderModel) {
			return;
		}

		LockedShulkerBoxBlock shulker = (LockedShulkerBoxBlock) blockstate.getBlock();
		DyeColor savedColor = shulkerBE.getColor();

		state.facing = blockstate.getValue(LockedShulkerBoxBlock.FACING);
		state.colorSprite = savedColor == null ? Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION : Sheets.getShulkerBoxSprite(savedColor);
		state.materialSprite = new SpriteId(Sheets.SHULKER_SHEET, StorageModels.SHULKER_LOCATIONS.get(shulker.getStorageMaterial()));
		state.model = new ShulkerBoxModel.State(shulkerBE.getProgress(partialTicks), shulkerBE.isLocked());
	}

	@Override
	public void submit(LockedShulkerBoxRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		if (!state.renderModel) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);
		float f = 0.9995F;
		poseStack.scale(f, f, f);
		poseStack.mulPose(state.facing.getRotation());
		poseStack.scale(1.0F, -1.0F, -1.0F);
		poseStack.translate(0.0D, -1.0D, 0.0D);

		submitNodeCollector.submitModel(this.model, state.model, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, state.colorSprite, this.sprites, 0, state.breakProgress);
		// Literally just submit the model again with our texture that is just the overlay
		submitNodeCollector.submitModel(this.model, state.model, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, state.materialSprite, this.sprites, 0, state.breakProgress);

		poseStack.popPose();
	}
}
