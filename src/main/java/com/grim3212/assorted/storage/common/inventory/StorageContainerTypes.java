package com.grim3212.assorted.storage.common.inventory;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
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

	public static final Map<StorageMaterial, RegistryObject<MenuType<LockedChestContainer>>> CHESTS = Maps.newHashMap();

	static {
		Stream.of(StorageMaterial.values()).forEach((type) -> CHESTS.put(type, CONTAINERS.register(type.toString() + "_locked_chest", () -> IForgeMenuType.create((syncId, inv, c) -> new LockedChestContainer(syncId, inv, type)))));
	}
}
