package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.client.model.BaseStorageModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.tileentity.BaseStorageTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class StorageTileEntityRenderer<T extends TileEntity & IStorage> extends TileEntityRenderer<T> {

	private final BaseStorageModel model;
	private final ResourceLocation textureLocation;

	public StorageTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn, BaseStorageModel model, ResourceLocation textureLocation) {
		super(rendererDispatcherIn);
		this.model = model;
		this.textureLocation = textureLocation;
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		BaseStorageTileEntity tileEntity = (BaseStorageTileEntity) tileEntityIn;

		World world = tileEntity.getWorld();
		boolean flag = world != null;

		BlockState blockstate = flag ? tileEntity.getBlockState() : (BlockState) tileEntity.getBlockToUse().getDefaultState().with(BaseStorageBlock.FACING, Direction.SOUTH);
		Block block = blockstate.getBlock();

		if (block instanceof BaseStorageBlock) {
			matrixStackIn.push();
			float f = blockstate.get(BaseStorageBlock.FACING).getHorizontalAngle();
			matrixStackIn.translate(0.5D, 0.5D, 0.5D);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
			matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

			float angle = tileEntity.getRotation(partialTicks);
			angle *= 90f;

			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(this.textureLocation));

			this.model.doorAngle = angle;
			this.model.renderHandle = !tileEntity.isLocked();

			this.model.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

			matrixStackIn.pop();
		}
	}
}
