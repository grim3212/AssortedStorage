package com.grim3212.assorted.storage.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * The GUI is retained-mode in 26.x: a screen records elements into a {@link GuiGraphicsExtractor} that
 * {@code GuiRenderer} plays back later, so {@code renderBg} became {@code extractBackground} and the
 * {@code render} override that sequenced background/contents/tooltip by hand is gone - the base screen
 * does that itself. {@code imageWidth} / {@code imageHeight} are final now and have to be passed to
 * the constructor, and the label drawing this class used to override is exactly what
 * {@code AbstractContainerScreen#extractLabels} already does with the default label positions.
 */
public abstract class BaseStorageScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private static final Identifier CHEST_GUI_TEXTURE = Identifier.parse("textures/gui/container/generic_54.png");
    protected final int inventoryRows;

    public BaseStorageScreen(T container, Inventory playerInventory, Component title) {
        this(container, playerInventory, title, 3);
    }

    public BaseStorageScreen(T container, Inventory playerInventory, Component title, int rows) {
        super(container, playerInventory, title, DEFAULT_IMAGE_WIDTH, 114 + rows * 18);

        this.inventoryRows = rows;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_GUI_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.inventoryRows * 18 + 17, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_GUI_TEXTURE, i, j + this.inventoryRows * 18 + 17, 0.0F, 126.0F, this.imageWidth, 96, 256, 256);
    }
}
