package com.grim3212.assorted.storage.client.tileentity;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.tileentity.IStorage;
import com.grim3212.assorted.storage.common.block.tileentity.ItemTowerTileEntity;
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

public class ItemTowerTileEntityRenderer<T extends TileEntity & IStorage> extends TileEntityRenderer<T> {

	private final ItemTowerModel model;
	private static final ResourceLocation ITEM_TOWER_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/model/item_tower.png");

	public ItemTowerTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.model = new ItemTowerModel();
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		ItemTowerTileEntity tileEntity = (ItemTowerTileEntity) tileEntityIn;

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

			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(ITEM_TOWER_TEXTURE));

			if (flag) {
				if (tileEntity.model != null) {
					tileEntity.model.bottomBlock = (tileEntity.getWorld().getTileEntity(tileEntity.getPos().down()) instanceof ItemTowerTileEntity);
					tileEntity.model.topBlock = (tileEntity.getWorld().getTileEntity(tileEntity.getPos().up()) instanceof ItemTowerTileEntity);

					tileEntity.model.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

					tileEntity.model.topBlock = false;
					tileEntity.model.bottomBlock = false;
				} else {
					this.model.bottomBlock = (tileEntity.getWorld().getTileEntity(tileEntity.getPos().down()) instanceof ItemTowerTileEntity);
					this.model.topBlock = (tileEntity.getWorld().getTileEntity(tileEntity.getPos().up()) instanceof ItemTowerTileEntity);

					this.model.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);

					this.model.topBlock = false;
					this.model.bottomBlock = false;
				}
			} else {
				this.model.renderModelInventory(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
			}

			matrixStackIn.pop();
		}
	}

}
