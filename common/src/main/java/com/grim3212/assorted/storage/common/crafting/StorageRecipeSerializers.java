package com.grim3212.assorted.storage.common.crafting;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class StorageRecipeSerializers {

    public static final RegistryProvider<RecipeSerializer<?>> RECIPES = RegistryProvider.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    public static final IRegistryObject<RecipeSerializer<LockedEnderChestRecipe>> LOCKED_ENDER_CHEST = register("locked_ender_chest", () -> LockedEnderChestRecipe.SERIALIZER);
    public static final IRegistryObject<RecipeSerializer<LockedChestRecipe>> LOCKED_CHEST = register("locked_chest", () -> LockedChestRecipe.SERIALIZER);
    public static final IRegistryObject<RecipeSerializer<LockedShulkerBoxRecipe>> LOCKED_SHULKER_BOX = register("locked_shulker_box", () -> LockedShulkerBoxRecipe.SERIALIZER);
    public static final IRegistryObject<RecipeSerializer<LockedShulkerBoxColoring>> SHULKER_BOX_COLORING = register("shulker_box_coloring", () -> LockedShulkerBoxColoring.SERIALIZER);
    public static final IRegistryObject<RecipeSerializer<LockedBarrelRecipe>> LOCKED_BARREL = register("locked_barrel", () -> LockedBarrelRecipe.SERIALIZER);
    public static final IRegistryObject<RecipeSerializer<LockedHopperRecipe>> LOCKED_HOPPER = register("locked_hopper", () -> LockedHopperRecipe.SERIALIZER);
    public static final IRegistryObject<RecipeSerializer<LockedUpgradingRecipe>> LOCKED_UPGRADING = register("locked_upgrading", () -> LockedUpgradingRecipe.SERIALIZER);

    public static final IRegistryObject<RecipeSerializer<BagColoringRecipe>> BAG_COLORING = register("bag_coloring", () -> BagColoringRecipe.SERIALIZER);

    private static <T extends RecipeSerializer<?>> IRegistryObject<T> register(final String name, final Supplier<T> sup) {
        return RECIPES.register(name, sup);
    }

    public static void init() {
    }
}
