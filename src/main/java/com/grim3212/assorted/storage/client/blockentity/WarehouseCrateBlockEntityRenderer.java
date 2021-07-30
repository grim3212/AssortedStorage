package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.WarehouseCrateModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.WarehouseCrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.IStorage;
import com.grim3212.assorted.storage.common.block.blockentity.WarehouseCrateBlockEntity;
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
import net.minecraft.world.level.block.state.properties.WoodType;

public class WarehouseCrateBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T> {

	private final WarehouseCrateModel model;
	private static final ResourceLocation OAK = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/oak.png");
	private static final ResourceLocation BIRCH = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/birch.png");
	private static final ResourceLocation SPRUCE = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/spruce.png");
	private static final ResourceLocation ACACIA = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/acacia.png");
	private static final ResourceLocation DARK_OAK = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/dark_oak.png");
	private static final ResourceLocation JUNGLE = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/jungle.png");
	private static final ResourceLocation WARPED = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/warped.png");
	private static final ResourceLocation CRIMSON = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/crimson.png");

	public WarehouseCrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.model = new WarehouseCrateModel(context.getModelSet().bakeLayer(StorageModelLayers.WAREHOUSE_CRATE));
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		WarehouseCrateBlockEntity tileEntity = (WarehouseCrateBlockEntity) tileEntityIn;

		Level world = tileEntity.getLevel();
		boolean flag = world != null;

		BlockState blockstate = flag ? tileEntity.getBlockState() : (BlockState) tileEntity.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();

		if (block instanceof WarehouseCrateBlock) {
			WarehouseCrateBlock crate = (WarehouseCrateBlock) block;

			matrixStackIn.pushPose();
			float f = blockstate.getValue(BaseStorageBlock.FACING).toYRot();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

			float angle = tileEntity.getRotation(partialTicks);
			angle *= 90f;

			VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(getTexture(crate.getWoodType())));

			this.model.doorAngle = angle;
			this.model.renderHandle = !tileEntity.isLocked();

			this.model.handleRotations();
			this.model.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

			matrixStackIn.popPose();
		}
	}

	private ResourceLocation getTexture(WoodType wood) {
		switch (wood.name()) {
			case "oak":
				return OAK;
			case "birch":
				return BIRCH;
			case "spruce":
				return SPRUCE;
			case "acacia":
				return ACACIA;
			case "dark_oak":
				return DARK_OAK;
			case "jungle":
				return JUNGLE;
			case "crimson":
				return CRIMSON;
			case "warped":
				return WARPED;
			default:
				return OAK;
		}
	}
}
