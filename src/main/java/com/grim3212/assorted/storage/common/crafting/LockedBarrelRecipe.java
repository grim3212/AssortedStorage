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

public class LockedBarrelRecipe extends CustomRecipe {

	public static final SimpleRecipeSerializer<LockedBarrelRecipe> SERIALIZER = new SimpleRecipeSerializer<>(LockedBarrelRecipe::new);

	public LockedBarrelRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		ItemStack barrel = ItemStack.EMPTY;
		ItemStack lock = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty())
				continue;
			Item item = stack.getItem();
			if (item instanceof AirItem)
				continue;
			if (stack.is(Tags.Items.BARRELS_WOODEN) && barrel.isEmpty())
				barrel = stack;
			else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
				lock = stack;
			else
				return false;
		}
		return !barrel.isEmpty() && !lock.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		ItemStack barrel = ItemStack.EMPTY;
		ItemStack lock = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();
			if (stack.is(Tags.Items.BARRELS_WOODEN) && barrel.isEmpty())
				barrel = stack;
			else if (item == StorageItems.LOCKSMITH_LOCK.get() && lock.isEmpty() && StorageUtil.hasCode(stack))
				lock = stack;
		}

		if (barrel.isEmpty() || lock.isEmpty())
			return ItemStack.EMPTY;

		String lockCode = StorageUtil.getCode(lock);
		return StorageUtil.setCodeOnStack(lockCode, new ItemStack(StorageBlocks.LOCKED_BARREL.get()));
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
