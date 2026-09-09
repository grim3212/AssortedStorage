package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.ItemTowerContainer;
import com.grim3212.assorted.storage.common.inventory.ItemTowerInventory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/**
 * See {@link BaseStorageScreen} for the retained-mode GUI change; {@code renderLabels} is
 * {@code extractLabels} and the label colour is a full ARGB int now, so the old {@code 0x404040}
 * would draw fully transparent.
 */
public class ItemTowerScreen extends AbstractContainerScreen<ItemTowerContainer> {

    private static final Identifier ITEM_TOWER_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/item_tower.png");
    private static final int LABEL_COLOR = -12566464;

    private int rowId = 0;
    private final IItemStorageHandler towerInventory;

    public ItemTowerScreen(ItemTowerContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, DEFAULT_IMAGE_WIDTH, 150);

        this.towerInventory = this.menu.getItemTowerInventory();
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        MutableComponent title = Component.literal(this.title.getString());
        if (this.towerInventory.getSlots() > 18) {
            title.append(Component.translatable(Constants.MOD_ID + ".container.item_tower.row", this.rowId + 1));
            title.append(" " + this.towerInventory.getSlots() / 9);
        }

        graphics.text(this.font, title, this.titleLabelX, this.titleLabelY, LABEL_COLOR, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL_COLOR, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, ITEM_TOWER_TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (this.towerInventory != null && this.towerInventory.getSlots() > 18) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, ITEM_TOWER_TEXTURE, i + this.imageWidth - 3, j, this.imageWidth, 0.0F, 20, 57, 256, 256);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double modx = event.x() - (this.width - this.imageWidth) / 2;
        double mody = event.y() - (this.height - this.imageHeight) / 2;

        if (modx >= 173 && modx < 186 && mody >= 22 && mody < 35)
            scrollInventory(false, true);

        if (modx >= 173 && modx < 186 && mody >= 35 && mody < 48)
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
