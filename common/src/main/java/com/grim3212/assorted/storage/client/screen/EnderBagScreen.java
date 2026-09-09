package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * See {@link BaseStorageScreen} for the retained-mode GUI change.
 */
public class EnderBagScreen extends AbstractContainerScreen<EnderBagContainer> {

    private static final Identifier CHEST_GUI_TEXTURE_9_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x9.png");

    private static final int X_ROWS = 3;
    private static final int Y_COLS = 9;
    private static final int TEXTURE_X_SIZE = 256;
    private static final int TEXTURE_Y_SIZE = 276;
    private static final int START_OF_PLAYER_INVENTORY_Y = 180;
    private static final int HEIGHT_OF_PLAYER_INVENTORY = 96;

    public EnderBagScreen(EnderBagContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 26 + 14 + Y_COLS * 18, 114 + X_ROWS * 18);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_GUI_TEXTURE_9_COLS, i, j, 0.0F, 0.0F, this.imageWidth, X_ROWS * 18 + 17, TEXTURE_X_SIZE, TEXTURE_Y_SIZE);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CHEST_GUI_TEXTURE_9_COLS, i, j + X_ROWS * 18 + 17, 0.0F, START_OF_PLAYER_INVENTORY_Y, this.imageWidth, HEIGHT_OF_PLAYER_INVENTORY, TEXTURE_X_SIZE, TEXTURE_Y_SIZE);
    }
}
