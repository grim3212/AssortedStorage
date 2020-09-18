package com.grim3212.assorted.storage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DualLockerModel extends BaseStorageModel {

	private ModelRenderer lockerLower;
	private ModelRenderer lockerUpper;
	private ModelRenderer lockerDoor;
	private ModelRenderer lockerHandle;
	private ModelRenderer lockerLock;
	private ModelRenderer lockerLeg1;
	private ModelRenderer lockerLeg2;
	private ModelRenderer lockerLeg3;
	private ModelRenderer lockerLeg4;
	private ModelRenderer lockerShelf;
	private ModelRenderer[] lockerInt = new ModelRenderer[5];

	public DualLockerModel() {
		super(RenderType::getEntityCutout);

		this.lockerLower = new ModelRenderer(this, 64, 0).setTextureSize(128, 128);
		this.lockerUpper = new ModelRenderer(this, 64, 32).setTextureSize(128, 128);
		this.lockerDoor = new ModelRenderer(this, 0, 32).setTextureSize(128, 128);
		this.lockerHandle = new ModelRenderer(this, 48, 0).setTextureSize(128, 128);
		this.lockerLock = new ModelRenderer(this, 64, 0).setTextureSize(128, 128);
		this.lockerLeg1 = new ModelRenderer(this, 0, 0).setTextureSize(128, 128);
		this.lockerLeg2 = new ModelRenderer(this, 0, 0).setTextureSize(128, 128);
		this.lockerLeg3 = new ModelRenderer(this, 0, 0).setTextureSize(128, 128);
		this.lockerLeg4 = new ModelRenderer(this, 0, 0).setTextureSize(128, 128);
		this.lockerShelf = new ModelRenderer(this, 48, 112).setTextureSize(128, 128);
		this.lockerInt[0] = new ModelRenderer(this, 79, 79).setTextureSize(128, 128);
		this.lockerInt[1] = new ModelRenderer(this, 80, 64).setTextureSize(128, 128);
		this.lockerInt[2] = new ModelRenderer(this, 97, 64).setTextureSize(128, 128);
		this.lockerInt[3] = new ModelRenderer(this, 0, 95).setTextureSize(128, 128);
		this.lockerInt[4] = new ModelRenderer(this, 16, 95).setTextureSize(128, 128);

		this.lockerLower.addBox(0.0F, 3.0F, 0.0F, 16, 13, 16);
		this.lockerUpper.addBox(0.0F, 16.0F, 0.0F, 16, 16, 16);

		this.lockerDoor.addBox(0.0F, 5.0F, 0.0F, 12, 25, 2, 0.0F);
		this.lockerDoor.setRotationPoint(2.0F, 0.0F, 15.0F);

		this.lockerHandle.addBox(9.0F, 15.0F, 2.0F, 1, 4, 1, 0.0F);
		this.lockerHandle.setRotationPoint(2.0F, 0.0F, 15.0F);

		this.lockerLock.addBox(9.0F, 12.0F, 1.0F, 3, 6, 1, 0.0F);
		this.lockerLock.setRotationPoint(2.0F, 0.0F, 15.1F);

		this.lockerLeg1.addBox(0.0F, 0.0F, 0.0F, 3, 3, 3);
		this.lockerLeg2.addBox(13.0F, 0.0F, 0.0F, 3, 3, 3);
		this.lockerLeg3.addBox(13.0F, 0.0F, 13.0F, 3, 3, 3);
		this.lockerLeg4.addBox(0.0F, 0.0F, 13.0F, 3, 3, 3);

		this.lockerShelf.addBox(1.01f, 15.502F, 1.01f, 13.98f, 0.98f, 14.98f);

		this.lockerInt[0].addBox(0.01f, 2.01f, 0.01f, 15.98f, 28.98f, 0.98f);
		this.lockerInt[1].addBox(0.01f, 2.01f, 0.01f, 0.98f, 28.98f, 15.98f);
		this.lockerInt[2].addBox(15.01f, 2.01f, 0.01f, 0.98f, 28.98f, 15.98f);
		this.lockerInt[3].addBox(0.01f, 3.01f, 0.01f, 15.98f, 0.98f, 15.98f);
		this.lockerInt[4].addBox(0.01f, 31.01f, 0.01f, 15.98f, 0.98f, 15.98f);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.lockerDoor.rotateAngleY = (-(this.doorAngle / 90.0F));
		this.lockerHandle.rotateAngleY = (-(this.doorAngle / 90.0F));
		this.lockerLock.rotateAngleY = (-(this.doorAngle / 90.0F));
		this.lockerUpper.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerLower.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerDoor.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (this.renderHandle)
			this.lockerHandle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		else
			this.lockerLock.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerLeg1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerLeg2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerLeg3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerLeg4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lockerShelf.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		for (ModelRenderer model : this.lockerInt) {
			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}