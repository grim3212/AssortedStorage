package com.grim3212.assorted.storage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class WarehouseCrateModel extends BaseStorageModel {

	private ModelRenderer crateLid;
	private ModelRenderer crateLock;
	private ModelRenderer[] crateMain = new ModelRenderer[5];
	private ModelRenderer[] crateBars = new ModelRenderer[13];

	public WarehouseCrateModel() {
		super(RenderType::entityCutout);
		init();
	}

	public void init() {
		this.crateMain[0] = new ModelRenderer(this, 0, 0).setTexSize(64, 94);
		this.crateMain[1] = new ModelRenderer(this, 0, 0).setTexSize(64, 94);
		this.crateMain[2] = new ModelRenderer(this, 0, 28).setTexSize(64, 94);
		this.crateMain[3] = new ModelRenderer(this, 0, 28).setTexSize(64, 94);
		this.crateMain[4] = new ModelRenderer(this, 0, 43).setTexSize(64, 94);

		this.crateBars[0] = new ModelRenderer(this, 30, 0).setTexSize(64, 94);
		this.crateBars[1] = new ModelRenderer(this, 30, 0).setTexSize(64, 94);
		this.crateBars[2] = new ModelRenderer(this, 30, 0).setTexSize(64, 94);
		this.crateBars[3] = new ModelRenderer(this, 30, 0).setTexSize(64, 94);
		this.crateBars[4] = new ModelRenderer(this, 30, 17).setTexSize(64, 94);
		this.crateBars[5] = new ModelRenderer(this, 30, 17).setTexSize(64, 94);
		this.crateBars[6] = new ModelRenderer(this, 30, 21).setTexSize(64, 94);
		this.crateBars[7] = new ModelRenderer(this, 30, 21).setTexSize(64, 94);
		this.crateBars[8] = new ModelRenderer(this, 0, 56).setTexSize(64, 94);
		this.crateBars[9] = new ModelRenderer(this, 0, 56).setTexSize(64, 94);
		this.crateBars[10] = new ModelRenderer(this, 6, 56).setTexSize(64, 94);
		this.crateBars[11] = new ModelRenderer(this, 6, 56).setTexSize(64, 94);
		this.crateBars[12] = new ModelRenderer(this, 6, 56).setTexSize(64, 94);

		this.crateLid = new ModelRenderer(this, 0, 76).setTexSize(64, 94);
		this.crateLock = new ModelRenderer(this, 56, 0).setTexSize(64, 94);

		this.crateMain[0].addBox(1.0F, 1.0F, 1.0F, 1, 14, 14);
		this.crateMain[1].addBox(14.0F, 1.0F, 1.0F, 1, 14, 14);
		this.crateMain[2].addBox(2.0F, 1.0F, 1.0F, 12, 14, 1);
		this.crateMain[3].addBox(2.0F, 1.0F, 14.0F, 12, 14, 1);
		this.crateMain[4].addBox(2.0F, 1.0F, 2.0F, 12, 1, 12);

		this.crateBars[0].addBox(0.0F, 0.01F, 0.0F, 2, 15, 2);
		this.crateBars[1].addBox(14.0F, 0.01F, 14.0F, 2, 15, 2);
		this.crateBars[2].addBox(0.0F, 0.01F, 14.0F, 2, 15, 2);
		this.crateBars[3].addBox(14.0F, 0.01F, 0.0F, 2, 15, 2);
		this.crateBars[4].addBox(2.0F, 0.0F, 0.0F, 12, 2, 2);
		this.crateBars[5].addBox(2.0F, 0.0F, 14.0F, 12, 2, 2);
		this.crateBars[6].addBox(0.0F, 0.0F, 2.0F, 2, 2, 12);
		this.crateBars[7].addBox(14.0F, 0.0F, 2.0F, 2, 2, 12);

		this.crateBars[8].addBox(0.0F, 0.0F, 0.0F, 2, 18, 1);
		this.crateBars[8].setPos(1.5F, 2.25F, 0.01F);
		this.crateBars[8].zRot = ((float) Math.toRadians(-45.0D));

		this.crateBars[9].addBox(0.0F, 0.0F, 0.0F, 2, 18, 1);
		this.crateBars[9].setPos(13.0F, 0.85F, 14.99F);
		this.crateBars[9].zRot = ((float) Math.toRadians(45.0D));

		this.crateBars[10].addBox(0.0F, 0.0F, 0.0F, 1, 18, 2);
		this.crateBars[10].setPos(0.01F, 2.25F, 1.5F);
		this.crateBars[10].xRot = ((float) Math.toRadians(45.0D));

		this.crateBars[11].addBox(0.0F, 0.0F, 0.0F, 1, 18, 2);
		this.crateBars[11].setPos(14.99F, 0.85F, 13.0F);
		this.crateBars[11].xRot = ((float) Math.toRadians(-45.0D));

		this.crateBars[12].addBox(0.0F, 0.0F, 0.0F, 1, 18, 2);
		this.crateBars[12].setPos(1.0F, 0.01F, 2.0F);
		this.crateBars[12].xRot = ((float) Math.toRadians(135.0D));
		this.crateBars[12].zRot = ((float) Math.toRadians(90.0D));

		this.crateLid.addBox(0.01F, 0.0F, 0.01F, 16, 2, 16);
		this.crateLid.setPos(0.0F, 14.0F, 0.0F);

		this.crateLock.addBox(0.0F, -4.0F, 15.05F, 3, 6, 1);
		this.crateLock.setPos(6.5F, 14.0F, 0.0F);
		this.crateLock.mirror = true;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.crateLid.xRot = ((float) Math.toRadians(this.doorAngle / -3.0F));
		this.crateLock.xRot = ((float) Math.toRadians(this.doorAngle / -3.0F));

		this.crateMain[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateMain[1].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateMain[2].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateMain[3].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateMain[4].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		this.crateBars[0].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[1].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[2].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[3].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[4].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[5].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[6].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[7].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[8].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[9].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[10].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[11].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.crateBars[12].render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		this.crateLid.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (!this.renderHandle)
			this.crateLock.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

	}
}