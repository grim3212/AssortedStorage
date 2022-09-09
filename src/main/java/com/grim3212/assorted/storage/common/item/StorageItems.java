package com.grim3212.assorted.storage.common.item;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.handler.EnabledCondition;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AssortedStorage.MODID);

	public static final RegistryObject<PadlockItem> LOCKSMITH_LOCK = register("locksmith_lock", () -> new PadlockItem(new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP)));
	public static final RegistryObject<CombinationItem> LOCKSMITH_KEY = register("locksmith_key", () -> new CombinationItem(new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP)));
	public static final RegistryObject<KeyRingItem> KEY_RING = register("key_ring", () -> new KeyRingItem(new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP)));
	public static final RegistryObject<EnabledItem> BLANK_UPGRADE = register("blank_upgrade", () -> new EnabledItem(EnabledCondition.UPGRADES_CONDITION, new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP)));

	public static final Map<StorageMaterial, RegistryObject<LevelUpgradeItem>> LEVEL_UPGRADES = Maps.newHashMap();

	static {
		Stream.of(StorageMaterial.values()).forEach((type) -> LEVEL_UPGRADES.put(type, register("level_upgrade_" + type.toString(), () -> new LevelUpgradeItem(new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP), type))));
	}

	private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return ITEMS.register(name, sup);
	}
}
