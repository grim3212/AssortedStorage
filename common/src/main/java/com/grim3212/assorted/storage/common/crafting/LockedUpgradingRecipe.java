package com.grim3212.assorted.storage.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.item.BagItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.List;

/**
 * A shaped recipe that carries the data of the storage item being upgraded over to the result.
 * <p>
 * The 1.20.1 version was a hand copied {@code ShapedRecipe}, including its own pattern parsing,
 * shrinking and matching, because none of that was reusable. 26.x factored all of it out into
 * {@link ShapedRecipePattern}, so this is now just a {@link NormalCraftingRecipe} that delegates
 * the shape work and only keeps the interesting part: the result inherits the input's data
 * components rather than being a fresh stack.
 */
public class LockedUpgradingRecipe extends NormalCraftingRecipe {

    public static final MapCodec<LockedUpgradingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                    CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
                    ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
            ).apply(instance, LockedUpgradingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LockedUpgradingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, recipe -> recipe.bookInfo,
            ShapedRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
            ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
            LockedUpgradingRecipe::new);

    public static final RecipeSerializer<LockedUpgradingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate result;

    public LockedUpgradingRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo);
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    public RecipeSerializer<LockedUpgradingRecipe> getSerializer() {
        return StorageRecipeSerializers.LOCKED_UPGRADING.get();
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(this.pattern.ingredients());
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack output = this.result.create();

        ItemStack itemstack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);

            if (!stack.isEmpty()) {
                if (Block.byItem(stack.getItem()) instanceof LockedShulkerBoxBlock || Block.byItem(stack.getItem()) instanceof LockedChestBlock || stack.getItem() instanceof BagItem) {
                    itemstack = stack;
                } else if (Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock shulkerBlock) {
                    // ShulkerBoxBlock exposes its colour as an instance accessor now.
                    DyeColor dyeColor = shulkerBlock.getColor();
                    int color = dyeColor == null ? -1 : dyeColor.getId();
                    itemstack = NBTHelper.putIntItemStack(stack.copy(), "Color", color);
                }
            }
        }

        // Stack NBT is gone; carrying the upgraded item's stored contents, name and lock over is a
        // matter of applying its data component patch to the fresh result.
        if (!itemstack.isEmpty()) {
            output.applyComponents(itemstack.getComponentsPatch());
        }

        return output;
    }

    public ItemStackTemplate getResultTemplate() {
        return this.result;
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    public List<Ingredient> getIngredients() {
        return this.placementInfo().ingredients();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapedCraftingRecipeDisplay(this.pattern.width(), this.pattern.height(),
                this.pattern.ingredients().stream().map(ingredient -> ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }
}
