package com.grim3212.assorted.storage.common.crafting;

import com.mojang.serialization.MapCodec;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class LockedShulkerBoxColoring extends CustomRecipe {

    public static final LockedShulkerBoxColoring INSTANCE = new LockedShulkerBoxColoring();
    public static final MapCodec<LockedShulkerBoxColoring> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LockedShulkerBoxColoring> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<LockedShulkerBoxColoring> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public boolean matches(CraftingInput container, Level level) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < container.size(); ++k) {
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

    @Override
    public ItemStack assemble(CraftingInput container) {
        ItemStack itemstack = ItemStack.EMPTY;
        DyeColor dyecolor = DyeColor.WHITE;

        for (int i = 0; i < container.size(); ++i) {
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


    public RecipeSerializer<LockedShulkerBoxColoring> getSerializer() {
        return SERIALIZER;
    }

}
