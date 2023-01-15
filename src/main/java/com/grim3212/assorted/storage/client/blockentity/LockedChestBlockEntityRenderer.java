package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.client.model.BaseStorageModel;
import com.grim3212.assorted.storage.client.model.ChestModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.StorageModels;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LockedChestBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T> {

	private final BaseStorageModel model;

	public LockedChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new ChestModel(context.getModelSet().bakeLayer(StorageModelLayers.LOCKED_CHEST));
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BaseStorageBlockEntity tileEntity = (BaseStorageBlockEntity) tileEntityIn;

		Level world = tileEntity.getLevel();
		boolean flag = world != null;

		BlockState blockstate = flag ? tileEntity.getBlockState() : (BlockState) tileEntity.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();

		if (block instanceof LockedChestBlock lockedChest) {
			matrixStackIn.pushPose();
			float f = blockstate.getValue(BaseStorageBlock.FACING).toYRot();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.mulPose(Axis.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

			float angle = tileEntity.getRotation(partialTicks);
			angle *= 90f;
			
			Material material = new Material(Sheets.CHEST_SHEET, StorageModels.CHEST_LOCATIONS.get(lockedChest.getStorageMaterial()));

			VertexConsumer ivertexbuilder = material.buffer(bufferIn, RenderType::entityCutout);

			this.model.doorAngle = angle;
			this.model.renderHandle = !tileEntity.isLocked();

			this.model.handleRotations();
			this.model.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

			matrixStackIn.popPose();
		}
	}
}
