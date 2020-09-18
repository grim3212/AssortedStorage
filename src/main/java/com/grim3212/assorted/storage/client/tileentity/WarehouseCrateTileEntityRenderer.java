package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.WarehouseCrateModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.WarehouseCrateBlock;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;
import com.grim3212.assorted.storage.common.block.tileentity.WarehouseCrateTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class WarehouseCrateTileEntityRenderer<T extends TileEntity & IStorage> extends TileEntityRenderer<T> {

	private final WarehouseCrateModel model;
	private static final ResourceLocation OAK = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/oak.png");
	private static final ResourceLocation BIRCH = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/birch.png");
	private static final ResourceLocation SPRUCE = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/spruce.png");
	private static final ResourceLocation ACACIA = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/acacia.png");
	private static final ResourceLocation DARK_OAK = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/dark_oak.png");
	private static final ResourceLocation JUNGLE = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/jungle.png");
	private static final ResourceLocation WARPED = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/warped.png");
	private static final ResourceLocation CRIMSON = new ResourceLocation(AssortedStorage.MODID, "textures/model/warehouse_crate/crimson.png");

	public WarehouseCrateTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.model = new WarehouseCrateModel();
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		WarehouseCrateTileEntity tileEntity = (WarehouseCrateTileEntity) tileEntityIn;

		World world = tileEntity.getWorld();
		boolean flag = world != null;

		BlockState blockstate = flag ? tileEntity.getBlockState() : (BlockState) tileEntity.getBlockToUse().getDefaultState().with(BaseStorageBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();

		if (block instanceof WarehouseCrateBlock) {
			WarehouseCrateBlock crate = (WarehouseCrateBlock) block;

			matrixStackIn.push();
			float f = blockstate.get(BaseStorageBlock.FACING).getHorizontalAngle();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

			float angle = tileEntity.getRotation(partialTicks);
			angle *= 90f;

			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(getTexture(crate.getWoodType())));

			this.model.doorAngle = angle;
			this.model.renderHandle = !tileEntity.isLocked();

			this.model.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

			matrixStackIn.pop();
		}
	}

	private ResourceLocation getTexture(WoodType wood) {
		switch (wood.getName()) {
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
