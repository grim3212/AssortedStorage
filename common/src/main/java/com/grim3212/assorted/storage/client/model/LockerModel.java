package com.grim3212.assorted.storage.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class LockerModel extends BaseStorageModel {

	private final ModelPart main;
	private final ModelPart door;
	private final ModelPart handle;
	private final ModelPart lock;

	public LockerModel(ModelPart root) {
		super(RenderType::entityCutout);
		this.main = root.getChild("main");

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
	public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		this.main.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		this.door.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);

		if (this.renderHandle) {
			this.handle.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		} else {
			this.lock.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		}
	}

	@Override
	public void handleRotations() {
		this.door.yRot = (-(this.doorAngle / 90.0F));
		this.handle.yRot = (-(this.doorAngle / 90.0F));
		this.lock.yRot = (-(this.doorAngle / 90.0F));
	}
}