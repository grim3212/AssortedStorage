package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageContainerTypes {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, AssortedStorage.MODID);

	public static final RegistryObject<MenuType<StorageContainer>> WOOD_CABINET = CONTAINERS.register("wood_cabinet", () -> new MenuType<>(StorageContainer::createWoodCabinetContainer));
	public static final RegistryObject<MenuType<StorageContainer>> GLASS_CABINET = CONTAINERS.register("glass_cabinet", () -> new MenuType<>(StorageContainer::createGlassCabinetContainer));
	public static final RegistryObject<MenuType<StorageContainer>> WAREHOUSE_CRATE = CONTAINERS.register("warehouse_crate", () -> new MenuType<>(StorageContainer::createWarehouseCrateContainer));
	public static final RegistryObject<MenuType<StorageContainer>> GOLD_SAFE = CONTAINERS.register("gold_safe", () -> new MenuType<>(StorageContainer::createGoldSafeContainer));
	public static final RegistryObject<MenuType<StorageContainer>> OBSIDIAN_SAFE = CONTAINERS.register("obsidian_safe", () -> new MenuType<>(StorageContainer::createObsidianSafeContainer));
	public static final RegistryObject<MenuType<LockerContainer>> LOCKER = CONTAINERS.register("locker", () -> new MenuType<>(LockerContainer::createLockerContainer));
	public static final RegistryObject<MenuType<LockerContainer>> DUAL_LOCKER = CONTAINERS.register("dual_locker", () -> new MenuType<>(LockerContainer::createDualLockerContainer));
	public static final RegistryObject<MenuType<ItemTowerContainer>> ITEM_TOWER = CONTAINERS.register("item_tower", () -> new MenuType<>(new ItemTowerContainer.ItemTowerContainerFactory<>()));
	public static final RegistryObject<MenuType<LocksmithWorkbenchContainer>> LOCKSMITH_WORKBENCH = CONTAINERS.register("locksmith_workbench", () -> new MenuType<>(LocksmithWorkbenchContainer::createContainer));
}
