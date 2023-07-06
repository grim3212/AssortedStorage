package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.ItemTowerContainer;
import com.grim3212.assorted.storage.common.inventory.ItemTowerInventory;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;


public class ItemTowerScreen extends AbstractContainerScreen<ItemTowerContainer> implements MenuAccess<ItemTowerContainer> {

    private static final ResourceLocation ITEM_TOWER_TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/item_tower.png");
    private int rowId = 0;
    private IItemStorageHandler towerInventory;

    public ItemTowerScreen(ItemTowerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        this.towerInventory = this.menu.getItemTowerInventory();

        this.imageWidth = 176;
        this.imageHeight = 150;
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
        MutableComponent title = Component.literal(this.title.getString());
        if (this.towerInventory.getSlots() > 18) {
            title.append(Component.translatable(Constants.MOD_ID + ".container.item_tower.row", this.rowId + 1));
            title.append(" " + this.towerInventory.getSlots() / 9);
        }

        guiGraphics.drawString(this.font, title, 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ITEM_TOWER_TEXTURE);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(ITEM_TOWER_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

        if (this.towerInventory != null && this.towerInventory.getSlots() > 18) {
            RenderSystem.enableBlend();
            guiGraphics.blit(ITEM_TOWER_TEXTURE, i + this.imageWidth - 3, j, this.imageWidth, 0, 20, 57);
            RenderSystem.disableBlend();
        }

    }

    public void scrollInventory(boolean directionDown, boolean playSound) {
        if (this.towerInventory != null && this.towerInventory.getSlots() > 18) {
            int prevRowID = this.rowId;
            if (directionDown) {
                if (this.rowId < this.towerInventory.getSlots() / 9 - 1)
                    this.rowId += 1;
                else {
                    this.rowId = 0;
                }

                if (this.towerInventory instanceof ItemTowerInventory) {
                    ((ItemTowerInventory) this.towerInventory).setAnimation(1);
                }

            } else {
                if (this.rowId > 0)
                    this.rowId -= 1;
                else {
                    this.rowId = (this.towerInventory.getSlots() / 9 - 1);
                }

                if (this.towerInventory instanceof ItemTowerInventory) {
                    ((ItemTowerInventory) this.towerInventory).setAnimation(-1);
                }
            }

            if (prevRowID != this.rowId) {
                this.menu.setDisplayRow(this.rowId);
                if (playSound)
                    this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
        double modx = mouseX - (this.width - this.imageWidth) / 2;
        double mody = mouseY - (this.height - this.imageHeight) / 2;

        if (modx >= 173 && modx < 186 && mody >= 22 && mody < 35)
            scrollInventory(false, true);

        if (modx >= 173 && modx < 186 && mody >= 35 && mody < 48)
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
