package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import com.grim3212.assorted.storage.common.network.PacketHandler;
import com.grim3212.assorted.storage.common.network.SetLockPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LocksmithWorkbenchScreen extends ContainerScreen<LocksmithWorkbenchContainer> implements IHasContainer<LocksmithWorkbenchContainer>, IContainerListener {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AssortedStorage.MODID, "textures/gui/container/locksmith_workbench.png");
	private TextFieldWidget lockField;

	public LocksmithWorkbenchScreen(LocksmithWorkbenchContainer container, PlayerInventory playerInventory, ITextComponent title) {
		super(container, playerInventory, title);

		this.passEvents = false;
	}

	@Override
	protected void init() {
		super.init();

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.lockField = new TextFieldWidget(this.font, this.leftPos + 67, this.topPos + 39, 42, 12, new TranslationTextComponent(AssortedStorage.MODID + ".container.keycode"));
		this.lockField.setCanLoseFocus(false);
		this.lockField.setTextColor(-1);
		this.lockField.setTextColorUneditable(-1);
		this.lockField.setBordered(false);
		this.lockField.setMaxLength(10);
		this.lockField.setResponder(this::onNameChanged);
		this.children.add(this.lockField);
		this.setInitialFocus(this.lockField);
	}

	private void onNameChanged(String lock) {
		String s = lock;
		Slot slot = this.menu.getSlot(0);
		if (slot != null && slot.hasItem() && !slot.getItem().hasTag()) {
			s = "";
		}

		this.menu.updateLock(s);
		PacketHandler.sendToServer(new SetLockPacket(s));
	}

	/**
	 * Sends the contents of an inventory slot to the client-side Container. This
	 * doesn't have to match the actual contents of that slot.
	 */
	public void slotChanged(Container containerToSend, int slotInd, ItemStack stack) {
		if (slotInd == 0) {
			String code = stack.getTag().contains("Storage_Lock", 8) ? stack.getTag().getString("Storage_Lock") : "";

			this.lockField.setValue(stack.isEmpty() ? "" : code);
			this.lockField.setEditable(!stack.isEmpty());
			this.setFocused(this.lockField);
		}

	}

	public void refreshContainer(Container containerToSend, NonNullList<ItemStack> itemsList) {
		this.slotChanged(containerToSend, 0, containerToSend.getSlot(0).getItem());
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String s = this.lockField.getValue();
		this.init(minecraft, width, height);
		this.lockField.setValue(s);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.lockField.keyPressed(keyCode, scanCode, modifiers) && !this.lockField.canConsumeInput() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
	}

	@Override
	public void removed() {
		super.removed();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		RenderSystem.disableBlend();
		this.lockField.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(GUI_TEXTURE);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	public void setContainerData(Container containerIn, int varToUpdate, int newValue) {
	}
}
