package com.grim3212.assorted.storage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CabinetModel extends BaseStorageModel {

	private ModelRenderer cabinetMain;
	private ModelRenderer[] cabinetInt = new ModelRenderer[5];
	private ModelRenderer cabinetDoor1;
	private ModelRenderer cabinetDoor2;
	private ModelRenderer cabinetHandle1;
	private ModelRenderer cabinetLock;
	private ModelRenderer cabinetHandle2;

	private final boolean glassDoor;

	public CabinetModel(boolean useGlass) {
		super(RenderType::entityCutout);
		this.glassDoor = useGlass;
		initModel();
	}

	public void initModel() {
		this.cabinetMain = new ModelRenderer(this, 0, 0).setTexSize(64, 48);
		this.cabinetInt[0] = new ModelRenderer(this, -1, 0).setTexSize(64, 48);
		this.cabinetInt[1] = new ModelRenderer(this, 14, -1).setTexSize(64, 48);
		this.cabinetInt[2] = new ModelRenderer(this, 0, 0).setTexSize(64, 48);
		this.cabinetInt[3] = new ModelRenderer(this, 0, 0).setTexSize(64, 48);
		this.cabinetInt[4] = new ModelRenderer(this, 0, 0).setTexSize(64, 48);
		this.cabinetDoor1 = new ModelRenderer(this, this.glassDoor ? 16 : 0, 32).setTexSize(64, 48);
		this.cabinetDoor2 = new ModelRenderer(this, this.glassDoor ? 16 : 0, 32).setTexSize(64, 48);
		this.cabinetHandle1 = new ModelRenderer(this, 0, 0).setTexSize(64, 48);
		this.cabinetLock = new ModelRenderer(this, 48, 0).setTexSize(64, 48);
		this.cabinetHandle2 = new ModelRenderer(this, 0, 0).setTexSize(64, 48);

		this.cabinetMain.addBox(0.0F, 0.0F, 0.0F, 16, 16, 16);

		this.cabinetInt[0].addBox(0.01f, 0.01f, 0.01f, 0.98f, 15.98f, 15.98f);
		this.cabinetInt[1].addBox(0.01f, 0.01f, 0.01f, 15.98f, 15.98f, 0.98f);
		this.cabinetInt[2].addBox(14.98f, 0.01f, 0.01f, 1, 15.98f, 15.98f);
		this.cabinetInt[3].addBox(0.01f, 15.01f, 0.01f, 15.98f, 0.98f, 15.98f);
		this.cabinetInt[4].addBox(0.01f, 0.01f, 0.01f, 15.98f, 0.98f, 15.98f);

		this.cabinetDoor1.addBox(0.0F, 2.0F, 0.0F, 6, 12, 2, 0.0F);
		this.cabinetDoor1.setPos(2.0F, 0.0F, 15.0F);

		this.cabinetDoor2.addBox(-6.0F, 2.0F, 0.0F, 6, 12, 2, 0.0F);
		this.cabinetDoor2.setPos(14.0F, 0.0F, 15.0F);

		this.cabinetHandle1.addBox(4.0F, 7.0F, 2.0F, 1, 2, 1, 0.0F);
		this.cabinetHandle1.setPos(2.0F, 0.0F, 15.0F);

		this.cabinetLock.addBox(4.5F, 5.0F, 1.0F, 3, 6, 1, 0.0F);
		this.cabinetLock.setPos(2.0F, 0.0F, 15.1F);

		this.cabinetHandle2.addBox(-5.0F, 7.0F, 2.0F, 1, 2, 1, 0.0F);
		this.cabinetHandle2.setPos(14.0F, 0.0F, 15.0F);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.cabinetDoor1.yRot = (this.doorAngle / 90.0F * -1.25F);
		this.cabinetDoor2.yRot = (this.doorAngle / 90.0F * 1.25F);
		this.cabinetHandle1.yRot = (this.doorAngle / 90.0F * -1.25F);
		this.cabinetHandle2.yRot = (this.doorAngle / 90.0F * 1.25F);
		this.cabinetLock.yRot = (this.doorAngle / 90.0F * -1.25F);
		this.cabinetMain.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetInt[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetInt[1].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetInt[2].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetInt[3].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetInt[4].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetDoor1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.cabinetDoor2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (this.renderHandle) {
			this.cabinetHandle1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.cabinetHandle2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		} else {
			this.cabinetLock.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}
}