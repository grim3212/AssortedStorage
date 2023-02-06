package com.grim3212.assorted.storage.common.inventory.crates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.grim3212.assorted.storage.common.block.blockentity.LockedItemHandler;
import com.grim3212.assorted.storage.common.block.blockentity.CrateControllerBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CrateControllerInvWrapper implements IItemHandlerModifiable, LockedItemHandler {
	protected final CrateControllerBlockEntity inv;
	@Nullable
	protected final Direction side;

	@SuppressWarnings("unchecked")
	public static LazyOptional<IItemHandlerModifiable>[] create(CrateControllerBlockEntity inv, Direction... sides) {
		LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];
		for (int x = 0; x < sides.length; x++) {
			final Direction side = sides[x];
			ret[x] = LazyOptional.of(() -> new CrateControllerInvWrapper(inv, side));
		}
		return ret;
	}

	public CrateControllerInvWrapper(CrateControllerBlockEntity inv, @Nullable Direction side) {
		this.inv = inv;
		this.side = side;
	}

	public CrateControllerBlockEntity getInv() {
		return inv;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CrateControllerInvWrapper that = (CrateControllerInvWrapper) o;

		return inv.equals(that.inv) && side == that.side;
	}

	@Override
	public int hashCode() {
		int result = inv.hashCode();
		result = 31 * result + (side == null ? 0 : side.hashCode());
		return result;
	}

	@Override
	public int getSlots() {
		return inv.getSlotsForFace(null).length;
	}

	@Override
	@NotNull
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		return inv.insertItem(slot, stack, simulate, "");
	}

	@Override
	@NotNull
	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode) {
		return inv.insertItem(slot, stack, simulate, inLockCode);
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return inv.extractItem(slot, amount, simulate, "");
	}

	@Override
	@NotNull
	public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode) {
		return inv.extractItem(slot, amount, simulate, inLockCode);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return inv.canPlaceItem(slot, stack);
	}
}