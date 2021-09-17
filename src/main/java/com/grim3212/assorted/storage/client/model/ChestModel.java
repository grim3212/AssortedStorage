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

public class ChestModel extends BaseStorageModel {

	private final ModelPart main;
	private final ModelPart lid;
	private final ModelPart lock;

	public ChestModel(ModelPart root) {
		super(RenderType::entityCutout);
		this.main = root.getChild("main");
		this.lid = this.main.getChild("lid");
		this.lock = this.main.getChild("lock");
	}

	public static LayerDefinition createBaseMeshDefinition() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
		partdefinition1.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
		partdefinition1.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(1, 7).addBox(7.0F, 3.0F, 15.0F, 2.0F, 1.0F, 1.0F).texOffs(0, 4).addBox(6.0F, 0.0F, 15.0F, 1.0F, 3.0F, 1.0F).addBox(9.0F, 0.0F, 15.0F, 1.0F, 3.0F, 1.0F).texOffs(0, 0).addBox(6.0F, -3.0F, 15.0F, 4.0F, 3.0F, 1.0F), PartPose.offset(0.0F, 8.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
		this.main.render(stack, buffer, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
	}

	@Override
	public void handleRotations() {
		this.lid.xRot = ((float) Math.toRadians(this.doorAngle / -1.0F));
		this.lock.xRot = lid.xRot;
	}

}
