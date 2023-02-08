package com.grim3212.assorted.storage.client.screen;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.screen.buttons.ImageToggleButton;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.network.PacketHandler;
import com.grim3212.assorted.storage.common.network.SetAllSlotLockPacket;
import com.grim3212.assorted.storage.common.util.LargeItemStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CrateCompactingScreen extends CrateScreen {

	public CrateCompactingScreen(CrateContainer container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
	}

	@Override
	protected LargeItemStack getStack(int slot) {
		if (this.menu.getInventory()instanceof CrateCompactingBlockEntity compactCrate) {
			return compactCrate.getLargeItemStack(slot);
		}

		return super.getStack(slot);
	}

	private void addSlotButton(int x, int y) {
		if (!this.menu.getInventory().areSlotsEmpty()) {
			this.addRenderableWidget(this.createImageButton(x, y));
		}
	}

	private ImageToggleButton createImageButton(int x, int y) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		return new ImageToggleButton(i + x, j + y, 10, 10, 0, 0, 10, CHECKBOX_LOCATION, 32, 32, (button) -> {
			CrateCompactingScreen.this.toggleAllSlotsLock();
		}, this.menu.getInventory().isSlotLocked(0), Component.translatable(AssortedStorage.MODID + ".info.compact_item_lock"));
	}

	private void toggleAllSlotsLock() {
		boolean newLock = !this.menu.getInventory().isSlotLocked(0);
		this.menu.getInventory().setAllSlotsLocked(newLock);
		PacketHandler.sendToServer(new SetAllSlotLockPacket(newLock));
	}

	@Override
	protected void init() {
		super.init();
		this.clearWidgets();

		this.addSlotButton(107, 16);
	}
}
