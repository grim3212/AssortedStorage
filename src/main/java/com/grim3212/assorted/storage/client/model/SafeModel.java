package com.grim3212.assorted.storage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class SafeModel extends BaseStorageModel {

	private ModelRenderer safeMain;
	private ModelRenderer safeDoor;
	private ModelRenderer safeHandle;
	private ModelRenderer safeLock;
	private ModelRenderer safeLeg1;
	private ModelRenderer safeLeg2;
	private ModelRenderer safeLeg3;
	private ModelRenderer safeLeg4;
	private ModelRenderer[] safeInt = new ModelRenderer[5];

	public SafeModel() {
		super(RenderType::getEntityCutout);

		this.safeMain = new ModelRenderer(this, 0, 0).setTextureSize(64, 48);
		this.safeDoor = new ModelRenderer(this, 0, 32).setTextureSize(64, 48);
		this.safeHandle = new ModelRenderer(this, 48, 0).setTextureSize(64, 48);
		this.safeLock = new ModelRenderer(this, 48, 29).setTextureSize(64, 48);
		this.safeLeg1 = new ModelRenderer(this, 0, 0).setTextureSize(64, 48);
		this.safeLeg2 = new ModelRenderer(this, 0, 0).setTextureSize(64, 48);
		this.safeLeg3 = new ModelRenderer(this, 0, 0).setTextureSize(64, 48);
		this.safeLeg4 = new ModelRenderer(this, 0, 0).setTextureSize(64, 48);
		this.safeInt[0] = new ModelRenderer(this, 14, 15).setTextureSize(64, 48);
		this.safeInt[1] = new ModelRenderer(this, 14, 0).setTextureSize(64, 48);
		this.safeInt[2] = new ModelRenderer(this, 33, 0).setTextureSize(64, 48);
		this.safeInt[3] = new ModelRenderer(this, 0, 0).setTextureSize(64, 48);
		this.safeInt[4] = new ModelRenderer(this, 16, 0).setTextureSize(64, 48);

		this.safeMain.addBox(0.0F, 3.0F, 0.0F, 16, 13, 16);

		this.safeDoor.addBox(0.0F, 6.0F, 0.0F, 10, 7, 2, 0.0F);
		this.safeDoor.setRotationPoint(3.0F, 0.0F, 15.0F);

		this.safeHandle.addBox(7.0F, 8.0F, 2.0F, 1, 3, 1, 0.0F);
		this.safeHandle.setRotationPoint(3.0F, 0.0F, 15.0F);

		this.safeLock.addBox(6.0F, 6.0F, 1.0F, 3, 6, 1, 0.0F);
		this.safeLock.setRotationPoint(3.0F, 0.0F, 15.1F);

		this.safeLeg1.addBox(0.0F, 0.0F, 0.0F, 3, 3, 3);
		this.safeLeg2.addBox(13.0F, 0.0F, 0.0F, 3, 3, 3);
		this.safeLeg3.addBox(13.0F, 0.0F, 13.0F, 3, 3, 3);
		this.safeLeg4.addBox(0.0F, 0.0F, 13.0F, 3, 3, 3);

		this.safeInt[0].addBox(0.01f, 3.01f, 0.01f, 15.98f, 12.98f, 0.98f);
		this.safeInt[1].addBox(0.01f, 3.01f, 0.01f, 0.98f, 12.98f, 15.98f);
		this.safeInt[2].addBox(14.998F, 3.01f, 0.01f, 0.98f, 12.98f, 15.98f);
		this.safeInt[3].addBox(0.01f, 3.01f, 0.01f, 15.98f, 0.98f, 15.98f);
		this.safeInt[4].addBox(0.01f, 15.0F, 0.01f, 15.98f, 0.98f, 15.98f);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.safeDoor.rotateAngleY = (-(this.doorAngle / 90.0F));
		this.safeHandle.rotateAngleY = (-(this.doorAngle / 90.0F));
		this.safeLock.rotateAngleY = (-(this.doorAngle / 90.0F));
		this.safeMain.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.safeDoor.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (this.renderHandle)
			this.safeHandle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		else
			this.safeLock.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.safeLeg1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.safeLeg2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.safeLeg3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.safeLeg4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		for (ModelRenderer model : this.safeInt) {
			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}