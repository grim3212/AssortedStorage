package com.grim3212.assorted.storage.common.loot;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.core.registries.Registries;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class StorageLootConditions {

    // The loot condition registry holds the MapCodec itself in 26.x - LootItemConditionType is gone.
    public static final RegistryProvider<MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITIONS = RegistryProvider.create(Registries.LOOT_CONDITION_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<MapCodec<? extends LootItemCondition>> MOD_LOADED = LOOT_ITEM_CONDITIONS.register("mod_loaded", () -> ModLoadedLootCondition.MAP_CODEC);

    public static void init() {
    }
}
