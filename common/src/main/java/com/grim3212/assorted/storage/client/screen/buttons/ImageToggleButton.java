package com.grim3212.assorted.storage.client.screen.buttons;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * A button drawn as a checkbox that stays ticked while it is on. The widget textures are sprites in
 * 26.2 - the {@code textures/gui/checkbox.png} sheet this used to slice up is gone - so the four
 * states are four sprite ids rather than offsets into one image.
 */
public class ImageToggleButton extends Button {

    public static final Identifier CHECKBOX = Identifier.withDefaultNamespace("widget/checkbox");
    public static final Identifier CHECKBOX_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/checkbox_highlighted");
    public static final Identifier CHECKBOX_SELECTED = Identifier.withDefaultNamespace("widget/checkbox_selected");
    public static final Identifier CHECKBOX_SELECTED_HIGHLIGHTED = Identifier.withDefaultNamespace("widget/checkbox_selected_highlighted");

    /** Every sprite this button can draw, for the client gametest that checks they are all in the atlas. */
    public static final List<Identifier> SPRITES = List.of(CHECKBOX, CHECKBOX_HIGHLIGHTED, CHECKBOX_SELECTED, CHECKBOX_SELECTED_HIGHLIGHTED);

    private boolean buttonClicked;

    public ImageToggleButton(int x, int y, int width, int height, OnPress onPress, boolean clicked, Component tooltip) {
        this(x, y, width, height, onPress, clicked, CommonComponents.EMPTY, tooltip);
    }

    public ImageToggleButton(int x, int y, int width, int height, Button.OnPress onPress, boolean clicked, Component message, Component tooltip) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
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
        boolean highlighted = this.isActive() && this.isHoveredOrFocused();
        Identifier sprite;
        if (this.isButtonClicked()) {
            sprite = highlighted ? CHECKBOX_SELECTED_HIGHLIGHTED : CHECKBOX_SELECTED;
        } else {
            sprite = highlighted ? CHECKBOX_HIGHLIGHTED : CHECKBOX;
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), this.width, this.height);
    }
}
