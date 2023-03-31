package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class LockedShulkerBoxColoring extends CustomRecipe {

    public static final SimpleCraftingRecipeSerializer<LockedShulkerBoxColoring> SERIALIZER = new SimpleCraftingRecipeSerializer<>(LockedShulkerBoxColoring::new);

    public LockedShulkerBoxColoring(ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
    }

    public boolean matches(CraftingContainer container, Level level) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < container.getContainerSize(); ++k) {
            ItemStack itemstack = container.getItem(k);
            if (!itemstack.isEmpty()) {
                if (Block.byItem(itemstack.getItem()) instanceof LockedShulkerBoxBlock) {
                    ++i;
                } else {
                    if (!itemstack.is(LibCommonTags.Items.DYES)) {
                        return false;
                    }

                    ++j;
                }

                if (j > 1 || i > 1) {
                    return false;
                }
            }
        }

        return i == 1 && j == 1;
    }

    public ItemStack assemble(CraftingContainer container) {
        ItemStack itemstack = ItemStack.EMPTY;
        DyeColor dyecolor = DyeColor.WHITE;

        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemstack1 = container.getItem(i);
            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();
                if (Block.byItem(item) instanceof LockedShulkerBoxBlock) {
                    itemstack = itemstack1;
                } else {
                    DyeColor tmp = DyeHelper.getColor(itemstack1);
                    if (tmp != null)
                        dyecolor = tmp;
                }
            }
        }

        ItemStack copy = itemstack.copyWithCount(1);
        NBTHelper.putInt(copy, "Color", dyecolor.getId());
        return copy;
    }

    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
