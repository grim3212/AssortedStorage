package com.grim3212.assorted.storage.client.screen;

import java.util.List;
import java.util.Optional;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.client.screen.buttons.ImageToggleButton;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.inventory.crates.LargeItemStackSlot;
import com.grim3212.assorted.storage.common.network.PacketHandler;
import com.grim3212.assorted.storage.common.network.SetSlotLockPacket;
import com.grim3212.assorted.storage.common.util.CrateLayout;
import com.grim3212.assorted.storage.common.util.LargeItemStack;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrateScreen extends AbstractContainerScreen<CrateContainer> implements MenuAccess<CrateContainer> {

	protected static final ResourceLocation CHECKBOX_LOCATION = new ResourceLocation("textures/gui/checkbox.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/gui/container/crate.png");
	private ItemStack renderStack;

	public CrateScreen(CrateContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);

		this.imageHeight = 188;
		this.imageWidth = 176;

		this.passEvents = true;
		this.renderStack = new ItemStack(container.getInventory().getBlockState().getBlock());
	}

	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			List<Component> tooltip = this.getTooltipFromItem(this.hoveredSlot.getItem());
			Optional<TooltipComponent> tooltipComponents = this.hoveredSlot.getItem().getTooltipImage();
			if (this.hoveredSlot instanceof LargeItemStackSlot slot) {
				int curSlot = slot.getContainerSlot();
				LargeItemStack stackInSlot = this.getStack(curSlot);
				int maxStackSize = this.menu.getInventory().getMaxStackSizeForSlot(curSlot);
				tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.amount", Component.literal(String.valueOf(stackInSlot.getAmount() + "/" + maxStackSize)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GOLD));
				tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.upgrade_redstone.mode.slot", Component.literal(String.valueOf(curSlot)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GOLD));
				this.renderTooltip(stack, tooltip, tooltipComponents, mouseX, mouseY);
				return;
			} else if (this.hoveredSlot.getItem().getItem()instanceof ICrateUpgrade upgrade) {
				if (upgrade.getStorageModifier() <= 0) {
					this.renderTooltip(stack, tooltip, tooltipComponents, mouseX, mouseY);
					return;
				}

				tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.storage_multiplier", Component.literal(String.valueOf(upgrade.getStorageModifier())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GOLD));
				this.renderTooltip(stack, tooltip, tooltipComponents, mouseX, mouseY);
				return;
			} else {
				this.renderTooltip(stack, tooltip, tooltipComponents, mouseX, mouseY);
			}
		}
	}

	protected LargeItemStack getStack(int slot) {
		return this.menu.getInventory().getLargeItemStack(slot);
	}

	private int getSlotAmount(int slotId) {
		return this.getStack(slotId).getAmount();
	}

	private void addSlotButton(int x, int y, int slot) {
		if (!this.getStack(slot).isEmpty()) {
			this.addRenderableWidget(this.createImageButton(x, y, slot));
		}
	}

	private ImageToggleButton createImageButton(int x, int y, int slot) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		return new ImageToggleButton(i + x, j + y, 10, 10, 0, 0, 10, CHECKBOX_LOCATION, 32, 32, (button) -> {
			CrateScreen.this.toggleSlotLock(slot);
		}, this.menu.getInventory().isSlotLocked(slot), Component.translatable(AssortedStorage.MODID + ".info.item_lock", Component.literal(String.valueOf(slot)).withStyle(ChatFormatting.AQUA)));
	}

	private void toggleSlotLock(int slot) {
		boolean newLock = !this.menu.getInventory().isSlotLocked(slot);
		this.menu.getInventory().setSlotLocked(slot, newLock);
		PacketHandler.sendToServer(new SetSlotLockPacket(slot, newLock));
	}

	@Override
	protected void init() {
		super.init();
		this.clearWidgets();

		CrateLayout layout = this.menu.getInventory().getLayout();

		switch (layout) {
			case SINGLE:
				this.addSlotButton(107, 58, 0);
				break;
			case DOUBLE:
				this.addSlotButton(107, 16, 0);
				this.addSlotButton(107, 58, 1);
				break;
			case TRIPLE:
				this.addSlotButton(107, 16, 0);
				this.addSlotButton(59, 58, 1);
				this.addSlotButton(107, 58, 2);
				break;
			case QUADRUPLE:
				this.addSlotButton(59, 16, 0);
				this.addSlotButton(107, 16, 1);
				this.addSlotButton(59, 58, 2);
				this.addSlotButton(107, 58, 3);
				break;
		}
	}

	private void drawAmount(PoseStack matrixStack, int slot, float x, float y) {
		ItemStack stack = this.getStack(slot).getStack();
		if (stack != ItemStack.EMPTY) {
			int slotAmount = getSlotAmount(slot);
			String displayAmount = String.valueOf(slotAmount);
			this.font.drawShadow(matrixStack, displayAmount, x - this.font.width(displayAmount), y, slotAmount <= 0 ? DyeColor.RED.getTextColor() : DyeColor.WHITE.getTextColor());
		}
	}

	@Override
	protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
		this.font.draw(matrixStack, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6.0F, 4210752);
		this.font.draw(matrixStack, this.playerInventoryTitle, 8.0F, this.imageHeight - 93, 4210752);
		this.font.draw(matrixStack, Component.translatable("assortedstorage.container.storage_crate.upgrades"), 8.0F, this.imageHeight - 124, 4210752);

		int mod = this.menu.getInventory().getStorageModifier();
		if (mod > 0) {
			String s = "X " + mod;
			this.font.draw(matrixStack, s, 160F - this.font.width(s), this.imageHeight - 93, DyeColor.GRAY.getTextColor());
		}

		matrixStack.pushPose();
		matrixStack.translate(0F, 0F, 300F);
		matrixStack.scale(0.5F, 0.5F, 0.5F);

		CrateLayout layout = menu.getInventory().getLayout();

		switch (layout) {
			case SINGLE:
				this.drawAmount(matrixStack, 0, 191.0F, 91.0F);
				break;
			case DOUBLE:
				this.drawAmount(matrixStack, 0, 191.0F, 69.0F);
				this.drawAmount(matrixStack, 1, 191.0F, 113.0F);
				break;
			case TRIPLE:
				this.drawAmount(matrixStack, 0, 191.0F, 69.0F);
				this.drawAmount(matrixStack, 1, 169.0F, 113.0F);
				this.drawAmount(matrixStack, 2, 213.0F, 113.0F);
				break;
			case QUADRUPLE:
				this.drawAmount(matrixStack, 0, 169.0F, 69.0F);
				this.drawAmount(matrixStack, 1, 213.0F, 69.0F);
				this.drawAmount(matrixStack, 2, 169.0F, 113.0F);
				this.drawAmount(matrixStack, 3, 213.0F, 113.0F);
				break;
		}
		matrixStack.popPose();
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth + 26, this.imageHeight);
		this.itemRenderer.blitOffset = 100.0F;

		matrixStack.pushPose();
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate(i + 80, j + 34, 0);
		posestack.translate(8.0F, 8.0F, 0.0F);
		posestack.scale(1.0F, -1.0F, 1.0F);
		posestack.scale(16.0F, 16.0F, 16.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack blockRenderPoseStack = new PoseStack();
		blockRenderPoseStack.pushPose();
		// Translate so it does not hide foreground items
		blockRenderPoseStack.translate(0.0D, 0.0D, -5.0D);
		blockRenderPoseStack.mulPose(Axis.YP.rotationDegrees(180));
		blockRenderPoseStack.scale(3.2F, 3.2F, 3.2F);
		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		Lighting.setupForFlatItems();
		itemRenderer.renderStatic(this.renderStack, TransformType.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, blockRenderPoseStack, buffer, 0);
		buffer.endBatch();
		Lighting.setupFor3DItems();
		blockRenderPoseStack.popPose();
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		matrixStack.popPose();

		this.itemRenderer.blitOffset = 0.0F;
	}
}
