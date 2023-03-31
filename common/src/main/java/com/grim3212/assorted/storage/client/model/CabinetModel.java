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

public class CabinetModel extends BaseStorageModel {

	private final ModelPart main;
	private final ModelPart door1;
	private final ModelPart door2;
	private final ModelPart handle1;
	private final ModelPart handle2;
	private final ModelPart lock;

	public CabinetModel(ModelPart root) {
		super(RenderType::entityCutout);
		this.main = root.getChild("main");

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
	public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		this.main.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		this.door1.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		this.door2.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);

		if (this.renderHandle) {
			this.handle1.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
			this.handle2.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		} else {
			this.lock.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
		}
	}

	@Override
	public void handleRotations() {
		this.door1.yRot = (this.doorAngle / 90.0F * -1.25F);
		this.door2.yRot = (this.doorAngle / 90.0F * 1.25F);
		this.handle1.yRot = (this.doorAngle / 90.0F * -1.25F);
		this.handle2.yRot = (this.doorAngle / 90.0F * 1.25F);
		this.lock.yRot = (this.doorAngle / 90.0F * -1.25F);
	}
}