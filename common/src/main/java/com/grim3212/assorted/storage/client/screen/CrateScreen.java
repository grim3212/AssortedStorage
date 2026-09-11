package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.client.screen.buttons.ImageToggleButton;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.inventory.crates.LargeItemStackSlot;
import com.grim3212.assorted.storage.common.network.SetSlotLockPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * The crate screen. Slot amounts are drawn in a later {@code nextStratum()} so they sit above the
 * item stacks, and tooltips are recorded with {@code setTooltipForNextFrame}.
 */
public class CrateScreen extends AbstractContainerScreen<CrateContainer> {

    protected static final Identifier CHECKBOX_LOCATION = Identifier.parse("textures/gui/checkbox.png");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container/crate.png");
    private static final int LABEL_COLOR = -12566464;

    private final ItemStack renderStack;

    public CrateScreen(CrateContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, DEFAULT_IMAGE_WIDTH, 188);

        this.renderStack = new ItemStack(container.getCrateBlockEntity().getBlockState().getBlock());
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            List<Component> tooltip = getTooltipFromContainerItem(this.hoveredSlot.getItem());
            Optional<TooltipComponent> tooltipComponents = this.hoveredSlot.getItem().getTooltipImage();

            if (this.hoveredSlot instanceof LargeItemStackSlot slot) {
                int curSlot = slot.getContainerSlot();
                LargeItemStack stackInSlot = this.getStack(curSlot);
                int maxStackSize = this.getCrateInventory().getMaxStackSizeForSlot(curSlot);
                tooltip.add(Component.translatable(Constants.MOD_ID + ".info.amount", Component.literal(String.valueOf(stackInSlot.getAmount() + "/" + maxStackSize)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GOLD));
                tooltip.add(Component.translatable(Constants.MOD_ID + ".info.upgrade_redstone.mode.slot", Component.literal(String.valueOf(curSlot)).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GOLD));
            } else if (this.hoveredSlot.getItem().getItem() instanceof ICrateUpgrade upgrade && upgrade.getStorageModifier() > 0) {
                tooltip.add(Component.translatable(Constants.MOD_ID + ".info.storage_multiplier", Component.literal(String.valueOf(upgrade.getStorageModifier())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GOLD));
            }

            graphics.setTooltipForNextFrame(this.font, tooltip, tooltipComponents, mouseX, mouseY);
        }
    }

    protected CrateSidedInv getCrateInventory() {
        return this.menu.getCrateBlockEntity().getItemStackStorageHandler();
    }

    protected LargeItemStack getStack(int slot) {
        return this.getCrateInventory().getLargeItemStack(slot);
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
        }, this.getCrateInventory().isSlotLocked(slot), Component.translatable(Constants.MOD_ID + ".info.item_lock", Component.literal(String.valueOf(slot)).withStyle(ChatFormatting.AQUA)));
    }

    private void toggleSlotLock(int slot) {
        boolean newLock = !this.getCrateInventory().isSlotLocked(slot);
        this.getCrateInventory().setSlotLocked(slot, newLock);
        Services.NETWORK.sendToServer(new SetSlotLockPacket(slot, newLock));
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        CrateLayout layout = this.menu.getCrateBlockEntity().getLayout();

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

    private void extractAmount(GuiGraphicsExtractor graphics, int slot, int x, int y) {
        ItemStack stack = this.getStack(slot).getStack();
        if (!stack.isEmpty()) {
            int slotAmount = getSlotAmount(slot);
            String displayAmount = String.valueOf(slotAmount);
            int color = ARGB.opaque(slotAmount <= 0 ? DyeColor.RED.getTextColor() : DyeColor.WHITE.getTextColor());
            graphics.text(this.font, displayAmount, x - this.font.width(displayAmount), y, color, true);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, (this.imageWidth - this.font.width(this.title)) / 2, 6, LABEL_COLOR, false);
        graphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 93, LABEL_COLOR, false);
        graphics.text(this.font, Component.translatable("assortedstorage.container.storage_crate.upgrades"), 8, this.imageHeight - 124, LABEL_COLOR, false);

        int mod = this.getCrateInventory().getStorageModifier();
        if (mod > 0) {
            String s = "X " + mod;
            graphics.text(this.font, s, 160 - this.font.width(s), this.imageHeight - 93, ARGB.opaque(DyeColor.GRAY.getTextColor()), false);
        }
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);

        // The slot amounts sit on top of the item stacks; a new stratum is what replaces the old
        // "translate 300 along z" trick now that the GUI pose is two dimensional.
        graphics.nextStratum();
        graphics.pose().pushMatrix();
        graphics.pose().translate(this.leftPos, this.topPos);
        graphics.pose().scale(0.5F, 0.5F);

        switch (this.menu.getCrateBlockEntity().getLayout()) {
            case SINGLE:
                this.extractAmount(graphics, 0, 191, 91);
                break;
            case DOUBLE:
                this.extractAmount(graphics, 0, 191, 69);
                this.extractAmount(graphics, 1, 191, 113);
                break;
            case TRIPLE:
                this.extractAmount(graphics, 0, 191, 69);
                this.extractAmount(graphics, 1, 169, 113);
                this.extractAmount(graphics, 2, 213, 113);
                break;
            case QUADRUPLE:
                this.extractAmount(graphics, 0, 169, 69);
                this.extractAmount(graphics, 1, 213, 69);
                this.extractAmount(graphics, 2, 169, 113);
                this.extractAmount(graphics, 3, 213, 113);
                break;
        }

        graphics.pose().popMatrix();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.imageWidth + 26, this.imageHeight, 256, 256);

        // The crate preview used to be an ItemRenderer#renderStatic call with a hand built model
        // transform. The retained GUI only draws items through GuiGraphicsExtractor#item, which
        // applies the standard GUI display context; scaling the 2D pose keeps the same 51px footprint.
        graphics.pose().pushMatrix();
        graphics.pose().translate(i + 88, j + 42);
        graphics.pose().scale(3.2F, 3.2F);
        graphics.item(this.renderStack, -8, -8);
        graphics.pose().popMatrix();
    }
}
