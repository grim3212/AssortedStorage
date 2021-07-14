package com.grim3212.assorted.storage.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BaseStorageScreen<T extends Container> extends ContainerScreen<T> implements IHasContainer<T> {

	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	protected final int inventoryRows;

	public BaseStorageScreen(T container, PlayerInventory playerInventory, ITextComponent title) {
		this(container, playerInventory, title, 3);
	}

	public BaseStorageScreen(T container, PlayerInventory playerInventory, ITextComponent title, int rows) {
		super(container, playerInventory, title);

		this.inventoryRows = rows;
		this.imageHeight = 114 + this.inventoryRows * 18;
		this.inventoryLabelY = this.imageHeight - 94;

		this.passEvents = false;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
		this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);

		this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(CHEST_GUI_TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
		this.blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);
	}
}
