package com.grim3212.assorted.storage.api;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Logic follows the same flow as laid out by StorageDrawers and FunctionStorage
 */

public class CompactingHelper {

    private final Level level;

    public CompactingHelper(Level level) {
        this.level = level;
    }

    public List<Match> findMatches(ItemStack stack, int numTiers) {
        List<Match> matches = new ArrayList<>();
        // Add initial match for the stack itself
        matches.add(new Match(stack, 1));
        // Try to find an upperTier
        Match match = findUpperTier(stack);
        if (!match.getItem().isEmpty()) {
            // We found an upperTier add to front of list
            matches.add(0, match);
            // Are we at the topMost tier
            if (matches.size() < numTiers) {
                // If we aren't search again
                match = findUpperTier(match.getItem());
                if (!match.getItem().isEmpty()) {
                    // Set the current result number of required items to be multiplied by the
                    // number of the previous in the tier
                    // as there is 1 tier difference between the two
                    match.setNumRequired(match.getNumRequired() * matches.get(0).getNumRequired());
                    // Found higher tier and to front of list
                    matches.add(0, match);
                }
            }
        }
        boolean keepSearching = true;
        // Until we can't find a lower tier anymore keep searching
        while (keepSearching && matches.size() < numTiers) {
            // Look for a lower tier for the first element in the list of matches
            match = findLowerTier(matches.get(matches.size() - 1).getItem());
            if (!match.getItem().isEmpty()) {
                // If we found a match we need to adjust all the other results
                // This is because this lower tier will now be what all the amounts are now
                // based off of
                for (Match prevMatch : matches) {
                    prevMatch.setNumRequired(prevMatch.getNumRequired() * match.getNumRequired());
                }
                match.setNumRequired(1);
                // Add the new lower tier to the first index
                matches.add(match);
            } else {
                keepSearching = false;
            }
        }
        // If we do not have a full amount of tiers then fill the remaining spots with
        // an empty match
        while (matches.size() < numTiers) {
            matches.add(new Match(ItemStack.EMPTY, 1));
        }
        // Modify each match to be the real number of requirements
        matches.stream().filter(x -> x.getItem().getCount() > 0).forEach(x -> x.setNumRequired(x.getNumRequired() / x.getItem().getCount()));
        return matches;
    }

    private Match findUpperTier(ItemStack stack) {
        List<ItemStack> outputs = new ArrayList<>();
        // Try to check for 3x3 recipes first
        List<ItemStack> matchingStacks = findMatchingStacks(new ComparisonCraftingContainer(3, stack));
        int sizeCheck = matchingStacks.size() == 0 ? 4 : 9;
        if (matchingStacks.size() == 0) {
            // If we didn't find a match at 3x3 then we search at 2x2
            matchingStacks = findMatchingStacks(new ComparisonCraftingContainer(2, stack));
        }

        if (stack.is(StorageTags.Items.CRAFTING_OVERRIDE)) {
            outputs = matchingStacks;
        } else if (matchingStacks.size() > 0) {
            ComparisonCraftingContainer craftingContainer = new ComparisonCraftingContainer(1);
            // If we were able to find a matchingStack iterate
            for (ItemStack match : matchingStacks) {
                // Reset the inventory and fill with the current item we are checking
                craftingContainer.fillInventory(match);
                for (ItemStack reverseMatch : findMatchingStacks(craftingContainer)) {
                    if (reverseMatch.getCount() != sizeCheck || !ItemStack.isSameItemSameTags(reverseMatch, stack)) {
                        continue;
                    }
                    outputs.add(match);
                }
            }
        }

        ItemStack same = findSameItems(stack, outputs);
        if (!same.isEmpty()) {
            return new Match(same, sizeCheck);
        }
        if (outputs.size() > 0) {
            return new Match(outputs.get(0), sizeCheck);
        }

        // Fallback
        return new Match(ItemStack.EMPTY, 0);
    }

