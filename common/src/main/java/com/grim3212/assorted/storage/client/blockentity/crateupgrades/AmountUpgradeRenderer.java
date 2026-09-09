package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class AmountUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final AmountUpgradeRenderer INSTANCE = new AmountUpgradeRenderer();

    private String getSlotAmount(CrateBlockEntity tileEntityIn, int slotId, boolean showFull) {
        int amount = tileEntityIn.getItemStackStorageHandler().getLargeItemStack(slotId).getAmount();
        if (showFull) {
            int max = tileEntityIn.getItemStackStorageHandler().getMaxStackSizeForSlot(slotId);
            return amount > 0 ? amount + "/" + max : "";
        }

        return amount > 0 ? String.valueOf(amount) : "";
    }


    private int getTextOffset(Font font, String text, float scale) {
        int scaledWidth = (int) Math.floor(1 / scale);
        int offset = (scaledWidth - font.width(text)) / 2;
        return offset - scaledWidth / 2;
    }

    @Override
    public void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStack, SubmitNodeCollector collectorIn, int combinedLightIn, int combinedOverlayIn) {
        Direction facing = tileEntityIn.getBlockState().getValue(CrateBlock.FACING);
        Font font = Minecraft.getInstance().font;

        boolean showFull = NBTHelper.getInt(selfStack, "Mode") > 0;

        matrixStack.pushPose();
        CrateBlockEntityRenderer.faceCrate(facing, matrixStack);
        matrixStack.translate(-0.5D, 0.5D, 0.45D);
        float scale = 0.012F * 0.6666667F;
        matrixStack.scale(scale, -scale, scale);

        int light = tileEntityIn.getItemStackStorageHandler().hasGlowUpgrade() ? LightCoordsUtil.FULL_BRIGHT : combinedLightIn;

        switch (tileEntityIn.getLayout()) {
            case SINGLE:
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 0, showFull), 63F, 90.0F, scale, light);
                break;
            case DOUBLE:
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 0, showFull), 63F, 45.25F, scale, light);
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 1, showFull), 63F, 100.0F, scale, light);
                break;
            case TRIPLE:
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 0, showFull), 63F, 45.25F, scale, light);
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 1, showFull), 35.5F, 100.0F, scale, light);
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 2, showFull), 90F, 100.0F, scale, light);
                break;
            case QUADRUPLE:
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 0, showFull), 35.5F, 45.25F, scale, light);
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 1, showFull), 90F, 45.25F, scale, light);
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 2, showFull), 35.5F, 100.0F, scale, light);
                submitAmount(collectorIn, matrixStack, font, getSlotAmount(tileEntityIn, 3, showFull), 90F, 100.0F, scale, light);
                break;
        }

        matrixStack.popPose();
    }

    /**
     * {@code Font#drawInBatch} is gone along with {@code MultiBufferSource}: in-world text is
     * submitted to the collector, which lays it out with the game's own font renderer at draw time.
     */
    private void submitAmount(SubmitNodeCollector collectorIn, PoseStack matrixStack, Font font, String amount, float x, float y, float scale, int light) {
        if (amount.isEmpty()) {
            return;
        }

        collectorIn.submitText(matrixStack, getTextOffset(font, amount, scale) + x, y, FormattedCharSequence.forward(amount, Style.EMPTY), false, Font.DisplayMode.NORMAL, light, ARGB.opaque(DyeColor.WHITE.getTextColor()), 0, 0);
    }
}
