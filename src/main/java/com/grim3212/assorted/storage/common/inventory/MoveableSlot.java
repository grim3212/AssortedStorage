package com.grim3212.assorted.storage.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MoveableSlot extends Slot {

	private boolean shouldRender = true;

	public MoveableSlot(Container par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	public void setSlotPosition(int slotX, int slotY) {
		this.x = slotX;
		this.y = slotY;
		this.shouldRender = true;
	}

	public void setSlotDisabled() {
		this.shouldRender = false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isActive() {
		return this.shouldRender;
	}
}