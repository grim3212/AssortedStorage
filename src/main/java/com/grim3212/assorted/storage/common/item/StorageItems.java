package com.grim3212.assorted.storage.common.item;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.item.upgrades.AmountUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.BasicCrateUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.RedstoneUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.VoidUpgradeItem;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageItems {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AssortedStorage.MODID);

	public static final RegistryObject<PadlockItem> LOCKSMITH_LOCK = register("locksmith_lock", () -> new PadlockItem(new Item.Properties()));
	public static final RegistryObject<CombinationItem> LOCKSMITH_KEY = register("locksmith_key", () -> new CombinationItem(new Item.Properties()));
	public static final RegistryObject<KeyRingItem> KEY_RING = register("key_ring", () -> new KeyRingItem(new Item.Properties()));
	public static final RegistryObject<Item> BLANK_UPGRADE = register("blank_upgrade", () -> new Item(new Item.Properties()));
	public static final RegistryObject<EnderBagItem> ENDER_BAG = register("ender_bag", () -> new EnderBagItem(new Item.Properties()));
	public static final RegistryObject<BagItem> BAG = register("bag", () -> new BagItem(new Item.Properties(), null));

	public static final RegistryObject<RotatorMajigItem> ROTATOR_MAJIG = register("rotator_majig", () -> new RotatorMajigItem());

	public static final RegistryObject<VoidUpgradeItem> VOID_UPGRADE = register("void_upgrade", () -> new VoidUpgradeItem(new Item.Properties().stacksTo(16)));
	public static final RegistryObject<AmountUpgradeItem> AMOUNT_UPGRADE = register("amount_upgrade", () -> new AmountUpgradeItem(new Item.Properties().stacksTo(16)));
	public static final RegistryObject<RedstoneUpgradeItem> REDSTONE_UPGRADE = register("redstone_upgrade", () -> new RedstoneUpgradeItem(new Item.Properties().stacksTo(16)));
	public static final RegistryObject<BasicCrateUpgradeItem> GLOW_UPGRADE = register("glow_upgrade", () -> new BasicCrateUpgradeItem(new Item.Properties().stacksTo(16)));

	public static final Map<StorageMaterial, RegistryObject<LevelUpgradeItem>> LEVEL_UPGRADES = Maps.newHashMap();
	public static final Map<StorageMaterial, RegistryObject<BagItem>> BAGS = Maps.newHashMap();

	static {
		Stream.of(StorageMaterial.values()).forEach((type) -> BAGS.put(type, register("bag_" + type.toString(), () -> new BagItem(new Item.Properties(), type))));
		Stream.of(StorageMaterial.values()).forEach((type) -> LEVEL_UPGRADES.put(type, register("level_upgrade_" + type.toString(), () -> new LevelUpgradeItem(new Item.Properties(), type))));
	}

	private static <T extends Item> RegistryObject<T> register(final String name, final Supplier<T> sup) {
		return ITEMS.register(name, sup);
	}
}
