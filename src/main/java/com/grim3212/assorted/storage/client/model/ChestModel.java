package com.grim3212.assorted.storage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ChestModel extends BaseStorageModel {

	private final ModelRenderer lid;
	private final ModelRenderer bottom;
	private final ModelRenderer lock;

	public ChestModel() {
		super(RenderType::entityCutout);
		this.bottom = new ModelRenderer(64, 64, 0, 19);
		this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.lid = new ModelRenderer(64, 64, 0, 0);
		this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.lid.y = 9.0F;
		this.lid.z = 1.0F;
		this.lock = new ModelRenderer(64, 64, 0, 0);
		this.lock.texOffs(1, 7);
		this.lock.addBox(7.0F, 3.0F, 15.0F, 2.0F, 1.0F, 1.0F, 0.0F);
		this.lock.texOffs(0, 4);
		this.lock.addBox(6.0F, 0.0F, 15.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		this.lock.addBox(9.0F, 0.0F, 15.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		this.lock.texOffs(0, 0);
		this.lock.addBox(6.0F, -3.0F, 15.0F, 4.0F, 3.0F, 1.0F, 0.0F);
		this.lock.y = 8.0F;
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.lid.xRot = ((float) Math.toRadians(this.doorAngle / -1.0F));
		this.lock.xRot = lid.xRot;
		this.bottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lid.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.lock.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

}
