package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;
import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemTowerBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T> {

	private final ItemTowerModel model;
	private static final ResourceLocation ITEM_TOWER_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/model/item_tower.png");

	public ItemTowerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new ItemTowerModel(context.getModelSet().bakeLayer(StorageModelLayers.ITEM_TOWER));
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		ItemTowerBlockEntity tileEntity = (ItemTowerBlockEntity) tileEntityIn;

		Level world = tileEntity.getLevel();
		boolean flag = world != null;

		BlockState blockstate = flag ? tileEntity.getBlockState() : (BlockState) tileEntity.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();

		if (block instanceof BaseStorageBlock) {
			matrixStackIn.pushPose();
			float f = blockstate.getValue(BaseStorageBlock.FACING).toYRot();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.mulPose(Axis.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

			VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(ITEM_TOWER_TEXTURE));

			if (flag) {
				if (tileEntity.model != null) {
					tileEntity.model.bottomBlock = (tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().below()) instanceof ItemTowerBlockEntity);
					tileEntity.model.topBlock = (tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().above()) instanceof ItemTowerBlockEntity);

					tileEntity.model.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

					tileEntity.model.topBlock = false;
					tileEntity.model.bottomBlock = false;
				} else {
					this.model.bottomBlock = (tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().below()) instanceof ItemTowerBlockEntity);
					this.model.topBlock = (tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().above()) instanceof ItemTowerBlockEntity);

					this.model.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

					this.model.topBlock = false;
					this.model.bottomBlock = false;
				}
			} else {
				this.model.renderModelInventory(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
			}

			matrixStackIn.popPose();
		}
	}

}
