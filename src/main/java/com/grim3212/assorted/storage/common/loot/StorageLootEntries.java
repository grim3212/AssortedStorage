package com.grim3212.assorted.storage.common.loot;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StorageLootEntries {

	public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES = DeferredRegister.create(Registry.LOOT_POOL_ENTRY_TYPE.key(), AssortedStorage.MODID);

	public static final RegistryObject<LootPoolEntryType> OPTIONAL_ITEM = LOOT_POOL_ENTRY_TYPES.register("optional_item", () -> new LootPoolEntryType(new OptionalLootItem.Serializer()));

}
