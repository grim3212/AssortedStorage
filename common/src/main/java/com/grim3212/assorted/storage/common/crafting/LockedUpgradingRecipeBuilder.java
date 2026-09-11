package com.grim3212.assorted.storage.common.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Builds a {@link LockedUpgradingRecipe} for data generation. The result is an
 * {@link ItemStackTemplate} because a stack cannot be built before item components are bound.
 */
public class LockedUpgradingRecipeBuilder implements RecipeBuilder {
    private final HolderGetter<Item> items;
    private final ItemStackTemplate result;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    @Nullable
    private String group;

    private LockedUpgradingRecipeBuilder(HolderGetter<Item> items, ItemLike result, int count) {
        this.items = items;
        this.result = new ItemStackTemplate(result.asItem(), count);
    }

    public static LockedUpgradingRecipeBuilder shaped(HolderGetter<Item> items, ItemLike result) {
        return shaped(items, result, 1);
    }

    public static LockedUpgradingRecipeBuilder shaped(HolderGetter<Item> items, ItemLike result, int count) {
        return new LockedUpgradingRecipeBuilder(items, result, count);
    }

    public LockedUpgradingRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(this.items.getOrThrow(tag)));
    }

    public LockedUpgradingRecipeBuilder define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    public LockedUpgradingRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }

        this.key.put(symbol, ingredient);
        return this;
    }

    public LockedUpgradingRecipeBuilder pattern(String row) {
        if (!this.rows.isEmpty() && row.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }

        this.rows.add(row);
        return this;
    }

    @Override
    public LockedUpgradingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.advancementBuilder.unlockedBy(name, criterion);
        return this;
    }

    @Override
    public LockedUpgradingRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.key, this.rows);
        LockedUpgradingRecipe recipe = new LockedUpgradingRecipe(
                RecipeBuilder.createCraftingCommonInfo(true),
                RecipeBuilder.createCraftingBookInfo(RecipeCategory.MISC, this.group),
                pattern,
                this.result);
        output.accept(id, recipe, this.advancementBuilder.build(output, id, RecipeCategory.MISC));
    }
}
