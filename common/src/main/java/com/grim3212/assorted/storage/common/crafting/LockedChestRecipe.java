package com.grim3212.assorted.storage.common.crafting;

import com.mojang.serialization.MapCodec;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class LockedChestRecipe extends CustomRecipe {

    public static final LockedChestRecipe INSTANCE = new LockedChestRecipe();
    public static final MapCodec<LockedChestRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LockedChestRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<LockedChestRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        ItemStack enderChest = ItemStack.EMPTY;
        ItemStack lock = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty())
                continue;
            Item item = stack.getItem();
            if (item instanceof AirItem)
                continue;
            if (stack.is(LibCommonTags.Items.CHESTS_WOODEN) && enderChest.isEmpty())
                enderChest = stack;
            else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
                lock = stack;
            else
                return false;
        }
        return !enderChest.isEmpty() && !lock.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        ItemStack enderChest = ItemStack.EMPTY;
        ItemStack lock = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (stack.is(LibCommonTags.Items.CHESTS_WOODEN) && enderChest.isEmpty())
                enderChest = stack;
            else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
                lock = stack;
        }

        if (enderChest.isEmpty() || lock.isEmpty())
            return ItemStack.EMPTY;

        String lockCode = StorageUtil.getCode(lock);
        return StorageUtil.setCodeOnStack(lockCode, new ItemStack(StorageBlocks.LOCKED_CHEST.get()));
    }


    @Override
    public RecipeSerializer<LockedChestRecipe> getSerializer() {
        return SERIALIZER;
    }

}
