package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AssortedStorage.MODID);

	public static final RegistryObject<BlockEntityType<WoodCabinetBlockEntity>> WOOD_CABINET = BLOCK_ENTITIES.register("wood_cabinet", () -> new BlockEntityType<>(WoodCabinetBlockEntity::new, Sets.newHashSet(StorageBlocks.WOOD_CABINET.get()), null));
	public static final RegistryObject<BlockEntityType<GlassCabinetBlockEntity>> GLASS_CABINET = BLOCK_ENTITIES.register("glass_cabinet", () -> new BlockEntityType<>(GlassCabinetBlockEntity::new, Sets.newHashSet(StorageBlocks.GLASS_CABINET.get()), null));
	public static final RegistryObject<BlockEntityType<GoldSafeBlockEntity>> GOLD_SAFE = BLOCK_ENTITIES.register("gold_safe", () -> new BlockEntityType<>(GoldSafeBlockEntity::new, Sets.newHashSet(StorageBlocks.GOLD_SAFE.get()), null));
	public static final RegistryObject<BlockEntityType<ObsidianSafeBlockEntity>> OBSIDIAN_SAFE = BLOCK_ENTITIES.register("obsidian_safe", () -> new BlockEntityType<>(ObsidianSafeBlockEntity::new, Sets.newHashSet(StorageBlocks.OBSIDIAN_SAFE.get()), null));
	public static final RegistryObject<BlockEntityType<LockerBlockEntity>> LOCKER = BLOCK_ENTITIES.register("locker", () -> new BlockEntityType<>(LockerBlockEntity::new, Sets.newHashSet(StorageBlocks.LOCKER.get()), null));
	public static final RegistryObject<BlockEntityType<ItemTowerBlockEntity>> ITEM_TOWER = BLOCK_ENTITIES.register("item_tower", () -> new BlockEntityType<>(ItemTowerBlockEntity::new, Sets.newHashSet(StorageBlocks.ITEM_TOWER.get()), null));

	public static final RegistryObject<BlockEntityType<WarehouseCrateBlockEntity>> WAREHOUSE_CRATE = BLOCK_ENTITIES.register("warehouse_crate", () -> new BlockEntityType<>(WarehouseCrateBlockEntity::new, getWarehouseCrates(), null));

	public static final RegistryObject<BlockEntityType<BaseLockedBlockEntity>> BASE_LOCKED = BLOCK_ENTITIES.register("base_locked", () -> new BlockEntityType<>(BaseLockedBlockEntity::new, StorageBlocks.lockedDoors(), null));

	public static final RegistryObject<BlockEntityType<LockedEnderChestBlockEntity>> LOCKED_ENDER_CHEST = BLOCK_ENTITIES.register("locked_ender_chest", () -> new BlockEntityType<>(LockedEnderChestBlockEntity::new, Sets.newHashSet(StorageBlocks.LOCKED_ENDER_CHEST.get()), null));

	public static final RegistryObject<BlockEntityType<LockedChestBlockEntity>> LOCKED_CHEST = BLOCK_ENTITIES.register("locked_chest", () -> new BlockEntityType<>(LockedChestBlockEntity::new, getChests(), null));
	public static final RegistryObject<BlockEntityType<LockedShulkerBoxBlockEntity>> LOCKED_SHULKER_BOX = BLOCK_ENTITIES.register("locked_shulker_box", () -> new BlockEntityType<>(LockedShulkerBoxBlockEntity::new, getShulkers(), null));
	public static final RegistryObject<BlockEntityType<LockedBarrelBlockEntity>> LOCKED_BARREL = BLOCK_ENTITIES.register("locked_barrel", () -> new BlockEntityType<>(LockedBarrelBlockEntity::new, getBarrels(), null));
	public static final RegistryObject<BlockEntityType<LockedHopperBlockEntity>> LOCKED_HOPPER = BLOCK_ENTITIES.register("locked_hopper", () -> new BlockEntityType<>(LockedHopperBlockEntity::new, getHoppers(), null));

	public static final RegistryObject<BlockEntityType<CrateBlockEntity>> CRATE = BLOCK_ENTITIES.register("crate", () -> new BlockEntityType<>(CrateBlockEntity::new, getCrates(), null));
	public static final RegistryObject<BlockEntityType<CrateCompactingBlockEntity>> CRATE_COMPACTING = BLOCK_ENTITIES.register("crate_compacting", () -> new BlockEntityType<>(CrateCompactingBlockEntity::new, Sets.newHashSet(StorageBlocks.CRATE_COMPACTING.get()), null));
	public static final RegistryObject<BlockEntityType<CrateControllerBlockEntity>> CRATE_CONTROLLER = BLOCK_ENTITIES.register("crate_controller", () -> new BlockEntityType<>(CrateControllerBlockEntity::new, Sets.newHashSet(StorageBlocks.CRATE_CONTROLLER.get()), null));

	private static Set<Block> getWarehouseCrates() {
		return Sets.newHashSet(StorageBlocks.OAK_WAREHOUSE_CRATE.get(), StorageBlocks.BIRCH_WAREHOUSE_CRATE.get(), StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get(), StorageBlocks.ACACIA_WAREHOUSE_CRATE.get(), StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get(), StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get(), StorageBlocks.WARPED_WAREHOUSE_CRATE.get(), StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get(), StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get());
	}

	private static Set<Block> getChests() {
		Set<Block> chests = StorageBlocks.CHESTS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
		chests.add(StorageBlocks.LOCKED_CHEST.get());
		return chests;
	}

	private static Set<Block> getShulkers() {
		Set<Block> shulkers = StorageBlocks.SHULKERS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
		shulkers.add(StorageBlocks.LOCKED_SHULKER_BOX.get());
		return shulkers;
	}

	private static Set<Block> getBarrels() {
		Set<Block> barrels = StorageBlocks.BARRELS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
		barrels.add(StorageBlocks.LOCKED_BARREL.get());
		return barrels;
	}

	private static Set<Block> getHoppers() {
		Set<Block> hoppers = StorageBlocks.HOPPERS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
		hoppers.add(StorageBlocks.LOCKED_HOPPER.get());
		return hoppers;
	}

	private static Set<Block> getCrates() {
		Set<Block> crates = Sets.newHashSet();
		crates.add(StorageBlocks.CRATE.get());
		crates.add(StorageBlocks.CRATE_DOUBLE.get());
		crates.add(StorageBlocks.CRATE_TRIPLE.get());
		crates.add(StorageBlocks.CRATE_QUADRUPLE.get());
		return crates;
	}
}
