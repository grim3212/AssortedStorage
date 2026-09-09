package com.grim3212.assorted.storage.client.screen.buttons;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * {@code Button} is abstract in 26.2 and widgets went retained-mode with the rest of the GUI:
 * {@code renderWidget} is {@code extractContents}, drawing is a {@code blit} on a
 * {@link GuiGraphicsExtractor} with an explicit {@code RenderPipeline} instead of a
 * {@code RenderSystem.setShader}/{@code setShaderTexture} pair, and {@code onClick} takes a
 * {@code MouseButtonEvent}. Depth ordering is no longer a pose translate either - the GUI renderer
 * orders elements by the stratum they were recorded in.
 */
public class ImageToggleButton extends Button {
    private final Identifier resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    private boolean buttonClicked;

    public ImageToggleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier location, int textureWidth, int textureHeight, OnPress onPress, boolean clicked, Component tooltip) {
        this(x, y, width, height, xTexStart, yTexStart, yDiffTex, location, textureWidth, textureHeight, onPress, clicked, CommonComponents.EMPTY, tooltip);
    }

    public ImageToggleButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffTex, Identifier location, int textureWidth, int textureHeight, Button.OnPress onPress, boolean clicked, Component message, Component tooltip) {
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
        return this.buttonClicked;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        super.onClick(event, doubleClick);
        this.toggleButtonClicked();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int i = this.yTexStart;
        if (!this.isActive()) {
            i += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused() || this.isButtonClicked()) {
            i += this.yDiffTex;
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.resourceLocation, this.getX(), this.getY(), (float) this.xTexStart, (float) i, this.width, this.height, this.textureWidth, this.textureHeight);
    }
}
