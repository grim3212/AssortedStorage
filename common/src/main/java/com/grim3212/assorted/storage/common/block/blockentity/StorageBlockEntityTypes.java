package com.grim3212.assorted.storage.common.block.blockentity;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageBlockEntityTypes {
    public static final RegistryProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistryProvider.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final IRegistryObject<BlockEntityType<WoodCabinetBlockEntity>> WOOD_CABINET = BLOCK_ENTITIES.register("wood_cabinet", () -> Services.REGISTRY_UTIL.createBlockEntityType(WoodCabinetBlockEntity::new, StorageBlocks.WOOD_CABINET.get()));
    public static final IRegistryObject<BlockEntityType<GlassCabinetBlockEntity>> GLASS_CABINET = BLOCK_ENTITIES.register("glass_cabinet", () -> Services.REGISTRY_UTIL.createBlockEntityType(GlassCabinetBlockEntity::new, StorageBlocks.GLASS_CABINET.get()));
    public static final IRegistryObject<BlockEntityType<GoldSafeBlockEntity>> GOLD_SAFE = BLOCK_ENTITIES.register("gold_safe", () -> Services.REGISTRY_UTIL.createBlockEntityType(GoldSafeBlockEntity::new, StorageBlocks.GOLD_SAFE.get()));
    public static final IRegistryObject<BlockEntityType<ObsidianSafeBlockEntity>> OBSIDIAN_SAFE = BLOCK_ENTITIES.register("obsidian_safe", () -> Services.REGISTRY_UTIL.createBlockEntityType(ObsidianSafeBlockEntity::new, StorageBlocks.OBSIDIAN_SAFE.get()));
    public static final IRegistryObject<BlockEntityType<LockerBlockEntity>> LOCKER = BLOCK_ENTITIES.register("locker", () -> Services.REGISTRY_UTIL.createBlockEntityType(LockerBlockEntity::new, StorageBlocks.LOCKER.get()));
    public static final IRegistryObject<BlockEntityType<ItemTowerBlockEntity>> ITEM_TOWER = BLOCK_ENTITIES.register("item_tower", () -> Services.REGISTRY_UTIL.createBlockEntityType(ItemTowerBlockEntity::new, StorageBlocks.ITEM_TOWER.get()));

    public static final IRegistryObject<BlockEntityType<WarehouseCrateBlockEntity>> WAREHOUSE_CRATE = BLOCK_ENTITIES.register("warehouse_crate", () -> Services.REGISTRY_UTIL.createBlockEntityType(WarehouseCrateBlockEntity::new, getWarehouseCrates()));

    public static final IRegistryObject<BlockEntityType<BaseLockedBlockEntity>> BASE_LOCKED = BLOCK_ENTITIES.register("base_locked", () -> Services.REGISTRY_UTIL.createBlockEntityType(BaseLockedBlockEntity::new, StorageBlocks.lockedDoors()));

    public static final IRegistryObject<BlockEntityType<LockedEnderChestBlockEntity>> LOCKED_ENDER_CHEST = BLOCK_ENTITIES.register("locked_ender_chest", () -> Services.REGISTRY_UTIL.createBlockEntityType(LockedEnderChestBlockEntity::new, StorageBlocks.LOCKED_ENDER_CHEST.get()));

    public static final IRegistryObject<BlockEntityType<LockedChestBlockEntity>> LOCKED_CHEST = BLOCK_ENTITIES.register("locked_chest", () -> Services.REGISTRY_UTIL.createBlockEntityType(LockedChestBlockEntity::new, getChests()));
    public static final IRegistryObject<BlockEntityType<LockedShulkerBoxBlockEntity>> LOCKED_SHULKER_BOX = BLOCK_ENTITIES.register("locked_shulker_box", () -> Services.REGISTRY_UTIL.createBlockEntityType(LockedShulkerBoxBlockEntity::new, getShulkers()));
    public static final IRegistryObject<BlockEntityType<LockedBarrelBlockEntity>> LOCKED_BARREL = BLOCK_ENTITIES.register("locked_barrel", () -> Services.REGISTRY_UTIL.createBlockEntityType(LockedBarrelBlockEntity::new, getBarrels()));
    public static final IRegistryObject<BlockEntityType<LockedHopperBlockEntity>> LOCKED_HOPPER = BLOCK_ENTITIES.register("locked_hopper", () -> Services.REGISTRY_UTIL.createBlockEntityType(LockedHopperBlockEntity::new, getHoppers()));

    public static final IRegistryObject<BlockEntityType<CrateBlockEntity>> CRATE = BLOCK_ENTITIES.register("crate", () -> Services.REGISTRY_UTIL.createBlockEntityType(CrateBlockEntity::new, getCrates()));
    public static final IRegistryObject<BlockEntityType<CrateCompactingBlockEntity>> CRATE_COMPACTING = BLOCK_ENTITIES.register("crate_compacting", () -> Services.REGISTRY_UTIL.createBlockEntityType(CrateCompactingBlockEntity::new, StorageBlocks.CRATE_COMPACTING.get()));
    public static final IRegistryObject<BlockEntityType<CrateControllerBlockEntity>> CRATE_CONTROLLER = BLOCK_ENTITIES.register("crate_controller", () -> Services.REGISTRY_UTIL.createBlockEntityType(CrateControllerBlockEntity::new, StorageBlocks.CRATE_CONTROLLER.get()));

    public static Block[] getWarehouseCrates() {
        return new Block[]{StorageBlocks.OAK_WAREHOUSE_CRATE.get(), StorageBlocks.BIRCH_WAREHOUSE_CRATE.get(), StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get(), StorageBlocks.ACACIA_WAREHOUSE_CRATE.get(), StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get(), StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get(), StorageBlocks.WARPED_WAREHOUSE_CRATE.get(), StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get(), StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get()};
    }

    public static Block[] getChests() {
        Set<Block> chests = StorageBlocks.CHESTS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
        chests.add(StorageBlocks.LOCKED_CHEST.get());
        return chests.toArray(new Block[0]);
    }

    public static Block[] getShulkers() {
        Set<Block> shulkers = StorageBlocks.SHULKERS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
        shulkers.add(StorageBlocks.LOCKED_SHULKER_BOX.get());
        return shulkers.toArray(new Block[0]);
    }

    private static Block[] getBarrels() {
        Set<Block> barrels = StorageBlocks.BARRELS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
        barrels.add(StorageBlocks.LOCKED_BARREL.get());
        return barrels.toArray(new Block[0]);
    }

    private static Block[] getHoppers() {
        Set<Block> hoppers = StorageBlocks.HOPPERS.values().stream().map((b) -> b.get()).collect(Collectors.toSet());
        hoppers.add(StorageBlocks.LOCKED_HOPPER.get());
        return hoppers.toArray(new Block[0]);
    }

    private static Block[] getCrates() {
        Set<Block> crates = new HashSet<>();
        StorageBlocks.CRATES.forEach(x -> {
            crates.add(x.SINGLE.get());
            crates.add(x.DOUBLE.get());
            crates.add(x.TRIPLE.get());
            crates.add(x.QUADRUPLE.get());
        });
        return crates.toArray(new Block[0]);
    }

    public static void init() {

    }
}
