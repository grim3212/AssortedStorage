package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.inventory.LockedMaterialContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/**
 * See {@link BaseStorageScreen} for the retained-mode GUI change.
 */
public class LockedMaterialScreen extends AbstractContainerScreen<LockedMaterialContainer> {

    // TODO: 9 box eventually
    private static final Identifier CHEST_GUI_TEXTURE_9_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x9.png");
    private static final Identifier CHEST_GUI_TEXTURE_10_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x10.png");
    private static final Identifier CHEST_GUI_TEXTURE_11_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x11.png");
    private static final Identifier CHEST_GUI_TEXTURE_12_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x12.png");
    private static final Identifier CHEST_GUI_TEXTURE_13_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x13.png");
    private static final Identifier CHEST_GUI_TEXTURE_14_COLS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/generic_9x14.png");

    private static final int START_OF_PLAYER_INVENTORY_Y = 180;
    private static final int HEIGHT_OF_PLAYER_INVENTORY = 96;

    private final int xRows;
    private final int textureXSize;
    private final int textureYSize;
    private final Identifier inventoryTexture;

    public LockedMaterialScreen(LockedMaterialContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, imageWidth(container.getStorageMaterial()), imageHeight(container.getStorageMaterial()));

        StorageMaterial storageMaterial = container.getStorageMaterial();
        this.xRows = storageMaterial == null ? 3 : storageMaterial.getXRows();

        int yCols = storageMaterial == null ? 9 : storageMaterial.getYCols();
        switch (yCols) {
            case (10):
                this.textureXSize = 256;
                this.textureYSize = 276;
                this.inventoryTexture = CHEST_GUI_TEXTURE_10_COLS;
                break;
            case (11):
                this.textureXSize = 256;
                this.textureYSize = 276;
                this.inventoryTexture = CHEST_GUI_TEXTURE_11_COLS;
                break;
            case (12):
                this.textureXSize = 256;
                this.textureYSize = 276;
                this.inventoryTexture = CHEST_GUI_TEXTURE_12_COLS;
                break;
            case (13):
                this.textureXSize = 276;
                this.textureYSize = 276;
                this.inventoryTexture = CHEST_GUI_TEXTURE_13_COLS;
                break;
            case (14):
                this.textureXSize = 296;
                this.textureYSize = 276;
                this.inventoryTexture = CHEST_GUI_TEXTURE_14_COLS;
                break;
            default:
                this.textureXSize = 256;
                this.textureYSize = 276;
                this.inventoryTexture = CHEST_GUI_TEXTURE_9_COLS;
                break;
        }
    }

    // imageWidth / imageHeight are final on AbstractContainerScreen now, so the sizes the constructor
    // used to assign have to be worked out before the super call.
    private static int imageWidth(StorageMaterial storageMaterial) {
        return 14 + (storageMaterial == null ? 9 : storageMaterial.getYCols()) * 18;
    }

    private static int imageHeight(StorageMaterial storageMaterial) {
        return 114 + (storageMaterial == null ? 3 : storageMaterial.getXRows()) * 18;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, this.inventoryTexture, i, j, 0.0F, 0.0F, this.imageWidth, this.xRows * 18 + 17, this.textureXSize, this.textureYSize);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.inventoryTexture, i, j + this.xRows * 18 + 17, 0.0F, START_OF_PLAYER_INVENTORY_Y, this.imageWidth, HEIGHT_OF_PLAYER_INVENTORY, this.textureXSize, this.textureYSize);
    }
}
