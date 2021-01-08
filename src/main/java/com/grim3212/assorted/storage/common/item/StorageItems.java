package com.grim3212.assorted.storage.common.item;

import java.util.function.Supplier;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AssortedStorage.MODID);

	public static final RegistryObject<PadlockItem> LOCKSMITH_LOCK = register("locksmith_lock", () -> new PadlockItem(new Item.Properties().group(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP)));
	public static final RegistryObject<CombinationItem> LOCKSMITH_KEY = register("locksmith_key", () -> new CombinationItem(new Item.Properties().group(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP)));

	private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return ITEMS.register(name, sup);
	}
}
