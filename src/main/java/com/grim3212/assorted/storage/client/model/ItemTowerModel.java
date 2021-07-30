package com.grim3212.assorted.storage.client.model;

import java.util.List;

import com.google.common.collect.Lists;
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

public class ItemTowerModel extends Model {

	public boolean topBlock = false;
	public boolean bottomBlock = false;
	public double nextRender = 0.0D;

	public int frame = 0;
	public boolean framedir = false;
	public int FramesCount = 100;
	public float[] BlankFrames = new float[100];
	public float[] TopSectionFramesY = new float[100];
	public float[] TopSectionFramesZ = new float[100];
	public float[] MidSectionFramesY = new float[100];
	public float[] MidSectionFramesZ = new float[100];
	public float[] BottomSectionFramesY = new float[100];
	public float[] BottomSectionFramesZ = new float[100];

	private final ModelPart main;
	private final ModelPart posts;
	private final ModelPart cap1;
	private final ModelPart cap2;
	private final ModelPart shelf1;
	private final ModelPart shelf2;
	private final ModelPart shelf3;
	private final ModelPart shelf4;
	private final ModelPart[] sidebars;
	private final ModelPart[] midbars;

	public ItemTowerModel(ModelPart root) {
		super(RenderType::entityCutout);

		this.main = root.getChild("main");
		this.posts = root.getChild("posts");
		this.cap1 = root.getChild("cap1");
		this.cap2 = root.getChild("cap2");

		this.shelf1 = root.getChild("shelf_1");
		this.shelf2 = root.getChild("shelf_2");
		this.shelf3 = root.getChild("shelf_3");
		this.shelf4 = root.getChild("shelf_4");

		this.sidebars = new ModelPart[8];
		ModelPart sidebarRoot = root.getChild("sidebars");
		for (int i = 1; i <= 8; i++) {
			this.sidebars[i - 1] = sidebarRoot.getChild("sidebar" + i);
		}

		this.midbars = new ModelPart[5];
		ModelPart midbarRoot = root.getChild("midbars");
		for (int i = 1; i <= 5; i++) {
			this.midbars[i - 1] = midbarRoot.getChild("midbar" + i);
		}

		List<Float> bottomy = Lists.newArrayList();
		List<Float> bottomz = Lists.newArrayList();
		List<Float> midy = Lists.newArrayList();
		List<Float> midz = Lists.newArrayList();
		List<Float> topy = Lists.newArrayList();
		List<Float> topz = Lists.newArrayList();
		float height = 0.0F;
		float length = 0.0F;

		for (int i = 0; i < 100; i++) {
			height = i - 50.0F;
			height /= 50.0F;
			height = Math.abs(height);
			height = 1.0F - height;
			midy.add(height * 13.0F);

			if (i < 50) {
				midz.add(10.0F);
			} else
				midz.add(3.0F);

		}

		for (int i = 0; i < 100; i++) {
			height = i - 50.0F;
			height /= 50.0F;
			height = Math.abs(height);
			height = 1.0F - height;
			topy.add(height * 13.0F);

			if (i < 30) {
				topz.add(10.0F);
			}

			if ((i >= 30) && (i <= 70)) {
				length = i - 30;
				length /= 40.0F;

				topz.add(10.0F - length * 7.0F);
			}

			if (i > 70) {
				topz.add(3.0F);
			}

		}

		for (int i = 0; i < 100; i++) {
			height = i - 50.0F;
			height /= 50.0F;
			height = Math.abs(height);
			bottomy.add(height * 13.0F);

			if (i < 30) {
				bottomz.add(3.0F);
			}

			if ((i >= 30) && (i <= 70)) {
				length = i - 30;
				length /= 40.0F;

				bottomz.add(3.0F + length * 7.0F);
			}

			if (i > 70) {
				bottomz.add(10.0F);
			}

		}

		for (int i = 0; i < this.FramesCount; i++) {
			this.BottomSectionFramesY[i] = ((Float) bottomy.get(i)).floatValue();
			this.BottomSectionFramesZ[i] = ((Float) bottomz.get(i)).floatValue();

			this.MidSectionFramesY[i] = ((Float) midy.get(i)).floatValue();
			this.MidSectionFramesZ[i] = ((Float) midz.get(i)).floatValue();

			this.TopSectionFramesY[i] = ((Float) topy.get(i)).floatValue();
			this.TopSectionFramesZ[i] = ((Float) topz.get(i)).floatValue();
		}
	}

