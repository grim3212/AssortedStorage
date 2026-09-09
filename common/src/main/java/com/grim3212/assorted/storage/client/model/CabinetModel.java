package com.grim3212.assorted.storage.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class CabinetModel extends BaseStorageModel {

	private final ModelPart door1;
	private final ModelPart door2;
	private final ModelPart handle1;
	private final ModelPart handle2;
	private final ModelPart lock;

	public CabinetModel(ModelPart root) {
		super(root, RenderTypes::entityCutoutCull);

		this.door1 = root.getChild("door1");
		this.door2 = root.getChild("door2");
		this.handle1 = root.getChild("handle1");
		this.handle2 = root.getChild("handle2");
		this.lock = root.getChild("lock");
	}

	public static LayerDefinition createBaseMeshDefinition(boolean glassDoor) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16, 16, 16), PartPose.ZERO);
		partdefinition.addOrReplaceChild("door1", CubeListBuilder.create().texOffs(glassDoor ? 16 : 0, 32).addBox(0.0F, 2.0F, 0.0F, 6, 12, 2), PartPose.offset(2.0F, 0.0F, 15.0F));
		partdefinition.addOrReplaceChild("door2", CubeListBuilder.create().texOffs(glassDoor ? 16 : 0, 32).addBox(-6.0F, 2.0F, 0.0F, 6, 12, 2), PartPose.offset(14.0F, 0.0F, 15.0F));
		partdefinition1.addOrReplaceChild("wall1", CubeListBuilder.create().texOffs(-1, 0).addBox(0.01f, 0.01f, 0.01f, 0.98f, 15.98f, 15.98f), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall2", CubeListBuilder.create().texOffs(14, -1).addBox(0.01f, 0.01f, 0.01f, 15.98f, 15.98f, 0.98f), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall3", CubeListBuilder.create().texOffs(0, 0).addBox(14.98f, 0.01f, 0.01f, 1, 15.98f, 15.98f), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall4", CubeListBuilder.create().texOffs(0, 0).addBox(0.01f, 15.01f, 0.01f, 15.98f, 0.98f, 15.98f), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall5", CubeListBuilder.create().texOffs(0, 0).addBox(0.01f, 0.01f, 0.01f, 15.98f, 0.98f, 15.98f), PartPose.ZERO);

		partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(48, 0).addBox(4.5F, 5.0F, 1.0F, 3, 6, 1), PartPose.offset(2.0F, 0.0F, 15.1F));
		partdefinition.addOrReplaceChild("handle1", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 7.0F, 2.0F, 1, 2, 1), PartPose.offset(2.0F, 0.0F, 15.0F));
		partdefinition.addOrReplaceChild("handle2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 7.0F, 2.0F, 1, 2, 1), PartPose.offset(14.0F, 0.0F, 15.0F));
		return LayerDefinition.create(meshdefinition, 64, 48);
	}

	@Override
	public void setupAnim(StorageModelState state) {
		super.setupAnim(state);

		float angle = state.doorAngle();
		this.door1.yRot = (angle / 90.0F * -1.25F);
		this.door2.yRot = (angle / 90.0F * 1.25F);
		this.handle1.yRot = (angle / 90.0F * -1.25F);
		this.handle2.yRot = (angle / 90.0F * 1.25F);
		this.lock.yRot = (angle / 90.0F * -1.25F);

		this.handle1.visible = state.renderHandle();
		this.handle2.visible = state.renderHandle();
		this.lock.visible = !state.renderHandle();
	}
}
