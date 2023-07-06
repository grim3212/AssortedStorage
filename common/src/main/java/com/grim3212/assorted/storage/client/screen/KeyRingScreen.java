package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KeyRingScreen extends AbstractContainerScreen<KeyRingContainer> implements MenuAccess<KeyRingContainer> {

    private static final ResourceLocation KEY_RING_GUI_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/key_ring.png");

    public KeyRingScreen(KeyRingContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 168;
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
        RenderSystem.setShaderTexture(0, KEY_RING_GUI_TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(KEY_RING_GUI_TEXTURE, i, j, 0, 0, this.imageWidth, 176);
    }
}
