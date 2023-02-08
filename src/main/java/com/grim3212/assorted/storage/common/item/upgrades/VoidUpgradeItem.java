package com.grim3212.assorted.storage.common.item.upgrades;

import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.client.render.RenderHelper;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT, _interface = ICrateUpgradeRenderer.class)
public class VoidUpgradeItem extends BasicCrateUpgradeItem implements ICrateUpgradeRenderer {

	public VoidUpgradeItem(Properties props) {
		super(props);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Direction facing = tileEntityIn.getBlockState().getValue(CrateBlock.FACING);

		matrixStack.pushPose();
		float rot = facing.toYRot();
		matrixStack.translate(0.5F, 0.5F, 0.5F);
		if (facing.getAxis().isHorizontal()) {
			matrixStack.mulPose(Axis.YP.rotationDegrees(-rot));
		} else {
			float f = facing == Direction.DOWN ? 90F : 270F;
			matrixStack.mulPose(Axis.XP.rotationDegrees(f));
		}

		matrixStack.translate(-0.5D, 0.5D, 0.45D);
		float scale = 0.012F * 0.6666667F;
		matrixStack.scale(scale, -scale, scale);
		RenderSystem.enableBlend();
		VertexConsumer vertexConsumer = bufferIn.getBuffer(CrateBlockEntityRenderer.ICONS);
		RenderHelper.lightedBlit(vertexConsumer, matrixStack, 116, 116, 7, 0, 0, 4, 4, 16, 16, combinedLightIn);
		RenderSystem.disableDepthTest();
		matrixStack.popPose();
	}
}
