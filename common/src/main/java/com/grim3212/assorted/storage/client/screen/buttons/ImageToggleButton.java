package com.grim3212.assorted.storage.client.screen.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageToggleButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    private boolean buttonClicked = false;

    public ImageToggleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation location, int textureWidth, int textureHeight, OnPress onPress, boolean clicked, Component tooltip) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, location, textureWidth, textureHeight, onPress, clicked, CommonComponents.EMPTY, tooltip);
    }

    public ImageToggleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, ResourceLocation location, int textureWidth, int textureHeight, Button.OnPress onPress, boolean clicked, Component message, Component tooltip) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffTex = yDiffTex;
        this.resourceLocation = location;
        this.buttonClicked = clicked;

        this.setTooltip(Tooltip.create(tooltip));
    }

    public void toggleButtonClicked() {
        this.buttonClicked = !this.buttonClicked;
    }

    public void setButtonClicked(boolean buttonClicked) {
        this.buttonClicked = buttonClicked;
    }

    public boolean isButtonClicked() {
        return buttonClicked;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.toggleButtonClicked();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int i = this.yTexStart;
        if (!this.isActive()) {
            i += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused() || this.isButtonClicked()) {
            i += this.yDiffTex;
        }

        RenderSystem.enableDepthTest();
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.translate(0.0F, 0.0F, 50.0F);
        guiGraphics.blit(this.resourceLocation, this.getX(), this.getY(), (float) this.xTexStart, (float) i, this.width, this.height, this.textureWidth, this.textureHeight);
        stack.popPose();
    }
}
