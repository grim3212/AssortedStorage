package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;


public class DualLockerScreen extends AbstractContainerScreen<LockerContainer> implements MenuAccess<LockerContainer> {

    private static final ResourceLocation LOCKER_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/locker.png");
    private int rowId = 0;

    public DualLockerScreen(LockerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        this.imageWidth += 17;
        this.imageHeight = 204;
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
        RenderSystem.setShaderTexture(0, LOCKER_TEXTURE);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(LOCKER_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.enableBlend();
        guiGraphics.blit(LOCKER_TEXTURE, i + (this.imageWidth - 18), j + 20 + this.rowId, this.imageWidth, 0, 10, 5);
        RenderSystem.disableBlend();

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
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
        double modx = mouseX - (this.width - this.imageWidth) / 2;
        double mody = mouseY - (this.height - this.imageHeight) / 2;

        if (modx >= 173 && modx < 186 && mody >= 7 && mody < 20)
            scrollInventory(false, true);

        if (modx >= 173 && modx < 186 && mody >= 30 && mody < 43)
            scrollInventory(true, true);

        return super.mouseClicked(mouseX, mouseY, p_231044_5_);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll >= 1.0f) {
            scrollInventory(false, true);
        } else {
            scrollInventory(true, true);
        }

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }
}
