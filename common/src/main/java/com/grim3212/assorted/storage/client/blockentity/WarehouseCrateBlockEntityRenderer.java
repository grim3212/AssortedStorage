package com.grim3212.assorted.storage.client.blockentity;

import com.grim3212.assorted.storage.Constants;
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
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarehouseCrateBlockEntityRenderer<T extends BlockEntity & IStorage> implements BlockEntityRenderer<T, WarehouseCrateRenderState> {

    private final WarehouseCrateModel model;
    private static final Identifier OAK = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/oak.png");
    private static final Identifier BIRCH = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/birch.png");
    private static final Identifier SPRUCE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/spruce.png");
    private static final Identifier ACACIA = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/acacia.png");
    private static final Identifier DARK_OAK = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/dark_oak.png");
    private static final Identifier JUNGLE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/jungle.png");
    private static final Identifier WARPED = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/warped.png");
    private static final Identifier CRIMSON = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/crimson.png");
    private static final Identifier MANGROVE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/warehouse_crate/mangrove.png");

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
        state.texture = getTexture(((WarehouseCrateBlock) blockstate.getBlock()).getWoodType());
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

    private static Identifier getTexture(WoodType wood) {
        return switch (wood.name()) {
            case "birch" -> BIRCH;
            case "spruce" -> SPRUCE;
            case "acacia" -> ACACIA;
            case "dark_oak" -> DARK_OAK;
            case "jungle" -> JUNGLE;
            case "crimson" -> CRIMSON;
            case "warped" -> WARPED;
            case "mangrove" -> MANGROVE;
            default -> OAK;
        };
    }
}
