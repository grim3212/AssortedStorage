package com.grim3212.assorted.storage.common.loot;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;

public class StorageLootEntries {

    public static final RegistryProvider<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES = RegistryProvider.create(Registries.LOOT_POOL_ENTRY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<LootPoolEntryType> OPTIONAL_ITEM = LOOT_POOL_ENTRY_TYPES.register("optional_item", () -> new LootPoolEntryType(new OptionalLootItem.Serializer()));

    public static void init() {
    }
}
