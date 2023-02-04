package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.common.block.blockentity.StorageCrateBlockEntity;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnhancementSlot extends Slot {

	public EnhancementSlot(Container inv, int slot, int x, int y) {
		super(inv, slot, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.getItem() instanceof ICrateUpgrade;
	}

	@Override
	public ItemStack getItem() {
		if (this.container instanceof StorageCrateBlockEntity crate) {
			return crate.getEnhancements().get(this.getContainerSlot());
		}

		return super.getItem();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void set(ItemStack stack) {
		if (this.container instanceof StorageCrateBlockEntity crate) {
			crate.getEnhancements().set(this.getContainerSlot(), stack);
			this.setChanged();
		} else {
			super.set(stack);
		}
	}

	@Override
	public void initialize(ItemStack stack) {
		if (this.container instanceof StorageCrateBlockEntity crate) {
			crate.getEnhancements().set(this.getContainerSlot(), stack);
			this.setChanged();
		} else {
			super.initialize(stack);
		}
	}

	@Override
	public ItemStack remove(int amount) {
		if (this.container instanceof StorageCrateBlockEntity crate) {
			return crate.removeEnhancement(this.getContainerSlot(), amount);
		}

		return super.remove(amount);
	}
}
