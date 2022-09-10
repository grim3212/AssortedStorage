package com.grim3212.assorted.storage.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxModel extends Model {
	private static final String LID = "lid";
	private static final String BASE = "base";
	private static final String LOCK = "lock";
	private final ModelPart base;
	private final ModelPart lid;
	private final ModelPart lock;
	public boolean isLocked = false;

	public ShulkerBoxModel(ModelPart modelPart) {
		super(RenderType::entityCutoutNoCullZOffset);
		this.lid = modelPart.getChild(LID);
		this.base = modelPart.getChild(BASE);
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

	public void renderToBuffer(PoseStack p_103013_, VertexConsumer p_103014_, int p_103015_, int p_103016_, float p_103017_, float p_103018_, float p_103019_, float p_103020_) {
		this.parts().forEach((p_103030_) -> {
			p_103030_.render(p_103013_, p_103014_, p_103015_, p_103016_, p_103017_, p_103018_, p_103019_, p_103020_);
		});

		if (this.isLocked) {
			this.lock.render(p_103013_, p_103014_, p_103015_, p_103016_, p_103017_, p_103018_, p_103019_, p_103020_);
		}
	}

	public Iterable<ModelPart> parts() {
		return ImmutableList.of(this.base, this.lid);
	}

	public void updateLid(float x, float y, float z, float yRot) {
		this.lid.setPos(x, 24.0F - y, z);
		this.lid.yRot = yRot;

		this.lock.setPos(x, -y, z);
		this.lock.yRot = yRot;
	}
}
