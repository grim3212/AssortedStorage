package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.ItemTowerContainer;
import com.grim3212.assorted.storage.common.inventory.ItemTowerInventory;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemTowerScreen extends AbstractContainerScreen<ItemTowerContainer> implements MenuAccess<ItemTowerContainer> {

	private static final ResourceLocation ITEM_TOWER_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/gui/container/item_tower.png");
	private int rowId = 0;
	private Container towerInventory;

	public ItemTowerScreen(ItemTowerContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);

		this.towerInventory = this.menu.getItemTowerInventory();

		this.imageWidth = 176;
		this.imageHeight = 150;
		this.inventoryLabelY = this.imageHeight - 94;

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
		MutableComponent title = Component.literal(this.title.getString());
		if (this.towerInventory.getContainerSize() > 18) {
			title.append(Component.translatable(AssortedStorage.MODID + ".container.item_tower.row", this.rowId + 1));
			title.append(" " + this.towerInventory.getContainerSize() / 9);
		}

		this.font.draw(matrixStack, title, 8.0F, 6.0F, 4210752);

		this.font.draw(matrixStack, this.playerInventoryTitle, 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);

	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, ITEM_TOWER_TEXTURE);

		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

		if (this.towerInventory != null && this.towerInventory.getContainerSize() > 18) {
			RenderSystem.enableBlend();
			this.blit(matrixStack, i + this.imageWidth - 3, j, this.imageWidth, 0, 20, 57);
			RenderSystem.disableBlend();
		}

	}

	public void scrollInventory(boolean directionDown, boolean playSound) {
		if (this.towerInventory != null && this.towerInventory.getContainerSize() > 18) {
			int prevRowID = this.rowId;
			if (directionDown) {
				if (this.rowId < this.towerInventory.getContainerSize() / 9 - 1)
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
					this.rowId = (this.towerInventory.getContainerSize() / 9 - 1);
				}

				if (this.towerInventory instanceof ItemTowerInventory) {
					((ItemTowerInventory) this.towerInventory).setAnimation(-1);
				}
			}

			if (prevRowID != this.rowId) {
				this.menu.setDisplayRow(this.rowId);
				if (playSound)
					this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.0F);
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
