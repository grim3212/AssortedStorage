package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.StorageTags;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class LockedShulkerBoxRecipe extends CustomRecipe {

    public static final SimpleCraftingRecipeSerializer<LockedShulkerBoxRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(LockedShulkerBoxRecipe::new);

    public LockedShulkerBoxRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        ItemStack shulker = ItemStack.EMPTY;
        ItemStack lock = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty())
                continue;
            Item item = stack.getItem();
            if (item instanceof AirItem)
                continue;
            if (stack.is(StorageTags.Items.SHULKERS_NORMAL) && shulker.isEmpty())
                shulker = stack;
            else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
                lock = stack;
            else
                return false;
        }
        return !shulker.isEmpty() && !lock.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack shulker = ItemStack.EMPTY;
        ItemStack lock = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (stack.is(StorageTags.Items.SHULKERS_NORMAL) && shulker.isEmpty())
                shulker = stack;
            else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
                lock = stack;
        }

        if (shulker.isEmpty() || lock.isEmpty())
            return ItemStack.EMPTY;

        DyeColor color = ShulkerBoxBlock.getColorFromItem(shulker.getItem());

        String lockCode = StorageUtil.getCode(lock);
        ItemStack output = new ItemStack(StorageBlocks.LOCKED_SHULKER_BOX.get());
        if (shulker.hasTag()) {
            output.setTag(shulker.getTag().copy());
        }

        StorageUtil.writeCodeToStack(lockCode, output);
        NBTHelper.putInt(output, "Color", color == null ? -1 : color.getId());
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

}
