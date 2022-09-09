package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.client.model.BaseStorageModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StorageBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T> {

	private final BaseStorageModel model;
	private final ResourceLocation textureLocation;

	public StorageBlockEntityRenderer(BlockEntityRendererProvider.Context context, BaseStorageModel model, ResourceLocation textureLocation) {
		this.model = model;
		this.textureLocation = textureLocation;
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BaseStorageBlockEntity tileEntity = (BaseStorageBlockEntity) tileEntityIn;

		Level world = tileEntity.getLevel();
		boolean flag = world != null;

		BlockState blockstate = flag ? tileEntity.getBlockState() : (BlockState) tileEntity.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();

		if (block instanceof BaseStorageBlock) {
			matrixStackIn.pushPose();
			float f = blockstate.getValue(BaseStorageBlock.FACING).toYRot();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

			float angle = tileEntity.getRotation(partialTicks);
			angle *= 90f;

			VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(this.textureLocation));

			this.model.doorAngle = angle;
			this.model.renderHandle = !alwaysLocked() ? !tileEntity.isLocked() : false;

			this.model.handleRotations();
			this.model.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

			matrixStackIn.popPose();
		}
	}
	
	protected boolean alwaysLocked () {
		return false;
	}
}
