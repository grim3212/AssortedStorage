package com.grim3212.assorted.storage.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class LockerModel extends BaseStorageModel {

	private final ModelPart door;
	private final ModelPart handle;
	private final ModelPart lock;

	public LockerModel(ModelPart root) {
		super(root, RenderTypes::entityCutoutCull);

		this.door = root.getChild("door");
		this.handle = root.getChild("handle");
		this.lock = root.getChild("lock");
	}

	public static LayerDefinition createBaseMeshDefinition() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 3.0F, 0.0F, 16, 13, 16), PartPose.ZERO);
		partdefinition.addOrReplaceChild("door", CubeListBuilder.create().texOffs(32, 32).addBox(0.0F, 5.0F, 0.0F, 12, 9, 2), PartPose.offset(2.0F, 0.0F, 15.0F));
		partdefinition1.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 3, 3, 3), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 0).addBox(13.0F, 0.0F, 0.0F, 3, 3, 3), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(0, 0).addBox(13.0F, 0.0F, 13.0F, 3, 3, 3), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 13.0F, 3, 3, 3), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall1", CubeListBuilder.create().texOffs(15, 79).addBox(0.0F, 2.0F, 0.0F, 16, 13, 1), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall2", CubeListBuilder.create().texOffs(16, 64).addBox(0.0F, 2.0F, 0.0F, 1, 13, 16), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall3", CubeListBuilder.create().texOffs(33, 64).addBox(15.0F, 2.0F, 0.0F, 1, 13, 16), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall4", CubeListBuilder.create().texOffs(0, 95).addBox(0.0F, 3.0F, 0.0F, 16, 1, 16), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("wall5", CubeListBuilder.create().texOffs(16, 95).addBox(0.0F, 15.0F, 0.0F, 16, 1, 16), PartPose.ZERO);
		partdefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(64, 0).addBox(9.0F, 6.0F, 1.0F, 3, 6, 1), PartPose.offset(2.0F, 0.0F, 15.1F));
		partdefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(48, 0).addBox(9.0F, 9.0F, 2.0F, 1, 3, 1), PartPose.offset(2.0F, 0.0F, 15.0F));
		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(StorageModelState state) {
		super.setupAnim(state);

		float yRot = -(state.doorAngle() / 90.0F);
		this.door.yRot = yRot;
		this.handle.yRot = yRot;
		this.lock.yRot = yRot;

		this.handle.visible = state.renderHandle();
		this.lock.visible = !state.renderHandle();
	}
}
