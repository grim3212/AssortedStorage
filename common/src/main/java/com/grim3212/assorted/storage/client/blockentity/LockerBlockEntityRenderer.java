package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LockerHalf;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.client.model.DualLockerModel;
import com.grim3212.assorted.storage.client.model.LockerModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockerBlock;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
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

public class LockerBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T> {

    private final LockerModel model;
    private final DualLockerModel dualModel;
    private static final ResourceLocation LOCKER_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/model/locker.png");

    public LockerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new LockerModel(context.getModelSet().bakeLayer(StorageModelLayers.LOCKER));
        this.dualModel = new DualLockerModel(context.getModelSet().bakeLayer(StorageModelLayers.DUAL_LOCKER));
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        LockerBlockEntity tileEntity = (LockerBlockEntity) tileEntityIn;
        Level world = tileEntity.getLevel();
        boolean placedInLevel = world != null;

        // We don't want to render the model if this is the top locker block
        if (tileEntity == null || (placedInLevel && tileEntity.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.TOP)) {
            return;
        }

        BlockState blockstate = placedInLevel ? tileEntity.getBlockState() : tileEntity.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);
        Block block = blockstate.getBlock();

        if (block instanceof BaseStorageBlock) {
            matrixStackIn.pushPose();
            float f = blockstate.getValue(BaseStorageBlock.FACING).toYRot();
            matrixStackIn.translate(0.5D, 0.5D, 0.5D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-f));
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);

            float angle = tileEntity.getRotation(partialTicks);
            angle *= 90f;

            VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(LOCKER_TEXTURE));

            if (placedInLevel && tileEntity.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.BOTTOM) {
                this.dualModel.doorAngle = angle;
                this.dualModel.renderHandle = !tileEntity.isLocked();

                this.dualModel.handleRotations();
                this.dualModel.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
            } else {
                this.model.doorAngle = angle;
                this.model.renderHandle = !tileEntity.isLocked();

                this.model.handleRotations();
                this.model.renderToBuffer(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
            }

            matrixStackIn.popPose();
        }
    }

}
