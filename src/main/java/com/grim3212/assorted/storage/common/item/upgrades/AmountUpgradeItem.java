package com.grim3212.assorted.storage.common.item.upgrades;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.util.NBTHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT, _interface = ICrateUpgradeRenderer.class)
public class AmountUpgradeItem extends ModeCrateUpgradeItem implements ICrateUpgradeRenderer {

	public AmountUpgradeItem(Properties props) {
		super(props);
	}

	@Override
	protected void cycleMode(ItemStack stack) {
		int currentMode = NBTHelper.getInt(stack, "Mode", 0);
		NBTHelper.putInt(stack, "Mode", currentMode == 0 ? 1 : 0);
	}

	@Override
	protected Component modeDisplay(ItemStack stack) {
		int currentMode = NBTHelper.getInt(stack, "Mode");
		return Component.translatable(AssortedStorage.MODID + ".info.upgrade.mode", Component.translatable(AssortedStorage.MODID + (currentMode == 0 ? ".info.upgrade_amount.mode.simple" : ".info.upgrade_amount.mode.full")).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.AQUA);
	}

	@Override
	protected int startingMode() {
		return 0;
	}

	private String getSlotAmount(CrateBlockEntity tileEntityIn, int slotId, boolean showFull) {
		int amount = tileEntityIn.getLargeItemStack(slotId).getAmount();
		if (showFull) {
			int max = tileEntityIn.getMaxStackSizeForSlot(slotId);
			return amount > 0 ? amount + "/" + max : "";
		}

		return amount > 0 ? String.valueOf(amount) : "";
	}

	@OnlyIn(Dist.CLIENT)
	private int getTextOffset(Font font, String text, float scale) {
		int scaledWidth = (int) Math.floor(1 / scale);
		int offset = (scaledWidth - font.width(text)) / 2;
		return offset - scaledWidth / 2;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Direction facing = tileEntityIn.getBlockState().getValue(CrateBlock.FACING);
		Font font = Minecraft.getInstance().font;

		boolean showFull = NBTHelper.getInt(selfStack, "Mode") > 0;

		String slotAmount = getSlotAmount(tileEntityIn, 0, showFull);
		String slotAmount1 = "";
		String slotAmount2 = "";
		String slotAmount3 = "";

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

		int light = tileEntityIn.hasGlowUpgrade() ? LightTexture.FULL_BRIGHT : combinedLightIn;

		switch (tileEntityIn.getLayout()) {
			case SINGLE:
				font.drawInBatch(slotAmount, getTextOffset(font, slotAmount, scale) + 63F, 90.0F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				break;
			case DOUBLE:
				slotAmount1 = getSlotAmount(tileEntityIn, 1, showFull);
				font.drawInBatch(slotAmount, getTextOffset(font, slotAmount, scale) + 63F, 45.25F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				font.drawInBatch(slotAmount1, getTextOffset(font, slotAmount1, scale) + 63F, 100.0F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				break;
			case TRIPLE:
				slotAmount1 = getSlotAmount(tileEntityIn, 1, showFull);
				slotAmount2 = getSlotAmount(tileEntityIn, 2, showFull);
				font.drawInBatch(slotAmount, getTextOffset(font, slotAmount, scale) + 63F, 45.25F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				font.drawInBatch(slotAmount1, getTextOffset(font, slotAmount1, scale) + 35.5F, 100.0F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				font.drawInBatch(slotAmount2, getTextOffset(font, slotAmount2, scale) + 90F, 100.0F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				break;
			case QUADRUPLE:
				slotAmount1 = getSlotAmount(tileEntityIn, 1, showFull);
				slotAmount2 = getSlotAmount(tileEntityIn, 2, showFull);
				slotAmount3 = getSlotAmount(tileEntityIn, 3, showFull);
				font.drawInBatch(slotAmount, getTextOffset(font, slotAmount, scale) + 35.5F, 45.25F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				font.drawInBatch(slotAmount1, getTextOffset(font, slotAmount1, scale) + 90F, 45.25F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				font.drawInBatch(slotAmount2, getTextOffset(font, slotAmount2, scale) + 35.5F, 100.0F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				font.drawInBatch(slotAmount3, getTextOffset(font, slotAmount3, scale) + 90F, 100.0F, DyeColor.WHITE.getTextColor(), false, matrixStack.last().pose(), bufferIn, false, 0, light);
				break;
		}

		matrixStack.popPose();
	}
}
