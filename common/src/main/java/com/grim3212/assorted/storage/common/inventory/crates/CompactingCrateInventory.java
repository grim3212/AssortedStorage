package com.grim3212.assorted.storage.common.inventory.crates;

import com.google.common.collect.Lists;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.api.CompactingHelper;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class CompactingCrateInventory extends CrateSidedInv {

    private List<CompactingHelper.Match> matches = NonNullList.<CompactingHelper.Match>withSize(3, new CompactingHelper.Match(ItemStack.EMPTY, 1));

    public CompactingCrateInventory(CrateBlockEntity inv) {
        super(inv);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();

        if (!this.matches.isEmpty()) {
            ListTag matchList = new ListTag();
            this.matches.forEach(m -> {
                CompoundTag match = new CompoundTag();
                m.getItem().save(match);
                match.putInt("MatchRequired", m.getNumRequired());
                matchList.add(match);
            });
            tag.put("Matches", matchList);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);

        ListTag matchList = nbt.getList("Matches", 10);
        this.matches = Lists.newArrayList();
        for (int i = 0; i < matchList.size(); i++) {
            CompoundTag match = matchList.getCompound(i);
            this.matches.add(new CompactingHelper.Match(ItemStack.of(match), match.getInt("MatchRequired")));
        }
    }

    public void findResults(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        CompactingHelper helper = new CompactingHelper(this.inv.getLevel());
        matches = helper.findMatches(stack, 3);
    }

    public boolean haveMatchForItemStack(ItemStack stack) {
        // Do we have a match
        // Are both the last checked and input the same
        // Does the last matches actually contain the stack
        return !matches.isEmpty() && matches.stream().anyMatch(m -> ItemStack.isSameItemSameTags(m.getItem(), stack));
    }

    @Override
    public int addItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.slotContents.size() && !stack.isEmpty()) {
            LargeItemStack largeStack = this.getLargeItemStack(index);

            // First check if the items are already the same if so try and add
            if (Services.INVENTORY.canItemStacksStack(stack, largeStack.getStack())) {
                int currentAmount = largeStack.getAmount();
                int amount = Math.min(this.getMaxStackSizeForSlot(index), currentAmount + stack.getCount());
                largeStack.setAmount(amount);
                this.inv.setSlotChanged(index);
                this.updateAllSlots(index, stack, currentAmount, amount);

                // If too many items were given this will have a positive value otherwise 0
                // If void force that we say all items were added
                return hasVoidUpgrade() ? 0 : stack.getCount() + currentAmount - amount;
            } else if (this.haveMatchForItemStack(stack)) {
                // If we have already setup a match list that includes this item let's try to
                // use that and adjust the slot to be in the correct order

                int slotIdx = IntStream.range(0, matches.size()).filter(i -> ItemStack.isSameItemSameTags(matches.get(i).getItem(), stack)).findFirst().orElse(-1);
                if (slotIdx == -1) {
                    return -1;
                }

                LargeItemStack newSlot = this.getLargeItemStack(slotIdx);
                int currentAmount = newSlot.isEmpty() ? 0 : newSlot.getAmount();
                int amount = Math.min(this.getMaxStackSizeForSlot(slotIdx), currentAmount + stack.getCount());
                this.setItem(slotIdx, newSlot.with(stack.copy(), amount));
                this.updateAllSlots(slotIdx, stack, currentAmount, amount);

                // If too many items were given this will have a positive value otherwise 0
                // If void force that we say all items were added
                return hasVoidUpgrade() ? 0 : stack.getCount() + currentAmount - amount;
            } else if (largeStack.isEmpty()) {

                if (!this.areSlotsEmpty()) {
                    // This compactor is already setup
                    return -1;
                }
                // First initialize a search for this since this compactor is empty
                this.findResults(stack.copyWithCount(1));

                // Find where this stack should be in the slot assortment
                int slotIdx = IntStream.range(0, matches.size()).filter(i -> ItemStack.isSameItemSameTags(matches.get(i).getItem(), stack)).findFirst().orElse(-1);
                if (slotIdx == -1) {
                    return -1;
                }

                int amount = Math.min(this.getMaxStackSizeForSlot(slotIdx), stack.getCount());
                this.setItem(slotIdx, this.getLargeItemStack(slotIdx).with(stack.copy(), amount));
                this.updateAllSlots(slotIdx, stack, 0, amount);

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

    /*
     * Update the slot that was activated and set the amount to the new amount
     *
     * The remaining slots will be checked and amounts updated as needed
     */
    public void updateAllSlots(int slot, ItemStack slotStack, int oldAmount, int newAmount) {
        ItemStack checkStack = slotStack.copyWithCount(1);

        if (!this.haveMatchForItemStack(checkStack)) {
            // If we don't have a match in the current list refresh once
            this.findResults(checkStack);
        }

        // Somehow we still don't have a match
        // This shouldn't happen
        if (!this.haveMatchForItemStack(checkStack)) {
            return;
        }

        int checkStackIndex = IntStream.range(0, matches.size()).filter(i -> ItemStack.isSameItemSameTags(matches.get(i).getItem(), checkStack)).findFirst().orElse(-1);
        if (checkStackIndex > -1) {
            // Possible indexes should be 0, 1, 2
            // 0 - slot 0 or highest tier
            // 1 - slot 1 or middle tier
            // 2 - slot 2 or lowest tier

            CompactingHelper.Match tier0Match = matches.get(0);
            CompactingHelper.Match tier1Match = matches.get(1);
            CompactingHelper.Match tier2Match = matches.get(2);

            switch (checkStackIndex) {
                case 0:
                    // First check middle tier
                    if (!tier1Match.getItem().isEmpty()) {
                        int oldTier0Amount = (tier0Match.getNumRequired() / tier1Match.getNumRequired()) * oldAmount;
                        int newTier0Amount = (tier0Match.getNumRequired() / tier1Match.getNumRequired()) * newAmount;
                        int checkSlot = 1;

                        LargeItemStack largeStack = this.getLargeItemStack(checkSlot);
                        // This is an addition
                        if (newTier0Amount > oldTier0Amount) {
                            int diff = Math.max(0, this.getLargeItemStack(checkSlot).getAmount() - oldTier0Amount);
                            this.setItem(checkSlot, new LargeItemStack(tier1Match.getItem().copyWithCount(1), newTier0Amount + diff, largeStack.getRotation(), largeStack.isLocked()));
                        } else {
                            // We removed items
                            int diff = Math.max(0, oldTier0Amount - newTier0Amount);
                            this.setItem(checkSlot, new LargeItemStack(tier1Match.getItem().copyWithCount(1), this.getLargeItemStack(checkSlot).getAmount() - diff, largeStack.getRotation(), largeStack.isLocked()));
                        }
                    }

                    if (!tier2Match.getItem().isEmpty()) {
                        int oldTier1Amount = tier0Match.getNumRequired() * oldAmount;
                        int newTier1Amount = tier0Match.getNumRequired() * newAmount;
                        int checkSlot = 2;

                        LargeItemStack largeStack = this.getLargeItemStack(checkSlot);
                        // This is an addition
                        if (newTier1Amount > oldTier1Amount) {
                            int diff = Math.max(0, this.getLargeItemStack(checkSlot).getAmount() - oldTier1Amount);
                            this.setItem(checkSlot, new LargeItemStack(tier2Match.getItem().copyWithCount(1), newTier1Amount + diff, largeStack.getRotation(), largeStack.isLocked()));
                        } else {
                            // We removed items
                            int diff = Math.max(0, oldTier1Amount - newTier1Amount);
                            this.setItem(checkSlot, new LargeItemStack(tier2Match.getItem().copyWithCount(1), this.getLargeItemStack(checkSlot).getAmount() - diff, largeStack.getRotation(), largeStack.isLocked()));
                        }
                    }
                    break;
                case 1:
                    if (!tier0Match.getItem().isEmpty()) {
                        int newTier0Amount = newAmount / (tier0Match.getNumRequired() / tier1Match.getNumRequired());
                        LargeItemStack largeStack = this.getLargeItemStack(0);

                        this.setItem(0, new LargeItemStack(tier0Match.getItem().copyWithCount(1), newTier0Amount, largeStack.getRotation(), largeStack.isLocked()));
                    }

                    if (!tier2Match.getItem().isEmpty()) {
                        int oldTier2Amount = tier1Match.getNumRequired() * oldAmount;
                        int newTier2Amount = tier1Match.getNumRequired() * newAmount;

                        LargeItemStack largeStack = this.getLargeItemStack(2);
                        // This is an addition
                        if (newTier2Amount > oldTier2Amount) {
                            int diff = Math.max(0, this.getLargeItemStack(2).getAmount() - oldTier2Amount);
                            this.setItem(2, new LargeItemStack(tier2Match.getItem().copyWithCount(1), newTier2Amount + diff, largeStack.getRotation(), largeStack.isLocked()));
                        } else {
                            // We removed items
                            int diff = Math.max(0, oldTier2Amount - newTier2Amount);
                            this.setItem(2, new LargeItemStack(tier2Match.getItem().copyWithCount(1), this.getLargeItemStack(2).getAmount() - diff, largeStack.getRotation(), largeStack.isLocked()));
                        }
                    }
                    break;
                case 2:
                    if (!tier1Match.getItem().isEmpty()) {
                        int checkSlot = 1;
                        int newTier1Amount = newAmount / tier1Match.getNumRequired();
                        LargeItemStack largeStack = this.getLargeItemStack(checkSlot);
                        this.setItem(checkSlot, new LargeItemStack(tier1Match.getItem().copyWithCount(1), newTier1Amount, largeStack.getRotation(), largeStack.isLocked()));
                    }

                    if (!tier0Match.getItem().isEmpty()) {
                        int checkSlot = 0;
                        int newTier0Amount = newAmount / tier0Match.getNumRequired();
                        LargeItemStack largeStack = this.getLargeItemStack(checkSlot);
                        LargeItemStack setStack = new LargeItemStack(tier0Match.getItem().copyWithCount(1), newTier0Amount, largeStack.getRotation(), largeStack.isLocked());
                        this.setItem(checkSlot, setStack);
                    }
                    break;
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (!this.haveMatchForItemStack(stack) && !this.areSlotsEmpty()) {
            return false;
        }

        return super.isItemValid(slot, stack);
    }

    @Override
    public int getMaxStackSizeForSlot(int slot) {
        int baseStackSize = super.getMaxStackSizeForSlot(slot);
        if (baseStackSize == 0) {
            return 0;
        }

        if (slot == 2) {
            // Multiply by two to account for the
            return this.matches.get(2).getItem().isEmpty() ? 0 : baseStackSize * 2 * this.matches.get(0).getNumRequired();
        } else if (slot == 1) {
            // If this only includes 2 items then this slot is not the base
            return (baseStackSize * 2) * (this.matches.get(2).getItem().isEmpty() ? this.matches.get(0).getNumRequired() : this.matches.get(1).getNumRequired());
        }

        return baseStackSize;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        LargeItemStack largeStack = this.slotContents.get(slot);

        if (!largeStack.getStack().isEmpty()) {
            if (largeStack.getAmount() >= 64) {
                return largeStack.getStack().copyWithCount(64);
            } else {
                return largeStack.getStack().copyWithCount(Math.max(1, largeStack.getAmount()));
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setSlotLocked(int index, boolean lock) {
        LargeItemStack largeStack = this.slotContents.get(index);

        if (largeStack.getStack().isEmpty()) {
            // No locking an empty slot
            return;
        }

        largeStack.setLock(lock);
        this.inv.setSlotChanged(index);
    }

    @Override
    public void setAllSlotsLocked(boolean lock) {
        for (int i = 0; i < this.slotContents.size(); i++) {
            this.setSlotLocked(i, lock);
        }

        if (this.areSlotsEmpty()) {
            for (int i = 0; i < this.slotContents.size(); i++) {
                this.setItem(i, LargeItemStack.empty());
            }
        }
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack;
        if (slot >= 0 && slot < this.slotContents.size() && !this.slotContents.get(slot).isEmpty() && amount > 0) {
            int maxStackSize = this.getStackInSlot(slot).getMaxStackSize();
            int amountToTake = Math.min(maxStackSize, amount);
            int currentAmount = this.slotContents.get(slot).getAmount();
            stack = this.slotContents.get(slot).split(amountToTake, false);
            this.updateAllSlots(slot, stack, currentAmount, currentAmount - stack.getCount());

            if (this.areSlotsEmpty()) {
                for (int i = 0; i < this.slotContents.size(); i++) {
                    this.setItem(i, LargeItemStack.empty());
                }
            }

        } else {
            stack = ItemStack.EMPTY;
        }

        if (!stack.isEmpty()) {
            this.inv.setSlotChanged(slot);
        }
        return stack;
    }

    public NonNullList<ItemStack> asItemStacks() {
        NonNullList<ItemStack> stacks = NonNullList.create();

        LargeItemStack topTier = this.getLargeItemStack(0);
        int topConversion = matches.get(0).getNumRequired();
        // Add top most tier itemstacks
        stacks.addAll(topTier.asItemStacks());

        // Then check if we have a middle tier
        if (!matches.get(1).getItem().isEmpty()) {
            int topAmount = topTier.getAmount();

            LargeItemStack middleTier = this.getLargeItemStack(1);
            int middleAmount = middleTier.getAmount();
            int middleConversion = matches.get(1).getNumRequired();

            // Use a different conversion rate if this compactor has 3 recipes vs 2
            int middleMultiplier = matches.get(2).getItem().isEmpty() ? topConversion : middleConversion;

            // Converted one tier down
            int middleRemaining = middleAmount - topAmount * middleMultiplier;

            if (middleRemaining > 0) {
                stacks.addAll(new LargeItemStack(middleTier.getStack().copyWithCount(1), middleRemaining).asItemStacks());
            }

            // Finally check if we have a lower tier
            if (!matches.get(2).getItem().isEmpty()) {
                LargeItemStack lowerTier = this.getLargeItemStack(2);
                int lowerAmount = lowerTier.getAmount();
                int lowerRemaining = middleRemaining > 0 ? lowerAmount - (middleRemaining * middleConversion) - (topTier.getAmount() * matches.get(0).getNumRequired()) : lowerAmount - (topTier.getAmount() * matches.get(0).getNumRequired());
                if (lowerRemaining > 0) {
                    stacks.addAll(new LargeItemStack(lowerTier.getStack().copyWithCount(1), lowerRemaining).asItemStacks());
                }
            }
        }

        return stacks;
    }
}