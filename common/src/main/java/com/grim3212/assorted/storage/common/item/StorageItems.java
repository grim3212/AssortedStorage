package com.grim3212.assorted.storage.common.item;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.upgrades.*;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StorageItems {


    public static final IRegistryObject<PadlockItem> LOCKSMITH_LOCK = register("locksmith_lock", () -> new PadlockItem(new Item.Properties()));
    public static final IRegistryObject<CombinationItem> LOCKSMITH_KEY = register("locksmith_key", () -> new CombinationItem(new Item.Properties()));
    public static final IRegistryObject<KeyRingItem> KEY_RING = register("key_ring", () -> new KeyRingItem(new Item.Properties()));
    public static final IRegistryObject<Item> BLANK_UPGRADE = register("blank_upgrade", () -> new Item(new Item.Properties()));
    public static final IRegistryObject<EnderBagItem> ENDER_BAG = register("ender_bag", () -> new EnderBagItem(new Item.Properties()));
    public static final IRegistryObject<BagItem> BAG = register("bag", () -> new BagItem(new Item.Properties(), null));

    public static final IRegistryObject<RotatorMajigItem> ROTATOR_MAJIG = register("rotator_majig", () -> new RotatorMajigItem());

    public static final IRegistryObject<VoidUpgradeItem> VOID_UPGRADE = register("void_upgrade", () -> new VoidUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final IRegistryObject<AmountUpgradeItem> AMOUNT_UPGRADE = register("amount_upgrade", () -> new AmountUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final IRegistryObject<RedstoneUpgradeItem> REDSTONE_UPGRADE = register("redstone_upgrade", () -> new RedstoneUpgradeItem(new Item.Properties().stacksTo(16)));
    public static final IRegistryObject<BasicCrateUpgradeItem> GLOW_UPGRADE = register("glow_upgrade", () -> new BasicCrateUpgradeItem(new Item.Properties().stacksTo(16)));

    public static final Map<StorageMaterial, IRegistryObject<LevelUpgradeItem>> LEVEL_UPGRADES = Maps.newHashMap();
    public static final Map<StorageMaterial, IRegistryObject<BagItem>> BAGS = Maps.newHashMap();

    static {
        Stream.of(StorageMaterial.values()).forEach((type) -> BAGS.put(type, register("bag_" + type.toString(), () -> new BagItem(new Item.Properties(), type))));
        Stream.of(StorageMaterial.values()).forEach((type) -> LEVEL_UPGRADES.put(type, register("level_upgrade_" + type.toString(), () -> new LevelUpgradeItem(new Item.Properties(), type))));
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Supplier<T> sup) {
        return StorageBlocks.ITEMS.register(name, sup);
    }

    public static void init() {

    }
}
