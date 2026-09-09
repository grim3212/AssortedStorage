package com.grim3212.assorted.storage.common.item;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.upgrades.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class StorageItems {


    public static final IRegistryObject<PadlockItem> LOCKSMITH_LOCK = register("locksmith_lock", key -> new PadlockItem(props(key)));
    public static final IRegistryObject<CombinationItem> LOCKSMITH_KEY = register("locksmith_key", key -> new CombinationItem(props(key)));
    public static final IRegistryObject<KeyRingItem> KEY_RING = register("key_ring", key -> new KeyRingItem(props(key)));
    public static final IRegistryObject<Item> BLANK_UPGRADE = register("blank_upgrade", key -> new Item(props(key)));
    public static final IRegistryObject<EnderBagItem> ENDER_BAG = register("ender_bag", key -> new EnderBagItem(props(key)));
    public static final IRegistryObject<BagItem> BAG = register("bag", key -> new BagItem(props(key), null));

    public static final IRegistryObject<RotatorMajigItem> ROTATOR_MAJIG = register("rotator_majig", key -> new RotatorMajigItem(props(key)));

    public static final IRegistryObject<VoidUpgradeItem> VOID_UPGRADE = register("void_upgrade", key -> new VoidUpgradeItem(props(key).stacksTo(16)));
    public static final IRegistryObject<AmountUpgradeItem> AMOUNT_UPGRADE = register("amount_upgrade", key -> new AmountUpgradeItem(props(key).stacksTo(16)));
    public static final IRegistryObject<RedstoneUpgradeItem> REDSTONE_UPGRADE = register("redstone_upgrade", key -> new RedstoneUpgradeItem(props(key).stacksTo(16)));
    public static final IRegistryObject<BasicCrateUpgradeItem> GLOW_UPGRADE = register("glow_upgrade", key -> new BasicCrateUpgradeItem(props(key).stacksTo(16)));

    public static final Map<StorageMaterial, IRegistryObject<LevelUpgradeItem>> LEVEL_UPGRADES = Maps.newHashMap();
    public static final Map<StorageMaterial, IRegistryObject<BagItem>> BAGS = Maps.newHashMap();

    static {
        Stream.of(StorageMaterial.values()).forEach((type) -> BAGS.put(type, register("bag_" + type.toString(), key -> new BagItem(props(key), type))));
        Stream.of(StorageMaterial.values()).forEach((type) -> LEVEL_UPGRADES.put(type, register("level_upgrade_" + type.toString(), key -> new LevelUpgradeItem(props(key), type))));
    }

    // Since 1.21.2 an item has to know its own registry id before it is constructed, so the
    // registration name is turned into a ResourceKey here and put on the properties. Without it
    // registration dies with "Item id not set"; StorageBlocks already does the same for blocks.
    private static Item.Properties props(ResourceKey<Item> key) {
        return new Item.Properties().setId(key);
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<ResourceKey<Item>, T> factory) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return StorageBlocks.ITEMS.register(name, () -> factory.apply(key));
    }

    public static void init() {

    }
}