    /**
     * Look for a lower crafting tier recipe using the input stack Example: 1 iron
     * ingot would trying to find 9 iron nuggets
     *
     * @param stack
     * @return
     */
    private Match findLowerTier(ItemStack stack) {
        // For each option we keep track of the amount it takes to craft the stack given
        Map<ItemStack, Integer> itemOptions = Maps.newLinkedHashMap();

        ComparisonCraftingContainer craftingContainer = new ComparisonCraftingContainer(1);
        for (CraftingRecipe craftingRecipe : level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            ItemStack output = craftingRecipe.getResultItem(level.registryAccess());
            // If the output is not this item check the next recipe
            if (!ItemStack.isSameItemSameTags(stack, output))
                continue;

            // Look for a match
            ItemStack match = tryMatch(stack, craftingRecipe.getIngredients());
            if (!match.isEmpty()) {
                int recipeSize = craftingRecipe.getIngredients().size();
                if (stack.is(StorageTags.Items.CRAFTING_OVERRIDE)) {
                    itemOptions.put(match, recipeSize);
                }

                // Look through each recipe to return a list of stacks that match the output
                // item
                craftingContainer.fillInventory(output);
                List<ItemStack> matchStacks = findMatchingStacks(craftingContainer);
                for (ItemStack matchStack : matchStacks) {
                    if (ItemStack.isSameItemSameTags(match, matchStack) && matchStack.getCount() == recipeSize) {
                        itemOptions.put(match, recipeSize);
                        break;
                    }
                }
            }
        }
        // Look for same items given the list of options we have generated
        ItemStack same = findSameItems(stack, itemOptions.keySet().stream().collect(Collectors.toList()));
        if (!same.isEmpty()) {
            return new Match(same, itemOptions.get(same));
        }
        if (itemOptions.size() > 0) {
            // If we could not find any similar items then lets return the first option
            Entry<ItemStack, Integer> firstOption = itemOptions.entrySet().iterator().next();
            return new Match(firstOption.getKey(), firstOption.getValue());
        }

        // Fallback
        return new Match(ItemStack.EMPTY, 0);
    }

    private List<ItemStack> findMatchingStacks(CraftingContainer crafting) {
        return level.getRecipeManager().getRecipesFor(RecipeType.CRAFTING, crafting, level).stream().filter(x -> x.matches(crafting, level)).map(r -> r.assemble(crafting, level.registryAccess())).filter(i -> !i.isEmpty()).collect(Collectors.toList());
    }

    private ItemStack findSameItems(ItemStack stack, List<ItemStack> ingredientItems) {
        ILoaderRegistry<Item> itemRegistry = Services.PLATFORM.getRegistry(Registries.ITEM);
        ResourceLocation stackKey = itemRegistry.getRegistryName(stack.getItem());
        if (stackKey != null) {
            ItemStack firstMatch = ingredientItems.stream().filter(x -> {
                ResourceLocation optionKey = itemRegistry.getRegistryName(x.getItem());
                return optionKey != null && stackKey.getNamespace().equals(optionKey.getNamespace());
            }).findFirst().orElse(ItemStack.EMPTY);

            if (firstMatch != ItemStack.EMPTY) {
                return firstMatch;
            }
        }
        return ingredientItems.size() > 0 ? ingredientItems.get(0) : ItemStack.EMPTY;
    }

    private ItemStack tryMatch(ItemStack stack, NonNullList<Ingredient> ingredients) {
        if (ingredients.size() != 9 && ingredients.size() != 4)
            return ItemStack.EMPTY;

        Ingredient refIngredient = ingredients.get(0);
        ItemStack[] refMatchingStacks = refIngredient.getItems();
        if (refMatchingStacks.length == 0)
            return ItemStack.EMPTY;

        for (int i = 1; i < ingredients.size(); i++) {
            Ingredient curIngredient = ingredients.get(i);

            boolean hasMatch = Arrays.stream(refMatchingStacks).anyMatch(x -> !x.isEmpty() && curIngredient.test(x));

            if (!hasMatch) {
                return ItemStack.EMPTY;
            }
        }

        ItemStack match = findSameItems(stack, Arrays.asList(refMatchingStacks));
        return match.isEmpty() ? refMatchingStacks[0] : match;
    }

    private static class ComparisonCraftingContainer extends TransientCraftingContainer {

        public ComparisonCraftingContainer(int size) {
            this(size, ItemStack.EMPTY);
        }

        public ComparisonCraftingContainer(int size, ItemStack stack) {
            super(new AbstractContainerMenu(null, 0) {
                @Override
                public boolean stillValid(Player playerIn) {
                    return false;
                }

                @Override
                public ItemStack quickMoveStack(Player player, int slot) {
                    return null;
                }
            }, size, size);

            this.fillInventory(stack);
        }

        protected void fillInventory(ItemStack stack) {
            for (int i = 0; i < this.getWidth() * this.getHeight(); i++) {
                this.setItem(i, stack.copy());
            }
        }
    }

    public static class Match {
        private final ItemStack item;
        // This is the number of required items based off of the lowest tier
        private int numRequired;

        public Match(ItemStack item, int numRequired) {
            this.item = item;
            this.numRequired = numRequired;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getNumRequired() {
            return numRequired;
        }

        public void setNumRequired(int numRequired) {
            this.numRequired = numRequired;
        }

        @Override
        public String toString() {
            return this.item.getDescriptionId() + ", " + this.numRequired;
        }
    }

}
