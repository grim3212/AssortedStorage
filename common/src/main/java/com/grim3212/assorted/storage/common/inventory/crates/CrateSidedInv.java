package com.grim3212.assorted.storage.common.inventory.crates;

import com.grim3212.assorted.lib.core.inventory.IItemStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.LockedStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.ITagSerializable;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.crates.ICrateUpgrade;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class CrateSidedInv implements IItemStorageHandler, LockedStorageHandler, ITagSerializable<CompoundTag> {
    protected final CrateBlockEntity inv;

    // One for each slots on the face of a Storage Crate
    protected NonNullList<LargeItemStack> slotContents;
    // Upgrade slots as well as lock slot
    protected NonNullList<ItemStack> enhancements;

    public CrateSidedInv(CrateBlockEntity inv) {
        this.inv = inv;

        this.slotContents = NonNullList.withSize(this.inv.getLayout().getNumStacks(), LargeItemStack.empty());
        // 8 upgrades and 1 lock slot
        this.enhancements = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
    }

    public CrateBlockEntity getInv() {
        return inv;
    }

    private boolean codeMatches(String s) {
        return !this.inv.isLocked() || this.inv.getLockCode().equals(s);
    }

    public NonNullList<ItemStack> getEnhancements() {
        return enhancements;
    }

    public NonNullList<LargeItemStack> getSlotContents() {
        return slotContents;
    }

    public ItemStack removeEnhancement(int index, int count) {
        ItemStack stack = ContainerHelper.removeItem(this.enhancements, index, count);
        if (!stack.isEmpty()) {
            this.inv.setChanged();
            this.inv.modelUpdate();
        }
        return stack;
    }

    @Override
    public int getSlots() {
        return this.slotContents.size();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int inSlot) {
        int slot = getSlot(inSlot);
        if (slot == -1) {
            return ItemStack.EMPTY;
        }

        LargeItemStack largeStack = this.slotContents.get(slot);

        if (!largeStack.isEmpty() && !largeStack.getStack().isEmpty()) {
            if (largeStack.getAmount() >= 64) {
                return largeStack.getStack().copyWithCount(64);
            } else {
                int amount = largeStack.isLocked() ? Math.max(1, largeStack.getAmount()) : largeStack.getAmount();
                return largeStack.getStack().copyWithCount(amount);
            }
        }

        return ItemStack.EMPTY;
    }

    public LargeItemStack getLargeItemStack(int index) {
        LargeItemStack largeStack = this.slotContents.get(index);
        return !largeStack.isEmpty() && !largeStack.getStack().isEmpty() ? largeStack : LargeItemStack.empty();
    }

    public void setSlotLocked(int index, boolean lock) {
        LargeItemStack largeStack = this.slotContents.get(index);

        if (largeStack.getStack().isEmpty()) {
            // No locking an empty slot
            return;
        }

        largeStack.setLock(lock);
        if (!lock && largeStack.getAmount() <= 0) {
            largeStack.setEmpty();
        }

        this.inv.setSlotChanged(index);
    }

    public void setAllSlotsLocked(boolean lock) {
        for (int i = 0; i < this.slotContents.size(); i++) {
            this.setSlotLocked(i, lock);
        }
    }

    public boolean isSlotLocked(int index) {
        LargeItemStack largeStack = this.slotContents.get(index);
        return largeStack.isLocked() && !largeStack.isEmpty();
    }

    public boolean anySlotsLocked() {
        return this.slotContents.stream().anyMatch(stack -> stack.isLocked());
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, "", false);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int inSlot, @NotNull ItemStack stack, boolean simulate, String inLockCode, boolean ignoreLock) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(inSlot, stack) || (!codeMatches(inLockCode) && !ignoreLock))
            return stack;

        int slot = getSlot(inSlot);
        if (slot == -1)
            return stack;

        LargeItemStack stackInSlot = getLargeItemStack(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getAmount() >= getSlotLimit(slot) && !hasVoidUpgrade())
                return stack;

            if (!Services.INVENTORY.canItemStacksStack(stack, stackInSlot.getStack()))
                return stack;

            m = getSlotLimit(slot) - stackInSlot.getAmount();

            if (stack.getCount() <= m || hasVoidUpgrade()) {
                if (!simulate) {
                    addItem(slot, stack.copy());
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    addItem(slot, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (inv instanceof CrateCompactingBlockEntity compactingCrate) {
                if (!compactingCrate.getItemStackStorageHandler().isItemValid(slot, stack)) {
                    return stack;
                }
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    addItem(slot, stack.split(m));
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate)
                    addItem(slot, stack);
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, "", false);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int inSlot, int amount, boolean simulate, String inLockCode, boolean ignoreLock) {
        if (amount == 0 || (!codeMatches(inLockCode) && !ignoreLock))
            return ItemStack.EMPTY;

        int slot = getSlot(inSlot);
        if (slot == -1)
            return ItemStack.EMPTY;

        LargeItemStack stackInSlot = getLargeItemStack(slot);

        if (stackInSlot.isEmpty())
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
            ItemStack ret = removeItem(slot, m);
            return ret;
        }
    }

    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack;
        if (slot >= 0 && slot < this.slotContents.size() && !this.slotContents.get(slot).isEmpty() && amount > 0) {
            int maxStackSize = this.getStackInSlot(slot).getMaxStackSize();

            stack = this.slotContents.get(slot).split(Math.min(maxStackSize, amount), true);
        } else {
            stack = ItemStack.EMPTY;
        }

        if (!stack.isEmpty()) {
            this.inv.setSlotChanged(slot);
        }
        return stack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getMaxStackSizeForSlot(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int slot1 = getSlot(slot);
        return slot1 != -1;
    }

    @Override
    public void setStackInSlot(int inSlot, @NotNull ItemStack stack) {
        int slot = getSlot(inSlot);
        if (slot == -1) {
            return;
        }

        if (slot >= 0 && slot < this.slotContents.size()) {
            LargeItemStack largeStack = this.getLargeItemStack(slot).withStack(stack);

            if (largeStack.getAmount() > this.getMaxStackSizeForSlot(slot)) {
                largeStack.setAmount(this.getMaxStackSizeForSlot(slot));
            }

            this.slotContents.set(slot, largeStack);
        }

        this.inv.setSlotChanged(slot);
    }

    /**
     * Used to sync the server side to the client side for slot changes
     *
     * @param slot
     * @param stack
     */
    public void setItem(int slot, LargeItemStack stack) {
        if (slot >= 0 && slot < this.slotContents.size()) {

            if (stack.getAmount() > this.getMaxStackSizeForSlot(slot)) {
                stack.setAmount(this.getMaxStackSizeForSlot(slot));
            }

            this.slotContents.set(slot, stack);
        }

        this.inv.setSlotChanged(slot);
    }

    @Override
    public boolean isEmpty() {
        for (LargeItemStack itemstack : this.slotContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemstack : this.enhancements) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean areSlotsEmpty() {
        for (LargeItemStack itemstack : this.slotContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onContentsChanged(int slot) {
        inv.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos cratePos = inv.getBlockPos();
        if (this.inv.getLevel().getBlockEntity(cratePos) != this.inv) {
            return false;
        } else {
            return !(player.distanceToSqr((double) cratePos.getX() + 0.5D, (double) cratePos.getY() + 0.5D, (double) cratePos.getZ() + 0.5D) > 64.0D);
        }
    }

    public int getMaxStackSizeForSlot(int slot) {
        if (slot < 0 && slot >= this.slotContents.size()) {
            return 0;
        }

        int baseStackSize = this.getBaseStackSize(slot);
        return baseStackSize + this.getExtraStorage(baseStackSize);
    }

    public int getBaseStackSize(int slot) {
        int slotBase = this.inv.getLayout().getSlotsBaseStacks()[slot];
        int stackSizeMultiplier = this.getStackInSlot(slot).getMaxStackSize();
        return stackSizeMultiplier * slotBase;
    }

    public int getExtraStorage(int baseStackSize) {
        int extra = 0;
        for (ItemStack stack : this.enhancements) {
            if (stack.getItem() instanceof ICrateUpgrade upgrade) {
                extra += upgrade.getStorageModifier() * baseStackSize;
            }
        }
        return extra;
    }

    public int getStorageModifier() {
        int base = 2;
        int total = base + this.getExtraStorage(base);
        return total / base;
    }

    public void cycleSlotRotation(int slot) {
        this.getLargeItemStack(slot).cycleRotation();
    }

    public ItemStack getLockStack() {
        // Lock is always slot 0
        return this.enhancements.get(0);
    }

    public ItemStack setLockStack(ItemStack stack) {
        // Lock is always slot 0
        return this.enhancements.set(0, stack);
    }

    public boolean hasVoidUpgrade() {
        return this.enhancements.stream().anyMatch(stack -> stack.getItem() == StorageItems.VOID_UPGRADE.get());
    }

    public boolean hasGlowUpgrade() {
        return this.enhancements.stream().anyMatch(stack -> stack.getItem() == StorageItems.GLOW_UPGRADE.get());
    }

    private int getSignalFromPercentage(float percentage) {
        return Math.round(0 + percentage * (15 - 0));
    }

    public int getSignalStrength() {
        ItemStack redstoneUpgrade = this.enhancements.stream().filter(stack -> stack.getItem() == StorageItems.REDSTONE_UPGRADE.get()).findAny().orElse(ItemStack.EMPTY);

        if (redstoneUpgrade == ItemStack.EMPTY) {
            return 0;
        }

        int mode = NBTHelper.getInt(redstoneUpgrade, "Mode");
        if (mode == 4) {
            // 4: Based on all slots
            int runningTotal = 0;
            int maxAllowed = 0;
            for (int i = 0; i < this.slotContents.size(); i++) {
                runningTotal += this.getLargeItemStack(i).getAmount();
                maxAllowed += this.getMaxStackSizeForSlot(i);
            }
            return getSignalFromPercentage((float) runningTotal / maxAllowed);
        } else if (mode == 5) {
            // 5: Based on most full slot
            int maxAmountSlot = IntStream.range(0, this.slotContents.size()).reduce((a, b) -> this.slotContents.get(a).getAmount() < this.slotContents.get(b).getAmount() ? b : a).orElse(-1);
            if (maxAmountSlot == -1) {
                return 0;
            }

            int amount = this.getLargeItemStack(maxAmountSlot).getAmount();
            int maxAllowed = this.getMaxStackSizeForSlot(maxAmountSlot);
            return getSignalFromPercentage((float) amount / maxAllowed);
        } else if (mode == 6) {
            // 6: Based on least full slot
            int minAmountSlot = IntStream.range(0, this.slotContents.size()).reduce((a, b) -> this.slotContents.get(a).getAmount() > this.slotContents.get(b).getAmount() ? b : a).orElse(-1);
            if (minAmountSlot == -1) {
                return 0;
            }

            int amount = this.getLargeItemStack(minAmountSlot).getAmount();
            int maxAllowed = this.getMaxStackSizeForSlot(minAmountSlot);
            return getSignalFromPercentage((float) amount / maxAllowed);
        } else {
            // 0: Based on slot 0
            // 1: Based on slot 1
            // 2: Based on slot 2
            // 3: Based on slot 3
            // Bail early if the slot doesn't exist in this storage crate
            if (mode < 0 || mode >= this.slotContents.size()) {
                return 0;
            }

            int amount = this.getLargeItemStack(mode).getAmount();
            int maxAllowed = this.getMaxStackSizeForSlot(mode);
            return getSignalFromPercentage((float) amount / maxAllowed);
        }
    }

    /**
     * @param index
     * @param stack
     * @return -1 means that we couldn't add anything 0 means everything was added
     * 1+ means there was a remainder of items not added
     */
    public int addItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.slotContents.size() && !stack.isEmpty()) {
            LargeItemStack largeStack = this.slotContents.get(index);

            if (Services.INVENTORY.canItemStacksStack(stack, largeStack.getStack())) {
                int currentAmount = largeStack.getAmount();
                int amount = Math.min(this.getMaxStackSizeForSlot(index), currentAmount + stack.getCount());
                largeStack.setAmount(amount);
                this.inv.setSlotChanged(index);
                // If too many items were given this will have a positive value otherwise 0
                // If void force that we say all items were added
                return hasVoidUpgrade() ? 0 : stack.getCount() + currentAmount - amount;
            } else if (largeStack.isEmpty()) {
                int amount = Math.min(this.getMaxStackSizeForSlot(index), stack.getCount());
                this.slotContents.set(index, largeStack.withStack(stack.copyWithCount(amount)));
                this.inv.setSlotChanged(index);
                // We tried to initialize this slot
                // If too many items were given this will have a positive value otherwise 0
                // If void force that we say all items were added
                return hasVoidUpgrade() ? 0 : stack.getCount() - amount;
            } else {
                // Stack did not match what was currently in this slot
                return -1;
            }
        }

        // Invalid input
        return -1;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        ListTag items = new ListTag();
        for (int i = 0; i < this.slotContents.size(); i++) {
            LargeItemStack stack = this.slotContents.get(i);
            CompoundTag slot = new CompoundTag();
            slot.putByte("Slot", (byte) i);
            slot.putInt("SlotAmount", stack.getAmount());
            slot.putInt("SlotRotation", stack.getRotation());
            slot.putBoolean("SlotLocked", stack.isLocked());
            stack.getStack().save(slot);
            items.add(slot);
        }
        tag.put("Items", items);

        ListTag enhancementItems = new ListTag();
        for (int i = 0; i < this.enhancements.size(); ++i) {
            ItemStack itemstack = this.enhancements.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte) i);
                itemstack.save(compoundtag);
                enhancementItems.add(compoundtag);
            }
        }
        tag.put("Enhancements", enhancementItems);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.slotContents = NonNullList.withSize(this.getSlots(), LargeItemStack.empty());

        ListTag items = nbt.getList("Items", 10);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag slot = items.getCompound(i);
            int slotIdx = slot.getByte("Slot") & 255;
            if (slotIdx >= 0 && slotIdx < this.slotContents.size()) {
                int amount = slot.getInt("SlotAmount");
                int rotation = slot.getInt("SlotRotation");
                boolean locked = slot.getBoolean("SlotLocked");
                ItemStack stack = ItemStack.of(slot);
                this.slotContents.set(slotIdx, new LargeItemStack(stack, amount, rotation, locked));
            }
        }

        ListTag enhancementItems = nbt.getList("Enhancements", 10);
        for (int i = 0; i < enhancementItems.size(); i++) {
            CompoundTag slot = enhancementItems.getCompound(i);
            int slotIdx = slot.getByte("Slot") & 255;
            if (slotIdx >= 0 && slotIdx < this.enhancements.size()) {
                this.enhancements.set(slotIdx, ItemStack.of(slot));
            }
        }
    }

    public int getSlot(int slot) {
        int[] slots = this.inv.getLayout().getSlots();
        if (slot < slots.length)
            return slots[slot];
        return -1;
    }
}