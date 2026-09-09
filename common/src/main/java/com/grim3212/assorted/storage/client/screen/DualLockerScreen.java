package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/**
 * See {@link BaseStorageScreen} for the retained-mode GUI change. The mouse callbacks changed shape
 * too: a click arrives as a {@code MouseButtonEvent} record and scrolling carries both axes.
 */
public class DualLockerScreen extends AbstractContainerScreen<LockerContainer> {

    private static final Identifier LOCKER_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/locker.png");
    private int rowId = 0;

    public DualLockerScreen(LockerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, DEFAULT_IMAGE_WIDTH + 17, 204);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCKER_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, LOCKER_TEXTURE, i + (this.imageWidth - 18), j + 20 + this.rowId, this.imageWidth, 0.0F, 10, 5, 256, 256);
    }

    public void scrollInventory(boolean directionDown, boolean playSound) {
        int prevRowID = this.rowId;

        if (directionDown) {
            if (this.rowId < 5)
                this.rowId += 1;
        } else if (this.rowId > 0)
            this.rowId -= 1;

        if (prevRowID != this.rowId) {
            this.menu.setDisplayRow(this.rowId);
            if (playSound)
                this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double modx = event.x() - (this.width - this.imageWidth) / 2;
        double mody = event.y() - (this.height - this.imageHeight) / 2;

        if (modx >= 173 && modx < 186 && mody >= 7 && mody < 20)
            scrollInventory(false, true);

        if (modx >= 173 && modx < 186 && mody >= 30 && mody < 43)
            scrollInventory(true, true);

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (scrollY >= 1.0D) {
            scrollInventory(false, true);
        } else {
            scrollInventory(true, true);
        }

        return super.mouseScrolled(x, y, scrollX, scrollY);
    }
}
