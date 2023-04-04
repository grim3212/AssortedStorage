package com.grim3212.assorted.storage.common.inventory;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.inventory.bag.BagContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateCompactingContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import java.util.Map;
import java.util.stream.Stream;

public class StorageContainerTypes {
    public static final RegistryProvider<MenuType<?>> CONTAINERS = RegistryProvider.create(Registries.MENU, Constants.MOD_ID);

    public static final IRegistryObject<MenuType<StorageContainer>> WOOD_CABINET = CONTAINERS.register("wood_cabinet", () -> Services.PLATFORM.createMenuType(StorageContainer::createWoodCabinetContainer));
    public static final IRegistryObject<MenuType<StorageContainer>> GLASS_CABINET = CONTAINERS.register("glass_cabinet", () -> Services.PLATFORM.createMenuType(StorageContainer::createGlassCabinetContainer));
    public static final IRegistryObject<MenuType<StorageContainer>> WAREHOUSE_CRATE = CONTAINERS.register("warehouse_crate", () -> Services.PLATFORM.createMenuType(StorageContainer::createWarehouseCrateContainer));
    public static final IRegistryObject<MenuType<StorageContainer>> GOLD_SAFE = CONTAINERS.register("gold_safe", () -> Services.PLATFORM.createMenuType(StorageContainer::createGoldSafeContainer));
    public static final IRegistryObject<MenuType<StorageContainer>> OBSIDIAN_SAFE = CONTAINERS.register("obsidian_safe", () -> Services.PLATFORM.createMenuType(StorageContainer::createObsidianSafeContainer));
    public static final IRegistryObject<MenuType<LockerContainer>> LOCKER = CONTAINERS.register("locker", () -> Services.PLATFORM.createMenuType(LockerContainer::createLockerContainer));
    public static final IRegistryObject<MenuType<LockerContainer>> DUAL_LOCKER = CONTAINERS.register("dual_locker", () -> Services.PLATFORM.createMenuType(LockerContainer::createDualLockerContainer));
    public static final IRegistryObject<MenuType<ItemTowerContainer>> ITEM_TOWER = CONTAINERS.register("item_tower", () -> Services.PLATFORM.createMenuType(ItemTowerContainer::create));
    public static final IRegistryObject<MenuType<LocksmithWorkbenchContainer>> LOCKSMITH_WORKBENCH = CONTAINERS.register("locksmith_workbench", () -> Services.PLATFORM.createMenuType(LocksmithWorkbenchContainer::createContainer));
    public static final IRegistryObject<MenuType<KeyRingContainer>> KEY_RING = CONTAINERS.register("key_ring", () -> Services.PLATFORM.createMenuType(KeyRingContainer::new));
    public static final IRegistryObject<MenuType<StorageContainer>> LOCKED_ENDER_CHEST = CONTAINERS.register("locked_ender_chest", () -> Services.PLATFORM.createMenuType(StorageContainer::createEnderChestContainer));
    public static final IRegistryObject<MenuType<BagContainer>> BAG = CONTAINERS.register("bag", () -> Services.PLATFORM.createMenuType(BagContainer::new));
    public static final IRegistryObject<MenuType<EnderBagContainer>> ENDER_BAG = CONTAINERS.register("ender_bag", () -> Services.PLATFORM.createMenuType(EnderBagContainer::new));
    public static final IRegistryObject<MenuType<CrateContainer>> CRATE = CONTAINERS.register("crate", () -> Services.PLATFORM.createMenuType(CrateContainer::createCrateContainer));
    public static final IRegistryObject<MenuType<CrateCompactingContainer>> CRATE_COMPACTING = CONTAINERS.register("crate_compacting", () -> Services.PLATFORM.createMenuType(CrateCompactingContainer::createCrateContainer));

    public static final Map<StorageMaterial, IRegistryObject<MenuType<LockedMaterialContainer>>> CHESTS = Maps.newHashMap();
    public static final Map<StorageMaterial, IRegistryObject<MenuType<LockedMaterialContainer>>> SHULKERS = Maps.newHashMap();
    public static final Map<StorageMaterial, IRegistryObject<MenuType<LockedMaterialContainer>>> BARRELS = Maps.newHashMap();
    public static final Map<StorageMaterial, IRegistryObject<MenuType<LockedHopperContainer>>> HOPPERS = Maps.newHashMap();

    static {
        CHESTS.put(null, CONTAINERS.register("locked_chest", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedMaterialContainer.createChestContainer(syncId, inv, null))));
        SHULKERS.put(null, CONTAINERS.register("locked_shulker_box", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedMaterialContainer.createShulkerContainer(syncId, inv, null))));
        BARRELS.put(null, CONTAINERS.register("locked_barrel", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedMaterialContainer.createBarrelContainer(syncId, inv, null))));
        HOPPERS.put(null, CONTAINERS.register("locked_hopper", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedHopperContainer.createHopperContainer(syncId, inv, null))));

        Stream.of(StorageMaterial.values()).forEach((type) -> {
            CHESTS.put(type, CONTAINERS.register(type.toString() + "_locked_chest", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedMaterialContainer.createChestContainer(syncId, inv, type))));
            BARRELS.put(type, CONTAINERS.register(type.toString() + "_locked_barrel", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedMaterialContainer.createBarrelContainer(syncId, inv, type))));
            SHULKERS.put(type, CONTAINERS.register(type.toString() + "_locked_shulker_box", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedMaterialContainer.createShulkerContainer(syncId, inv, type))));
            HOPPERS.put(type, CONTAINERS.register(type.toString() + "_locked_hopper", () -> Services.PLATFORM.createMenuType((syncId, inv, c) -> LockedHopperContainer.createHopperContainer(syncId, inv, type))));
        });
    }
}
