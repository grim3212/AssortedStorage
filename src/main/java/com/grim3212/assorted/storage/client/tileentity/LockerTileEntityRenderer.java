package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.DualLockerModel;
import com.grim3212.assorted.storage.client.model.LockerModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;
import com.grim3212.assorted.storage.common.block.tileentity.LockerTileEntity;
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

public class LockerTileEntityRenderer<T extends TileEntity & IStorage> extends TileEntityRenderer<T> {

	private final LockerModel model;
	private final DualLockerModel dualModel;
	private static final ResourceLocation LOCKER_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/model/locker.png");

	public LockerTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.model = new LockerModel();
		this.dualModel = new DualLockerModel();
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		LockerTileEntity tileEntity = (LockerTileEntity) tileEntityIn;
		if (tileEntity != null && tileEntity.isUpperLocker()) {
			return;
		}

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

			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(LOCKER_TEXTURE));

			if (tileEntity.getUpperLocker() != null) {
				this.dualModel.doorAngle = angle;
				this.dualModel.renderHandle = !tileEntity.isLocked();

				this.dualModel.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
			} else {
				this.model.doorAngle = angle;
				this.model.renderHandle = !tileEntity.isLocked();

				this.model.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
			}

			matrixStackIn.pop();
		}
	}

}
