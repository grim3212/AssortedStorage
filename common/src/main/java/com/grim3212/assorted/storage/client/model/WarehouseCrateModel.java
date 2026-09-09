package com.grim3212.assorted.storage.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class WarehouseCrateModel extends BaseStorageModel {

	private final ModelPart lid;
	private final ModelPart lock;

	public WarehouseCrateModel(ModelPart root) {
		super(root, RenderTypes::entityCutoutCull);

		this.lid = root.getChild("lid");
		this.lock = root.getChild("lock");
	}

	public static LayerDefinition createBaseMeshDefinition() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 1.0F, 1.0F, 1, 14, 14), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall2", CubeListBuilder.create().texOffs(0, 0).addBox(14.0F, 1.0F, 1.0F, 1, 14, 14), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall3", CubeListBuilder.create().texOffs(0, 28).addBox(2.0F, 1.0F, 1.0F, 12, 14, 1), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall4", CubeListBuilder.create().texOffs(0, 28).addBox(2.0F, 1.0F, 14.0F, 12, 14, 1), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall5", CubeListBuilder.create().texOffs(0, 43).addBox(2.0F, 1.0F, 2.0F, 12, 1, 12), PartPose.ZERO);
		partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 76).addBox(0.01F, 0.0F, 0.01F, 16, 2, 16), PartPose.offset(0.0F, 14.0F, 0.0F));

		partdefinition1.addOrReplaceChild("bar1", CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, 0.01F, 0.0F, 2, 15, 2), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar2", CubeListBuilder.create().texOffs(30, 0).addBox(14.0F, 0.01F, 14.0F, 2, 15, 2), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar3", CubeListBuilder.create().texOffs(30, 0).addBox(0.0F, 0.01F, 14.0F, 2, 15, 2), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar4", CubeListBuilder.create().texOffs(30, 0).addBox(14.0F, 0.01F, 0.0F, 2, 15, 2), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar5", CubeListBuilder.create().texOffs(30, 17).addBox(2.0F, 0.0F, 0.0F, 12, 2, 2), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar6", CubeListBuilder.create().texOffs(30, 17).addBox(2.0F, 0.0F, 14.0F, 12, 2, 2), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar7", CubeListBuilder.create().texOffs(30, 21).addBox(0.0F, 0.0F, 2.0F, 2, 2, 12), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar8", CubeListBuilder.create().texOffs(30, 21).addBox(14.0F, 0.0F, 2.0F, 2, 2, 12), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("bar9", CubeListBuilder.create().texOffs(0, 56).addBox(0.0F, 0.0F, 0.0F, 2, 18, 1), PartPose.offsetAndRotation(1.5F, 2.25F, 0.01F, 0, 0, ((float) Math.toRadians(-45.0D))));
		partdefinition1.addOrReplaceChild("bar10", CubeListBuilder.create().texOffs(0, 56).addBox(0.0F, 0.0F, 0.0F, 2, 18, 1), PartPose.offsetAndRotation(13.0F, 0.85F, 14.99F, 0, 0, ((float) Math.toRadians(45.0D))));
		partdefinition1.addOrReplaceChild("bar11", CubeListBuilder.create().texOffs(6, 56).addBox(0.0F, 0.0F, 0.0F, 1, 18, 2), PartPose.offsetAndRotation(0.01F, 2.25F, 1.5F, ((float) Math.toRadians(45.0D)), 0, 0));
		partdefinition1.addOrReplaceChild("bar12", CubeListBuilder.create().texOffs(6, 56).addBox(0.0F, 0.0F, 0.0F, 1, 18, 2), PartPose.offsetAndRotation(14.99F, 0.85F, 13.0F, ((float) Math.toRadians(-45.0D)), 0, 0));
		partdefinition1.addOrReplaceChild("bar13", CubeListBuilder.create().texOffs(6, 56).addBox(0.0F, 0.0F, 0.0F, 1, 18, 2), PartPose.offsetAndRotation(1.0F, 0.01F, 2.0F, ((float) Math.toRadians(135.0D)), 0, ((float) Math.toRadians(90.0D))));
		partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(56, 0).addBox(0.0F, -4.0F, 15.05F, 3, 6, 1).mirror(), PartPose.offset(6.5F, 14.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 94);
	}

	@Override
	public void setupAnim(StorageModelState state) {
		super.setupAnim(state);

		float xRot = (float) Math.toRadians(state.doorAngle() / -3.0F);
		this.lid.xRot = xRot;
		this.lock.xRot = xRot;

		// The warehouse crate has no handle; the padlock simply disappears when it is unlocked.
		this.lock.visible = !state.renderHandle();
	}
}
