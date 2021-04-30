package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.LockerContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DualLockerScreen extends ContainerScreen<LockerContainer> implements IHasContainer<LockerContainer> {

	private static final ResourceLocation LOCKER_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/gui/container/locker.png");
	private int rowId = 0;

	public DualLockerScreen(LockerContainer container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);

		this.xSize += 17;
		this.ySize = 204;
		this.playerInventoryTitleY = this.ySize - 94;

		this.passEvents = false;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		this.font.drawText(matrixStack, this.title, 8.0F, 6.0F, 4210752);

		this.font.drawText(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(LOCKER_TEXTURE);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;

		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);

		RenderSystem.enableBlend();
		this.blit(matrixStack, i + (this.xSize - 18), j + 20 + this.rowId, this.xSize, 0, 10, 5);
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
			this.container.setDisplayRow(this.rowId);
			if (playSound)
				this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
		double modx = mouseX - (this.width - this.xSize) / 2;
		double mody = mouseY - (this.height - this.ySize) / 2;

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
