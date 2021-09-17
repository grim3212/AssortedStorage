package com.grim3212.assorted.storage.common.inventory;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageContainerTypes {
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, AssortedStorage.MODID);

	public static final RegistryObject<ContainerType<StorageContainer>> WOOD_CABINET = CONTAINERS.register("wood_cabinet", () -> new ContainerType<>(StorageContainer::createWoodCabinetContainer));
	public static final RegistryObject<ContainerType<StorageContainer>> GLASS_CABINET = CONTAINERS.register("glass_cabinet", () -> new ContainerType<>(StorageContainer::createGlassCabinetContainer));
	public static final RegistryObject<ContainerType<StorageContainer>> WAREHOUSE_CRATE = CONTAINERS.register("warehouse_crate", () -> new ContainerType<>(StorageContainer::createWarehouseCrateContainer));
	public static final RegistryObject<ContainerType<StorageContainer>> GOLD_SAFE = CONTAINERS.register("gold_safe", () -> new ContainerType<>(StorageContainer::createGoldSafeContainer));
	public static final RegistryObject<ContainerType<StorageContainer>> OBSIDIAN_SAFE = CONTAINERS.register("obsidian_safe", () -> new ContainerType<>(StorageContainer::createObsidianSafeContainer));
	public static final RegistryObject<ContainerType<LockerContainer>> LOCKER = CONTAINERS.register("locker", () -> new ContainerType<>(LockerContainer::createLockerContainer));
	public static final RegistryObject<ContainerType<LockerContainer>> DUAL_LOCKER = CONTAINERS.register("dual_locker", () -> new ContainerType<>(LockerContainer::createDualLockerContainer));
	public static final RegistryObject<ContainerType<ItemTowerContainer>> ITEM_TOWER = CONTAINERS.register("item_tower", () -> new ContainerType<>(new ItemTowerContainer.ItemTowerContainerFactory<>()));
	public static final RegistryObject<ContainerType<LocksmithWorkbenchContainer>> LOCKSMITH_WORKBENCH = CONTAINERS.register("locksmith_workbench", () -> new ContainerType<>(LocksmithWorkbenchContainer::createContainer));
	public static final RegistryObject<ContainerType<KeyRingContainer>> KEY_RING = CONTAINERS.register("key_ring", () -> IForgeContainerType.create(KeyRingContainer::new));
	public static final RegistryObject<ContainerType<StorageContainer>> LOCKED_ENDER_CHEST = CONTAINERS.register("locked_ender_chest", () -> new ContainerType<>(StorageContainer::createEnderChestContainer));
}
