package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.inventory.LockedHopperContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class LockedHopperScreen extends AbstractContainerScreen<LockedHopperContainer> implements MenuAccess<LockedHopperContainer> {

    private static final ResourceLocation CHEST_GUI_TEXTURE_4_COLS = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/generic_9x4.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE_5_COLS = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/generic_9x5.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE_6_COLS = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/generic_9x6.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE_7_COLS = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/generic_9x7.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE_8_COLS = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/generic_9x8.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE_9_COLS = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/generic_9x9.png");
    private final StorageMaterial storageMaterial;

    private final int textureXSize;
    private final int textureYSize;
    private final ResourceLocation inventoryTexture;
    private final int startOfPlayerInventoryY = 180;
    private final int heightOfPlayerInvetory = 96;

    public LockedHopperScreen(LockedHopperContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        this.storageMaterial = container.getStorageMaterial();

        int xRows = 1;
        int yCols = 5;

        if (storageMaterial != null) {
            xRows = storageMaterial.hopperXRows();
            yCols = storageMaterial.hopperYCols();
        }

        this.imageHeight = 114 + xRows * 18;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;

        this.textureXSize = 256;
        this.textureYSize = 276;

        switch (yCols) {
            case (4):
                inventoryTexture = CHEST_GUI_TEXTURE_4_COLS;
                break;
            case (5):
                inventoryTexture = CHEST_GUI_TEXTURE_5_COLS;
                break;
            case (6):
                inventoryTexture = CHEST_GUI_TEXTURE_6_COLS;
                break;
            case (7):
                inventoryTexture = CHEST_GUI_TEXTURE_7_COLS;
                break;
            case (8):
                inventoryTexture = CHEST_GUI_TEXTURE_8_COLS;
                break;
            default:
                inventoryTexture = CHEST_GUI_TEXTURE_9_COLS;
                break;
        }

        this.passEvents = false;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
        this.font.draw(matrixStack, this.playerInventoryTitle, 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.inventoryTexture);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        int xRows = storageMaterial == null ? 1 : storageMaterial.hopperXRows();

        blit(matrixStack, i, j, 0, 0, this.imageWidth, xRows * 18 + 17, this.textureXSize, this.textureYSize);
        blit(matrixStack, i, j + xRows * 18 + 17, 0, startOfPlayerInventoryY, this.imageWidth, heightOfPlayerInvetory, this.textureXSize, this.textureYSize);
    }
}