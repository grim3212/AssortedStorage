package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LockerHalf;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.client.blockentity.state.LockerRenderState;
import com.grim3212.assorted.storage.client.model.BaseStorageModel;
import com.grim3212.assorted.storage.client.model.DualLockerModel;
import com.grim3212.assorted.storage.client.model.LockerModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.StorageModelState;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.LockerBlock;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LockerBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T, LockerRenderState> {

    private final LockerModel model;
    private final DualLockerModel dualModel;
    private static final Identifier LOCKER_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/locker.png");

    public LockerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new LockerModel(context.bakeLayer(StorageModelLayers.LOCKER));
        this.dualModel = new DualLockerModel(context.bakeLayer(StorageModelLayers.DUAL_LOCKER));
    }

    @Override
    public LockerRenderState createRenderState() {
        return new LockerRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, LockerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        LockerBlockEntity locker = (LockerBlockEntity) blockEntity;
        boolean placedInLevel = locker.getLevel() != null;

        // We don't want to render the model if this is the top locker block
        if (placedInLevel && locker.getBlockState().getValue(LockerBlock.HALF) == LockerHalf.TOP) {
            state.renderModel = false;
            return;
        }

        BlockState blockstate = placedInLevel ? locker.getBlockState() : locker.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);
        state.renderModel = blockstate.getBlock() instanceof BaseStorageBlock;
        if (!state.renderModel) {
            return;
        }

        state.facing = blockstate.getValue(BaseStorageBlock.FACING);
        state.dual = placedInLevel && blockstate.getValue(LockerBlock.HALF) == LockerHalf.BOTTOM;
        state.model = new StorageModelState(locker.getRotation(partialTicks) * 90.0F, !locker.isLocked());
    }

    @Override
    public void submit(LockerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.renderModel) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        BaseStorageModel model = state.dual ? this.dualModel : this.model;
        submitNodeCollector.submitModel(model, state.model, poseStack, LOCKER_TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);

        poseStack.popPose();
    }
}
