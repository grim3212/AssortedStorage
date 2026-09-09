package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * See {@link BaseStorageScreen} for the retained-mode GUI change.
 */
public class KeyRingScreen extends AbstractContainerScreen<KeyRingContainer> {

    private static final Identifier KEY_RING_GUI_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/key_ring.png");

    public KeyRingScreen(KeyRingContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, DEFAULT_IMAGE_WIDTH, 168);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(RenderPipelines.GUI_TEXTURED, KEY_RING_GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, 176, 256, 256);
    }
}
