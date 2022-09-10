package com.grim3212.assorted.storage.common.crafting;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageRecipeSerializers {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AssortedStorage.MODID);

	public static final RegistryObject<SimpleRecipeSerializer<LockedEnderChestRecipe>> LOCKED_ENDER_CHEST = register("locked_ender_chest", () -> LockedEnderChestRecipe.SERIALIZER);
	public static final RegistryObject<SimpleRecipeSerializer<LockedChestRecipe>> LOCKED_CHEST = register("locked_chest", () -> LockedChestRecipe.SERIALIZER);
	public static final RegistryObject<SimpleRecipeSerializer<LockedShulkerBoxRecipe>> LOCKED_SHULKER_BOX = register("locked_shulker_box", () -> LockedShulkerBoxRecipe.SERIALIZER);
	public static final RegistryObject<SimpleRecipeSerializer<LockedShulkerBoxColoring>> SHULKER_BOX_COLORING = register("shulker_box_coloring", () -> LockedShulkerBoxColoring.SERIALIZER);
	public static final RegistryObject<RecipeSerializer<LockedUpgradingRecipe>> LOCKED_UPGRADING = register("locked_upgrading", () -> new LockedUpgradingRecipe.Serializer());

	private static <T extends RecipeSerializer<?>> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return RECIPES.register(name, sup);
	}

}
