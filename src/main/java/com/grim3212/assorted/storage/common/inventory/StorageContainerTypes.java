package com.grim3212.assorted.storage.common.inventory;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.bag.BagContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateCompactingContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageContainerTypes {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AssortedStorage.MODID);

	public static final RegistryObject<MenuType<StorageContainer>> WOOD_CABINET = CONTAINERS.register("wood_cabinet", () -> new MenuType<>(StorageContainer::createWoodCabinetContainer));
	public static final RegistryObject<MenuType<StorageContainer>> GLASS_CABINET = CONTAINERS.register("glass_cabinet", () -> new MenuType<>(StorageContainer::createGlassCabinetContainer));
	public static final RegistryObject<MenuType<StorageContainer>> WAREHOUSE_CRATE = CONTAINERS.register("warehouse_crate", () -> new MenuType<>(StorageContainer::createWarehouseCrateContainer));
	public static final RegistryObject<MenuType<StorageContainer>> GOLD_SAFE = CONTAINERS.register("gold_safe", () -> new MenuType<>(StorageContainer::createGoldSafeContainer));
	public static final RegistryObject<MenuType<StorageContainer>> OBSIDIAN_SAFE = CONTAINERS.register("obsidian_safe", () -> new MenuType<>(StorageContainer::createObsidianSafeContainer));
	public static final RegistryObject<MenuType<LockerContainer>> LOCKER = CONTAINERS.register("locker", () -> new MenuType<>(LockerContainer::createLockerContainer));
	public static final RegistryObject<MenuType<LockerContainer>> DUAL_LOCKER = CONTAINERS.register("dual_locker", () -> new MenuType<>(LockerContainer::createDualLockerContainer));
	public static final RegistryObject<MenuType<ItemTowerContainer>> ITEM_TOWER = CONTAINERS.register("item_tower", () -> new MenuType<>(new ItemTowerContainer.ItemTowerContainerFactory<>()));
	public static final RegistryObject<MenuType<LocksmithWorkbenchContainer>> LOCKSMITH_WORKBENCH = CONTAINERS.register("locksmith_workbench", () -> new MenuType<>(LocksmithWorkbenchContainer::createContainer));
	public static final RegistryObject<MenuType<KeyRingContainer>> KEY_RING = CONTAINERS.register("key_ring", () -> IForgeMenuType.create(KeyRingContainer::new));
	public static final RegistryObject<MenuType<StorageContainer>> LOCKED_ENDER_CHEST = CONTAINERS.register("locked_ender_chest", () -> new MenuType<>(StorageContainer::createEnderChestContainer));
	public static final RegistryObject<MenuType<BagContainer>> BAG = CONTAINERS.register("bag", () -> IForgeMenuType.create(BagContainer::new));
	public static final RegistryObject<MenuType<EnderBagContainer>> ENDER_BAG = CONTAINERS.register("ender_bag", () -> IForgeMenuType.create(EnderBagContainer::new));

	public static final Map<StorageMaterial, RegistryObject<MenuType<LockedMaterialContainer>>> CHESTS = Maps.newHashMap();
	public static final Map<StorageMaterial, RegistryObject<MenuType<LockedMaterialContainer>>> SHULKERS = Maps.newHashMap();
	public static final Map<StorageMaterial, RegistryObject<MenuType<LockedMaterialContainer>>> BARRELS = Maps.newHashMap();
	public static final Map<StorageMaterial, RegistryObject<MenuType<LockedHopperContainer>>> HOPPERS = Maps.newHashMap();
	public static final Map<StorageMaterial, RegistryObject<MenuType<CrateContainer>>> CRATES = Maps.newHashMap();
	public static final Map<StorageMaterial, RegistryObject<MenuType<CrateCompactingContainer>>> CRATES_COMPACTING = Maps.newHashMap();

	static {
		CHESTS.put(null, CONTAINERS.register("locked_chest", () -> IForgeMenuType.create((syncId, inv, c) -> LockedMaterialContainer.createChestContainer(syncId, inv, null))));
		SHULKERS.put(null, CONTAINERS.register("locked_shulker_box", () -> IForgeMenuType.create((syncId, inv, c) -> LockedMaterialContainer.createShulkerContainer(syncId, inv, null))));
		BARRELS.put(null, CONTAINERS.register("locked_barrel", () -> IForgeMenuType.create((syncId, inv, c) -> LockedMaterialContainer.createBarrelContainer(syncId, inv, null))));
		HOPPERS.put(null, CONTAINERS.register("locked_hopper", () -> IForgeMenuType.create((syncId, inv, c) -> LockedHopperContainer.createHopperContainer(syncId, inv, null))));
		CRATES.put(null, CONTAINERS.register("crate", () -> IForgeMenuType.create((syncId, inv, c) -> CrateContainer.createCrateContainer(syncId, inv, c))));
		CRATES_COMPACTING.put(null, CONTAINERS.register("crate_compacting", () -> IForgeMenuType.create((syncId, inv, c) -> CrateCompactingContainer.createCrateContainer(syncId, inv, c))));

		Stream.of(StorageMaterial.values()).forEach((type) -> {
			CHESTS.put(type, CONTAINERS.register(type.toString() + "_locked_chest", () -> IForgeMenuType.create((syncId, inv, c) -> LockedMaterialContainer.createChestContainer(syncId, inv, type))));
			BARRELS.put(type, CONTAINERS.register(type.toString() + "_locked_barrel", () -> IForgeMenuType.create((syncId, inv, c) -> LockedMaterialContainer.createBarrelContainer(syncId, inv, type))));
			SHULKERS.put(type, CONTAINERS.register(type.toString() + "_locked_shulker_box", () -> IForgeMenuType.create((syncId, inv, c) -> LockedMaterialContainer.createShulkerContainer(syncId, inv, type))));
			HOPPERS.put(type, CONTAINERS.register(type.toString() + "_locked_hopper", () -> IForgeMenuType.create((syncId, inv, c) -> LockedHopperContainer.createHopperContainer(syncId, inv, type))));
		});
	}
}
