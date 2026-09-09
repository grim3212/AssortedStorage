package com.grim3212.assorted.storage.common.loot;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.core.registries.Registries;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public class StorageLootEntries {

    // The loot entry registry holds the MapCodec itself in 26.x - LootPoolEntryType is gone.
    public static final RegistryProvider<MapCodec<? extends LootPoolEntryContainer>> LOOT_POOL_ENTRY_TYPES = RegistryProvider.create(Registries.LOOT_POOL_ENTRY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<MapCodec<? extends LootPoolEntryContainer>> OPTIONAL_ITEM = LOOT_POOL_ENTRY_TYPES.register("optional_item", () -> OptionalLootItem.MAP_CODEC);

    public static void init() {
    }
}
