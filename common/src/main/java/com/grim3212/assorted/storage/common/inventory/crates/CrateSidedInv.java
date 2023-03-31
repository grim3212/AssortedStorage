package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.LockedItemHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateSidedInv implements IItemHandlerModifiable, LockedItemHandler {
    protected final CrateBlockEntity inv;
    @Nullable
    protected final Direction side;

    @SuppressWarnings("unchecked")
    public static LazyOptional<IItemHandlerModifiable>[] create(CrateBlockEntity inv, Direction... sides) {
        LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];
        for (int x = 0; x < sides.length; x++) {
            final Direction side = sides[x];
            ret[x] = LazyOptional.of(() -> new CrateSidedInv(inv, side));
        }
        return ret;
    }

    public CrateSidedInv(CrateBlockEntity inv, @Nullable Direction side) {
        this.inv = inv;
        this.side = side;
    }

    public CrateBlockEntity getInv() {
        return inv;
    }

    public static int getSlot(CrateBlockEntity inv, int slot, @Nullable Direction side) {
        int[] slots = inv.getSlotsForFace(side);
        if (slot < slots.length)
            return slots[slot];
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CrateSidedInv that = (CrateSidedInv) o;

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
        return inv.getSlotsForFace(side).length;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        int i = getSlot(inv, slot, side);
        return i == -1 ? ItemStack.EMPTY : inv.getItem(i);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return stack;

        LargeItemStack stackInSlot = inv.getLargeItemStack(slot1);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getAmount() >= getSlotLimit(slot) && !inv.hasVoidUpgrade())
                return stack;

            if (!Services.INVENTORY.canItemStacksStack(stack, stackInSlot.getStack()))
                return stack;

            if (!inv.canPlaceItemThroughFace(slot1, stack, side) || !inv.canPlaceItem(slot1, stack))
                return stack;

            m = getSlotLimit(slot) - stackInSlot.getAmount();

            if (stack.getCount() <= m || inv.hasVoidUpgrade()) {
                if (!simulate) {
                    inv.addItem(slot1, stack.copy());
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.addItem(slot1, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!inv.canPlaceItemThroughFace(slot1, stack, side) || !inv.canPlaceItem(slot1, stack))
                return stack;

            if (inv instanceof CrateCompactingBlockEntity compactingCrate) {
                if (!compactingCrate.haveMatchForItemStack(stack) && !compactingCrate.areSlotsEmpty()) {
                    return stack;
                }
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.addItem(slot1, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate)
                    inv.addItem(slot1, stack);
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return stack;

        LargeItemStack stackInSlot = inv.getLargeItemStack(slot1);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getAmount() >= getSlotLimit(slot) && !inv.hasVoidUpgrade())
                return stack;

            if (!Services.INVENTORY.canItemStacksStack(stack, stackInSlot.getStack()))
                return stack;

            if (!inv.canPlaceItemThroughFace(slot1, stack, side, inLockCode, false) || !inv.canPlaceItem(slot1, stack))
                return stack;

            m = getSlotLimit(slot) - stackInSlot.getAmount();

            if (stack.getCount() <= m || inv.hasVoidUpgrade()) {
                if (!simulate) {
                    inv.addItem(slot1, stack.copy());
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.addItem(slot1, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!inv.canPlaceItemThroughFace(slot1, stack, side, inLockCode, false) || !inv.canPlaceItem(slot1, stack))
                return stack;

            if (inv instanceof CrateCompactingBlockEntity compactingCrate) {
                if (!compactingCrate.haveMatchForItemStack(stack) && !compactingCrate.areSlotsEmpty()) {
                    return stack;
                }
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.addItem(slot1, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate)
                    inv.addItem(slot1, stack);
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        int slot1 = getSlot(inv, slot, side);

        if (slot1 != -1)
            inv.setItem(slot1, stack);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        LargeItemStack stackInSlot = inv.getLargeItemStack(slot1);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (!inv.canTakeItemThroughFace(slot1, stackInSlot.getStack(), side))
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getAmount() < amount) {
                return stackInSlot.getStack().copyWithCount(stackInSlot.getAmount());
            } else {
                ItemStack copy = stackInSlot.getStack();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getAmount(), amount);
            ItemStack ret = inv.removeItem(slot1, m);
            return ret;
        }
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode) {
        if (amount == 0)
            return ItemStack.EMPTY;

        int slot1 = getSlot(inv, slot, side);

        if (slot1 == -1)
            return ItemStack.EMPTY;

        LargeItemStack stackInSlot = inv.getLargeItemStack(slot1);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (!inv.canTakeItemThroughFace(slot1, stackInSlot.getStack(), side, inLockCode, false))
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getAmount() < amount) {
                return stackInSlot.getStack().copyWithCount(stackInSlot.getAmount());
            } else {
                ItemStack copy = stackInSlot.getStack();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getAmount(), amount);
            ItemStack ret = inv.removeItem(slot1, m);
            return ret;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv.getMaxStackSizeForSlot(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int slot1 = getSlot(inv, slot, side);
        return slot1 == -1 ? false : inv.canPlaceItem(slot1, stack);
    }
}