package com.grim3212.assorted.storage.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;


public abstract class BaseStorageScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> implements MenuAccess<T> {

    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    protected final int inventoryRows;

    public BaseStorageScreen(T container, Inventory playerInventory, Component title) {
        this(container, playerInventory, title, 3);
    }

    public BaseStorageScreen(T container, Inventory playerInventory, Component title, int rows) {
        super(container, playerInventory, title);

        this.inventoryRows = rows;
        this.imageHeight = 114 + this.inventoryRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CHEST_GUI_TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CHEST_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        guiGraphics.blit(CHEST_GUI_TEXTURE, i, j + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
