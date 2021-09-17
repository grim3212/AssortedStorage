package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class LockedEnderChestRecipe extends SpecialRecipe {

	public static final SpecialRecipeSerializer<LockedEnderChestRecipe> SERIALIZER = new SpecialRecipeSerializer<>(LockedEnderChestRecipe::new);

	public LockedEnderChestRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		ItemStack enderChest = ItemStack.EMPTY;
		ItemStack lock = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty())
				continue;
			Item item = stack.getItem();
			if (item instanceof AirItem)
				continue;
			if (item == Blocks.ENDER_CHEST.asItem() && enderChest.isEmpty())
				enderChest = stack;
			else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
				lock = stack;
			else
				return false;
		}
		return !enderChest.isEmpty() && !lock.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		ItemStack enderChest = ItemStack.EMPTY;
		ItemStack lock = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();
			if (item == Blocks.ENDER_CHEST.asItem() && enderChest.isEmpty())
				enderChest = stack;
			else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
				lock = stack;
		}

		if (enderChest.isEmpty() || lock.isEmpty())
			return ItemStack.EMPTY;

		String lockCode = StorageUtil.getCode(lock);
		return StorageUtil.setCodeOnStack(lockCode, new ItemStack(StorageBlocks.LOCKED_ENDER_CHEST.get()));
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

}
