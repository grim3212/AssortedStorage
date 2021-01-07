package com.grim3212.assorted.storage.common.block.tileentity;

import java.util.Set;

import com.google.common.collect.Sets;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, AssortedStorage.MODID);

	public static final RegistryObject<TileEntityType<WoodCabinetTileEntity>> WOOD_CABINET = TILE_ENTITIES.register("wood_cabinet", () -> new TileEntityType<>(WoodCabinetTileEntity::new, Sets.newHashSet(StorageBlocks.WOOD_CABINET.get()), null));
	public static final RegistryObject<TileEntityType<GlassCabinetTileEntity>> GLASS_CABINET = TILE_ENTITIES.register("glass_cabinet", () -> new TileEntityType<>(GlassCabinetTileEntity::new, Sets.newHashSet(StorageBlocks.GLASS_CABINET.get()), null));
	public static final RegistryObject<TileEntityType<GoldSafeTileEntity>> GOLD_SAFE = TILE_ENTITIES.register("gold_safe", () -> new TileEntityType<>(GoldSafeTileEntity::new, Sets.newHashSet(StorageBlocks.GOLD_SAFE.get()), null));
	public static final RegistryObject<TileEntityType<ObsidianSafeTileEntity>> OBSIDIAN_SAFE = TILE_ENTITIES.register("obsidian_safe", () -> new TileEntityType<>(ObsidianSafeTileEntity::new, Sets.newHashSet(StorageBlocks.OBSIDIAN_SAFE.get()), null));
	public static final RegistryObject<TileEntityType<LockerTileEntity>> LOCKER = TILE_ENTITIES.register("locker", () -> new TileEntityType<>(LockerTileEntity::new, Sets.newHashSet(StorageBlocks.LOCKER.get()), null));
	public static final RegistryObject<TileEntityType<ItemTowerTileEntity>> ITEM_TOWER = TILE_ENTITIES.register("item_tower", () -> new TileEntityType<>(ItemTowerTileEntity::new, Sets.newHashSet(StorageBlocks.ITEM_TOWER.get()), null));

	public static final RegistryObject<TileEntityType<WarehouseCrateTileEntity>> WAREHOUSE_CRATE = TILE_ENTITIES.register("warehouse_crate", () -> new TileEntityType<>(WarehouseCrateTileEntity::new, getWarehouseCrates(), null));

	public static final RegistryObject<TileEntityType<BasicLockedTileEntity>> BASIC_LOCKED = TILE_ENTITIES.register("basic_locked", () -> new TileEntityType<>(BasicLockedTileEntity::new, Sets.newHashSet(StorageBlocks.QUARTZ_LOCKED_DOOR.get()), null));

	private static Set<Block> getWarehouseCrates() {
		return Sets.newHashSet(StorageBlocks.OAK_WAREHOUSE_CRATE.get(), StorageBlocks.BIRCH_WAREHOUSE_CRATE.get(), StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get(), StorageBlocks.ACACIA_WAREHOUSE_CRATE.get(), StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get(), StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get(), StorageBlocks.WARPED_WAREHOUSE_CRATE.get(), StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
	}
}
