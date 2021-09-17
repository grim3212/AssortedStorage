package com.grim3212.assorted.storage.common.crafting;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageRecipeSerializers {

	public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AssortedStorage.MODID);

	public static final RegistryObject<SpecialRecipeSerializer<LockedEnderChestRecipe>> LOCKED_ENDER_CHEST = register("locked_ender_chest", () -> LockedEnderChestRecipe.SERIALIZER);

	private static <T extends IRecipeSerializer<?>> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return RECIPES.register(name, sup);
	}

}
