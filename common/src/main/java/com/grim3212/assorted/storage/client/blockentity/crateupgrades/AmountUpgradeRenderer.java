package com.grim3212.assorted.storage.client.blockentity.crateupgrades;

import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.crates.ICrateUpgradeRenderer;
import com.grim3212.assorted.storage.client.blockentity.CrateBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.state.CrateRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class AmountUpgradeRenderer implements ICrateUpgradeRenderer {

    public static final AmountUpgradeRenderer INSTANCE = new AmountUpgradeRenderer();

    private static String getSlotAmount(CrateRenderState state, int slot, boolean showFull) {
        int amount = state.slotAmounts[slot];
        if (amount <= 0) {
            return "";
        }

        return showFull ? amount + "/" + state.slotCapacities[slot] : String.valueOf(amount);
    }


    private int getTextOffset(Font font, String text, float scale) {
        int scaledWidth = (int) Math.floor(1 / scale);
        int offset = (scaledWidth - font.width(text)) / 2;
        return offset - scaledWidth / 2;
    }

    @Override
    public void submit(CrateRenderState state, ItemStack upgrade, PoseStack poseStack, SubmitNodeCollector collector) {
        Font font = Minecraft.getInstance().font;

        boolean showFull = NBTHelper.getInt(upgrade, "Mode") > 0;

        poseStack.pushPose();
        CrateBlockEntityRenderer.faceCrate(state.facing, poseStack);
        poseStack.translate(-0.5D, 0.5D, 0.45D);
        float scale = 0.012F * 0.6666667F;
        poseStack.scale(scale, -scale, scale);

        int light = state.itemLightCoords;

        switch (state.layout) {
            case SINGLE:
                submitAmount(collector, poseStack, font, getSlotAmount(state, 0, showFull), 63F, 90.0F, scale, light);
                break;
            case DOUBLE:
                submitAmount(collector, poseStack, font, getSlotAmount(state, 0, showFull), 63F, 45.25F, scale, light);
                submitAmount(collector, poseStack, font, getSlotAmount(state, 1, showFull), 63F, 100.0F, scale, light);
                break;
            case TRIPLE:
                submitAmount(collector, poseStack, font, getSlotAmount(state, 0, showFull), 63F, 45.25F, scale, light);
                submitAmount(collector, poseStack, font, getSlotAmount(state, 1, showFull), 35.5F, 100.0F, scale, light);
                submitAmount(collector, poseStack, font, getSlotAmount(state, 2, showFull), 90F, 100.0F, scale, light);
                break;
            case QUADRUPLE:
                submitAmount(collector, poseStack, font, getSlotAmount(state, 0, showFull), 35.5F, 45.25F, scale, light);
                submitAmount(collector, poseStack, font, getSlotAmount(state, 1, showFull), 90F, 45.25F, scale, light);
                submitAmount(collector, poseStack, font, getSlotAmount(state, 2, showFull), 35.5F, 100.0F, scale, light);
                submitAmount(collector, poseStack, font, getSlotAmount(state, 3, showFull), 90F, 100.0F, scale, light);
                break;
        }

        poseStack.popPose();
    }

    private void submitAmount(SubmitNodeCollector collector, PoseStack poseStack, Font font, String amount, float x, float y, float scale, int light) {
        if (amount.isEmpty()) {
            return;
        }

        collector.submitText(poseStack, getTextOffset(font, amount, scale) + x, y, FormattedCharSequence.forward(amount, Style.EMPTY), false, Font.DisplayMode.NORMAL, light, ARGB.opaque(DyeColor.WHITE.getTextColor()), 0, 0);
    }
}
