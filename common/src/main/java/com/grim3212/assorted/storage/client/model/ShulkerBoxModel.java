package com.grim3212.assorted.storage.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

/**
 * Mirrors vanilla's {@code ShulkerBoxRenderer.ShulkerBoxModel}, which is now a
 * {@link Model} of the lid progress; the padlock overlay is the one extra piece, so this model's
 * state carries the progress and whether the box is locked.
 * <p>
 * 26.2 renamed the entity render types: {@code RenderPipelines.ENTITY_CUTOUT} is declared
 * {@code withCull(false)}, so {@code entityCutout} is what {@code entityCutoutNoCull} used to be and
 * the culled variant gained the {@code Cull} suffix. That is the render type used here, because the
 * model's own {@code entityCutoutNoCullZOffset} never actually applied in 1.20.1 - the block entity
 * renderer opened its {@code VertexConsumer} with {@code entityCutoutNoCull} and overrode it. The
 * submit API takes the render type from the model, so it has to be the one that was really in use.
 */
public class ShulkerBoxModel extends Model<ShulkerBoxModel.State> {
    private static final String LID = "lid";
    private static final String BASE = "base";
    private static final String LOCK = "lock";
    private final ModelPart lid;
    private final ModelPart lock;

    /**
     * @param progress How far the lid is open, 0-1.
     * @param locked   Whether to draw the padlock overlay.
     */
    public record State(float progress, boolean locked) {
    }

    public ShulkerBoxModel(ModelPart modelPart) {
        super(modelPart, RenderTypes::entityCutout);
        this.lid = modelPart.getChild(LID);
        this.lock = modelPart.getChild(LOCK);
    }

    public static LayerDefinition createBaseMeshDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild(LOCK, CubeListBuilder.create().texOffs(0, 24).addBox(-8.2F, 12.0F, -2.0F, 0.2F, 6.0F, 4.0F).addBox(8.2F, 12.0F, -2.0F, 0.2F, 6.0F, 4.0F).texOffs(0, 28).addBox(-2.0F, 12.0F, -8.2F, 4.0F, 6.0F, 0.0F).addBox(-2.0F, 12.0F, 8.2F, 4.0F, 6.0F, 0.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ShulkerBoxModel.State state) {
        super.setupAnim(state);

        float y = state.progress() * 0.5F * 16.0F;
        float yRot = 270.0F * state.progress() * ((float) Math.PI / 180F);

        this.lid.setPos(0.0F, 24.0F - y, 0.0F);
        this.lid.yRot = yRot;

        this.lock.setPos(0.0F, -y, 0.0F);
        this.lock.yRot = yRot;
        this.lock.visible = state.locked();
    }
}