	public static LayerDefinition createBaseMeshDefinition() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition maindefinition = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16, 16, 16), PartPose.ZERO);
		maindefinition.addOrReplaceChild("mainAlt", CubeListBuilder.create().texOffs(0, 0).addBox(16.01F, 16.01F, 16.01F, -16, -16, -16), PartPose.ZERO);
		partdefinition.addOrReplaceChild("cap1", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, 0.0F, 0.0F, 16, 1, 16), PartPose.ZERO);
		partdefinition.addOrReplaceChild("cap2", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, 15.0F, 0.0F, 16, 1, 16), PartPose.ZERO);

		PartDefinition postsdefinition = partdefinition.addOrReplaceChild("posts", CubeListBuilder.create(), PartPose.ZERO);
		postsdefinition.addOrReplaceChild("post1", CubeListBuilder.create().texOffs(0, 80).addBox(0.0F, 0.0F, 0.0F, 3, 16, 3), PartPose.ZERO);
		postsdefinition.addOrReplaceChild("post2", CubeListBuilder.create().texOffs(12, 80).addBox(0.0F, 0.0F, 13.0F, 3, 16, 3), PartPose.ZERO);
		postsdefinition.addOrReplaceChild("post3", CubeListBuilder.create().texOffs(0, 99).addBox(13.0F, 0.0F, 0.0F, 3, 16, 3), PartPose.ZERO);
		postsdefinition.addOrReplaceChild("post4", CubeListBuilder.create().texOffs(12, 99).addBox(13.0F, 0.0F, 13.0F, 3, 16, 3), PartPose.ZERO);

		PartDefinition sidebarsdefinition = partdefinition.addOrReplaceChild("sidebars", CubeListBuilder.create(), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar1", CubeListBuilder.create().texOffs(0, 32).addBox(3.0F, 0.0F, 0.0F, 10, 1, 1), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar2", CubeListBuilder.create().texOffs(0, 34).addBox(0.0F, 0.0F, 3.0F, 1, 1, 10), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar3", CubeListBuilder.create().texOffs(0, 32).addBox(3.0F, 0.0F, 15.0F, 10, 1, 1), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar4", CubeListBuilder.create().texOffs(0, 34).addBox(15.0F, 0.0F, 3.0F, 1, 1, 10), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar5", CubeListBuilder.create().texOffs(0, 32).addBox(3.0F, 15.0F, 0.0F, 10, 1, 1), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar6", CubeListBuilder.create().texOffs(0, 34).addBox(0.0F, 15.0F, 3.0F, 1, 1, 10), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar7", CubeListBuilder.create().texOffs(0, 32).addBox(3.0F, 15.0F, 15.0F, 10, 1, 1), PartPose.ZERO);
		sidebarsdefinition.addOrReplaceChild("sidebar8", CubeListBuilder.create().texOffs(0, 34).addBox(15.0F, 15.0F, 3.0F, 1, 1, 10), PartPose.ZERO);

		PartDefinition midbarsdefinition = partdefinition.addOrReplaceChild("midbars", CubeListBuilder.create(), PartPose.ZERO);
		midbarsdefinition.addOrReplaceChild("midbar1", CubeListBuilder.create().texOffs(0, 45).addBox(0.0F, 7.0F, 7.0F, 16, 2, 2), PartPose.ZERO);
		midbarsdefinition.addOrReplaceChild("midbar2", CubeListBuilder.create().texOffs(48, 32).addBox(6.0F, 0.0F, 6.0F, 4, 12, 4), PartPose.ZERO);
		midbarsdefinition.addOrReplaceChild("midbar3", CubeListBuilder.create().texOffs(48, 32).addBox(6.0F, 0.0F, 6.0F, 4, 16, 4), PartPose.ZERO);
		midbarsdefinition.addOrReplaceChild("midbar4", CubeListBuilder.create().texOffs(48, 32).addBox(6.0F, 4.0F, 6.0F, 4, 12, 4), PartPose.ZERO);
		midbarsdefinition.addOrReplaceChild("midbar5", CubeListBuilder.create().texOffs(48, 32).addBox(6.0F, 1.0F, 6.0F, 4, 14, 4), PartPose.ZERO);

		makeShelf(partdefinition, 1);
		makeShelf(partdefinition, 2);
		makeShelf(partdefinition, 3);
		makeShelf(partdefinition, 4);

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	private static void makeShelf(PartDefinition partdefinition, int shelfNum) {
		PartDefinition shelf = partdefinition.addOrReplaceChild("shelf_" + shelfNum, CubeListBuilder.create().texOffs(0, 64).addBox(1.0F, 0.0F, 0.0F, 14, 1, 3), PartPose.ZERO);
		shelf.addOrReplaceChild("shelf_part2", CubeListBuilder.create().texOffs(0, 68).addBox(1.0F, 1.0F, 0.0F, 1, 1, 3), PartPose.ZERO);
		shelf.addOrReplaceChild("shelf_part3", CubeListBuilder.create().texOffs(0, 68).addBox(14.0F, 1.0F, 0.0F, 1, 1, 3), PartPose.ZERO);
		shelf.addOrReplaceChild("shelf_part4", CubeListBuilder.create().texOffs(0, 72).addBox(2.0F, 1.0F, 0.0F, 12, 1, 1), PartPose.ZERO);
		shelf.addOrReplaceChild("shelf_part5", CubeListBuilder.create().texOffs(0, 72).addBox(2.0F, 1.0F, 2.0F, 12, 1, 1), PartPose.ZERO);
	}

	public void renderModelInventory(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.main.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.posts.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cap1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cap2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.midbars[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.midbars[4].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.bottomBlock && this.topBlock) {
			this.main.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.midbars[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.midbars[2].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		if (!this.bottomBlock && !this.topBlock) {
			this.main.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.midbars[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.midbars[4].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		if (this.bottomBlock && this.topBlock) {
			for (ModelPart model : this.sidebars) {
				model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			}

		}

		if (this.bottomBlock && !this.topBlock) {
			this.sidebars[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.sidebars[1].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.sidebars[2].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.sidebars[3].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.midbars[1].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		if (!this.bottomBlock && this.topBlock) {
			this.sidebars[4].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.sidebars[5].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.sidebars[6].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.sidebars[7].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.midbars[3].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}

		this.posts.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (!this.bottomBlock)
			this.cap1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (!this.topBlock)
			this.cap2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		processFrame(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public void setAnimation(int animation) {
		if (animation > 0) {
			this.framedir = true;
			this.frame = 0;
		}

		if (animation < 0) {
			this.framedir = false;
			this.frame = 25;
		}
	}

	public void processFrame(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (System.currentTimeMillis() > this.nextRender && (this.topBlock || this.bottomBlock)) {
			if (this.framedir) {
				if (this.frame < 25) {
					this.frame += 1;
				}
			} else if (this.frame > 0) {
				this.frame -= 1;
			}

		}

		renderFramePosition(this.shelf1, this.frame, 5, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		renderFramePosition(this.shelf2, this.frame, 30, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		renderFramePosition(this.shelf3, this.frame, 55, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		renderFramePosition(this.shelf4, this.frame, 80, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.nextRender = (System.currentTimeMillis() + 1.0D);
	}

	public void renderFramePosition(ModelPart model, int frame, int i, PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		int i2 = frame;
		i2 += i;
		while (i2 > 100)
			i2 -= 100;
		if (i2 > 0)
			i2--;

		float x = 0.0F;
		float y = getFrameYTable()[i2] / 16.0F;
		float z = getFrameZTable()[i2] / 16.0F;

		matrixStackIn.translate(x, y, z);
		model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.translate(-x, -y, -z);
	}

	public float[] getFrameYTable() {
		if (this.topBlock && !this.bottomBlock) {
			return this.BottomSectionFramesY;
		}

		if (!this.topBlock && this.bottomBlock) {
			return this.TopSectionFramesY;
		}

		if ((this.topBlock && this.bottomBlock) || (!this.topBlock && !this.bottomBlock)) {
			return this.MidSectionFramesY;
		}

		return this.BlankFrames;
	}

	public float[] getFrameZTable() {
		if (this.topBlock && !this.bottomBlock) {
			return this.BottomSectionFramesZ;
		}

		if (!this.topBlock && this.bottomBlock) {
			return this.TopSectionFramesZ;
		}

		if ((this.topBlock && this.bottomBlock) || (!this.topBlock && !this.bottomBlock)) {
			return this.MidSectionFramesZ;
		}

		return this.BlankFrames;
	}
}