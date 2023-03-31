package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.common.item.BagItem;
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

public class BagColoringRecipe extends CustomRecipe {

    public static final SimpleCraftingRecipeSerializer<BagColoringRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(BagColoringRecipe::new);

    public BagColoringRecipe(ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int i = 0;
        int bagXSlot = -1;

        int numDyes = 0;
        int firstDyeXSlot = -1;
        int secondDyeXSlot = -1;

        for (int y = 0; y < container.getHeight(); y++) {
            for (int x = 0; x < container.getWidth(); x++) {
                ItemStack itemstack = container.getItem(x + y * container.getWidth());
                if (!itemstack.isEmpty()) {
                    if (itemstack.getItem() instanceof BagItem) {
                        ++i;
                        bagXSlot = x;
                    } else {
                        if (!itemstack.is(LibCommonTags.Items.DYES)) {
                            return false;
                        }

                        if (firstDyeXSlot == -1) {
                            firstDyeXSlot = x;
                        } else if (secondDyeXSlot == -1) {
                            secondDyeXSlot = x;
                        }

                        ++numDyes;

                    }

                    if (numDyes > 2 || i > 1) {
                        return false;
                    }
                }

            }
        }

        // Lets make sure both possible dyes are not in the same x slot
        if (firstDyeXSlot < bagXSlot && secondDyeXSlot != -1 && secondDyeXSlot < bagXSlot) {
            return false;
        }

        if (firstDyeXSlot >= bagXSlot && secondDyeXSlot != -1 && secondDyeXSlot >= bagXSlot) {
            return false;
        }

        return i == 1 && (numDyes == 1 || numDyes == 2);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack itemstack = ItemStack.EMPTY;
        int bagXSlot = -1;

        DyeColor firstColor = null;
        int firstDyeXSlot = -1;
        DyeColor secondColor = null;
        int secondDyeXSlot = -1;

        for (int y = 0; y < container.getHeight(); y++) {
            for (int x = 0; x < container.getWidth(); x++) {
                ItemStack itemstack1 = container.getItem(x + y * container.getWidth());
                if (!itemstack1.isEmpty()) {
                    Item item = itemstack1.getItem();
                    if (item instanceof BagItem) {
                        itemstack = itemstack1;
                        bagXSlot = x;
                    } else {
                        DyeColor tmp = DyeHelper.getColor(itemstack1);
                        if (tmp != null) {
                            if (firstDyeXSlot == -1) {
                                firstColor = tmp;
                                firstDyeXSlot = x;
                            } else {
                                secondColor = tmp;
                                secondDyeXSlot = x;
                            }
                        }
                    }
                }
            }
        }

        ItemStack copy = itemstack.copy();
        if (firstColor != null) {
            if (firstDyeXSlot < bagXSlot) {
                NBTHelper.putInt(copy, BagItem.TAG_SECONDARY_COLOR, firstColor.getId());
            } else {
                NBTHelper.putInt(copy, BagItem.TAG_PRIMARY_COLOR, firstColor.getId());
            }
        }
        if (secondColor != null) {
            if (secondDyeXSlot < bagXSlot) {
                NBTHelper.putInt(copy, BagItem.TAG_SECONDARY_COLOR, secondColor.getId());
            } else {
                NBTHelper.putInt(copy, BagItem.TAG_PRIMARY_COLOR, secondColor.getId());
            }
        }

        return copy;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
