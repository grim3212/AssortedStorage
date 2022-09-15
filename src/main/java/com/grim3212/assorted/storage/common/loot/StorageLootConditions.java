package com.grim3212.assorted.storage.common.loot;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class StorageLootConditions {

	public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, AssortedStorage.MODID);

	public static final RegistryObject<LootItemConditionType> MOD_LOADED = LOOT_ITEM_CONDITIONS.register("mod_loaded", () -> new LootItemConditionType(new ModLoadedLootCondition.ModLoadedLootConditionSerializer()));

}
