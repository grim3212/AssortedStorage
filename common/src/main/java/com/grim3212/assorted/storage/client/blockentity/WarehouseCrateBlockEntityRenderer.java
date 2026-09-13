package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.storage.api.blockentity.IStorage;
import com.grim3212.assorted.storage.client.blockentity.state.WarehouseCrateRenderState;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.StorageModelState;
import com.grim3212.assorted.storage.client.model.WarehouseCrateModel;
import com.grim3212.assorted.storage.common.block.BaseStorageBlock;
import com.grim3212.assorted.storage.common.block.WarehouseCrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.WarehouseCrateBlockEntity;
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

import java.util.EnumMap;
import java.util.Map;

public class WarehouseCrateBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T, WarehouseCrateRenderState> {

    private final WarehouseCrateModel model;

    /**
     * The one place a warehouse crate's texture path is spelled out. The block item's
     * {@code minecraft:special} renderer is handed the same path at datagen, so the item and the
     * placed block always draw the same picture.
     */
    private static final Map<Wood, Identifier> TEXTURES = new EnumMap<>(Wood.class);

    static {
        for (Wood wood : Wood.values()) {
            TEXTURES.put(wood, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/" + wood + ".png"));
        }
    }

    public static Identifier texture(Wood wood) {
        return TEXTURES.get(wood);
    }

    public WarehouseCrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WarehouseCrateModel(context.bakeLayer(StorageModelLayers.WAREHOUSE_CRATE));
    }

    @Override
    public WarehouseCrateRenderState createRenderState() {
        return new WarehouseCrateRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, WarehouseCrateRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        WarehouseCrateBlockEntity crate = (WarehouseCrateBlockEntity) blockEntity;
        boolean placedInLevel = crate.getLevel() != null;
        BlockState blockstate = placedInLevel ? crate.getBlockState() : crate.getBlockState().setValue(BaseStorageBlock.FACING, Direction.SOUTH);

        state.renderModel = blockstate.getBlock() instanceof WarehouseCrateBlock;
        if (!state.renderModel) {
            return;
        }

        state.facing = blockstate.getValue(BaseStorageBlock.FACING);
        state.texture = texture(((WarehouseCrateBlock) blockstate.getBlock()).getWoodType());
        state.model = new StorageModelState(crate.getRotation(partialTicks) * 90.0F, !crate.isLocked());
    }

    @Override
    public void submit(WarehouseCrateRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.renderModel) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        submitNodeCollector.submitModel(this.model, state.model, poseStack, state.texture, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);

        poseStack.popPose();
    }
}
