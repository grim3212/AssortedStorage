package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import com.grim3212.assorted.storage.common.network.SetLockPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

/** The locksmith workbench screen. The code field is a widget, so the base screen draws it. */
public class LocksmithWorkbenchScreen extends AbstractContainerScreen<LocksmithWorkbenchContainer> implements ContainerListener {

    private static final Identifier GUI_TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/locksmith_workbench.png");
    private EditBox lockField;

    public LocksmithWorkbenchScreen(LocksmithWorkbenchContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.lockField = new EditBox(this.font, this.leftPos + 67, this.topPos + 39, 42, 12, Component.translatable(Constants.MOD_ID + ".container.keycode"));
        this.lockField.setCanLoseFocus(false);
        this.lockField.setTextColor(-1);
        this.lockField.setTextColorUneditable(-1);
        this.lockField.setBordered(false);
        this.lockField.setMaxLength(10);
        this.lockField.setResponder(this::onNameChanged);
        this.addRenderableWidget(this.lockField);
        this.setInitialFocus(this.lockField);
    }

    private void onNameChanged(String lock) {
        this.menu.updateLock(lock);
        Services.NETWORK.sendToServer(new SetLockPacket(lock));
    }

    /**
     * Sends the contents of an inventory slot to the client-side Container. This
     * doesn't have to match the actual contents of that slot.
     */
    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int slotInd, ItemStack stack) {
        if (slotInd == 0) {
            // Stacks have no free-form tag any more; the lock code lives in the CUSTOM_DATA component,
            // which StorageUtil already reads.
            this.lockField.setValue(stack.isEmpty() ? "" : StorageUtil.getCode(stack));
            this.lockField.setEditable(!stack.isEmpty());
            this.setFocused(this.lockField);
        }
    }

    @Override
    public void resize(int width, int height) {
        String s = this.lockField.getValue();
        this.init(width, height);
        this.lockField.setValue(s);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isEscape()) {
            this.minecraft.player.closeContainer();
            return true;
        }

        return !this.lockField.keyPressed(event) && !this.lockField.canConsumeInput() ? super.keyPressed(event) : true;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerIn, int varToUpdate, int newValue) {
    }
}
