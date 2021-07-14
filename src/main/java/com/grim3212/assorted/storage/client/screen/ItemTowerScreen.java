package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.ItemTowerContainer;
import com.grim3212.assorted.storage.common.inventory.ItemTowerInventory;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemTowerScreen extends ContainerScreen<ItemTowerContainer> implements IHasContainer<ItemTowerContainer> {

	private static final ResourceLocation ITEM_TOWER_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/gui/container/item_tower.png");
	private int rowId = 0;
	private IInventory towerInventory;

	public ItemTowerScreen(ItemTowerContainer container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);

		this.towerInventory = this.menu.getItemTowerInventory();

		this.imageWidth = 176;
		this.imageHeight = 150;
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
		StringTextComponent title = new StringTextComponent(this.title.getString());
		if (this.inventory.getContainerSize() > 18) {
			title.append(new TranslationTextComponent(AssortedStorage.MODID + ".container.item_tower.row", this.rowId + 1));
			title.append(" " + this.inventory.getContainerSize() / 9);
		}

		this.font.draw(matrixStack, title, 8.0F, 6.0F, 4210752);

		this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);

	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(ITEM_TOWER_TEXTURE);

		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

		if (this.inventory != null && this.inventory.getContainerSize() > 18) {
			RenderSystem.enableBlend();
			this.blit(matrixStack, i + this.imageWidth - 3, j, this.imageWidth, 0, 20, 57);
			RenderSystem.disableBlend();
		}

	}

	public void scrollInventory(boolean directionDown, boolean playSound) {
		if (this.inventory != null && this.inventory.getContainerSize() > 18) {
			int prevRowID = this.rowId;
			if (directionDown) {
				if (this.rowId < this.inventory.getContainerSize() / 9 - 1)
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
					this.rowId = (this.inventory.getContainerSize() / 9 - 1);
				}

				if (this.towerInventory instanceof ItemTowerInventory) {
					((ItemTowerInventory) this.towerInventory).setAnimation(-1);
				}
			}

			if (prevRowID != this.rowId) {
				this.menu.setDisplayRow(this.rowId);
				if (playSound)
					this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
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
