package com.grim3212.assorted.storage.common.crafting;

import com.mojang.serialization.MapCodec;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.StorageTags;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.world.item.AirItem;
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
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class LockedShulkerBoxRecipe extends CustomRecipe {

    public static final LockedShulkerBoxRecipe INSTANCE = new LockedShulkerBoxRecipe();
    public static final MapCodec<LockedShulkerBoxRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, LockedShulkerBoxRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<LockedShulkerBoxRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        ItemStack shulker = ItemStack.EMPTY;
        ItemStack lock = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
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
    public ItemStack assemble(CraftingInput inv) {
        ItemStack shulker = ItemStack.EMPTY;
        ItemStack lock = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (stack.is(StorageTags.Items.SHULKERS_NORMAL) && shulker.isEmpty())
                shulker = stack;
            else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
                lock = stack;
        }

        if (shulker.isEmpty() || lock.isEmpty())
            return ItemStack.EMPTY;

        // ShulkerBoxBlock exposes its colour as an instance accessor now.
        DyeColor color = Block.byItem(shulker.getItem()) instanceof ShulkerBoxBlock shulkerBlock ? shulkerBlock.getColor() : null;

        String lockCode = StorageUtil.getCode(lock);
        ItemStack output = new ItemStack(StorageBlocks.LOCKED_SHULKER_BOX.get());
        // Stack NBT is gone; carrying the shulker's contents and name over means copying its data
        // component patch onto the locked one.
        output.applyComponents(shulker.getComponentsPatch());

        StorageUtil.writeCodeToStack(lockCode, output);
        NBTHelper.putInt(output, "Color", color == null ? -1 : color.getId());
        return output;
    }


    @Override
    public RecipeSerializer<LockedShulkerBoxRecipe> getSerializer() {
        return SERIALIZER;
    }

}
