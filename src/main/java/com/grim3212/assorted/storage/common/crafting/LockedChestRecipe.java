package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

public class LockedChestRecipe extends CustomRecipe {

	public static final SimpleRecipeSerializer<LockedChestRecipe> SERIALIZER = new SimpleRecipeSerializer<>(LockedChestRecipe::new);

	public LockedChestRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		ItemStack enderChest = ItemStack.EMPTY;
		ItemStack lock = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty())
				continue;
			Item item = stack.getItem();
			if (item instanceof AirItem)
				continue;
			if (stack.is(Tags.Items.CHESTS_WOODEN) && enderChest.isEmpty())
				enderChest = stack;
			else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
				lock = stack;
			else
				return false;
		}
		return !enderChest.isEmpty() && !lock.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		ItemStack enderChest = ItemStack.EMPTY;
		ItemStack lock = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();
			if (stack.is(Tags.Items.CHESTS_WOODEN) && enderChest.isEmpty())
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
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

}
