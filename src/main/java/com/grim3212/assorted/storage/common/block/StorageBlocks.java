package com.grim3212.assorted.storage.common.block;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.item.LockerItem;
import com.grim3212.assorted.storage.common.item.StorageBlockItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.WarehouseCrateBlockItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssortedStorage.MODID);
	public static final DeferredRegister<Item> ITEMS = StorageItems.ITEMS;

	public static final RegistryObject<LocksmithWorkbenchBlock> LOCKSMITH_WORKBENCH = register("locksmith_workbench", () -> new LocksmithWorkbenchBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(3.0f, 5.0f)));
	public static final RegistryObject<WoodCabinetBlock> WOOD_CABINET = registerStorageItem("wood_cabinet", () -> new WoodCabinetBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD)));
	public static final RegistryObject<GlassCabinetBlock> GLASS_CABINET = registerStorageItem("glass_cabinet", () -> new GlassCabinetBlock(Block.Properties.of(Material.WOOD).sound(SoundType.WOOD)));
	public static final RegistryObject<GoldSafeBlock> GOLD_SAFE = registerStorageItem("gold_safe", () -> new GoldSafeBlock(Block.Properties.of(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<ObsidianSafeBlock> OBSIDIAN_SAFE = registerStorageItem("obsidian_safe", () -> new ObsidianSafeBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE)));
	public static final RegistryObject<LockerBlock> LOCKER = registerWithItem("locker", () -> new LockerBlock(Block.Properties.of(Material.METAL).sound(SoundType.METAL)), lockerItem());
	public static final RegistryObject<ItemTowerBlock> ITEM_TOWER = registerStorageItem("item_tower", () -> new ItemTowerBlock(Block.Properties.of(Material.METAL).sound(SoundType.METAL)));
	public static final RegistryObject<WarehouseCrateBlock> OAK_WAREHOUSE_CRATE = registerCrate("oak_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.OAK));
	public static final RegistryObject<WarehouseCrateBlock> BIRCH_WAREHOUSE_CRATE = registerCrate("birch_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.BIRCH));
	public static final RegistryObject<WarehouseCrateBlock> SPRUCE_WAREHOUSE_CRATE = registerCrate("spruce_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.SPRUCE));
	public static final RegistryObject<WarehouseCrateBlock> ACACIA_WAREHOUSE_CRATE = registerCrate("acacia_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.ACACIA));
	public static final RegistryObject<WarehouseCrateBlock> DARK_OAK_WAREHOUSE_CRATE = registerCrate("dark_oak_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.DARK_OAK));
	public static final RegistryObject<WarehouseCrateBlock> JUNGLE_WAREHOUSE_CRATE = registerCrate("jungle_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.JUNGLE));
	public static final RegistryObject<WarehouseCrateBlock> WARPED_WAREHOUSE_CRATE = registerCrate("warped_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.WARPED));
	public static final RegistryObject<WarehouseCrateBlock> CRIMSON_WAREHOUSE_CRATE = registerCrate("crimson_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.CRIMSON));

	public static final RegistryObject<LockedEnderChestBlock> LOCKED_ENDER_CHEST = registerStorageItem("locked_ender_chest", () -> new LockedEnderChestBlock(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(22.5F, 600.0F).lightLevel((state) -> {
		return 7;
	})));

	public static final RegistryObject<LockedDoorBlock> LOCKED_OAK_DOOR = registerNoItem("locked_oak_door", () -> new LockedDoorBlock(Blocks.OAK_DOOR, Block.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_SPRUCE_DOOR = registerNoItem("locked_spruce_door", () -> new LockedDoorBlock(Blocks.SPRUCE_DOOR, Block.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_BIRCH_DOOR = registerNoItem("locked_birch_door", () -> new LockedDoorBlock(Blocks.BIRCH_DOOR, Block.Properties.of(Material.WOOD, MaterialColor.SAND).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_JUNGLE_DOOR = registerNoItem("locked_jungle_door", () -> new LockedDoorBlock(Blocks.JUNGLE_DOOR, Block.Properties.of(Material.WOOD, MaterialColor.DIRT).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_ACACIA_DOOR = registerNoItem("locked_acacia_door", () -> new LockedDoorBlock(Blocks.ACACIA_DOOR, Block.Properties.of(Material.WOOD, MaterialColor.COLOR_ORANGE).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_DARK_OAK_DOOR = registerNoItem("locked_dark_oak_door", () -> new LockedDoorBlock(Blocks.DARK_OAK_DOOR, Block.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_CRIMSON_DOOR = registerNoItem("locked_crimson_door", () -> new LockedDoorBlock(Blocks.CRIMSON_DOOR, Block.Properties.of(Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_WARPED_DOOR = registerNoItem("locked_warped_door", () -> new LockedDoorBlock(Blocks.WARPED_DOOR, Block.Properties.of(Material.NETHER_WOOD, MaterialColor.WARPED_STEM).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_IRON_DOOR = registerNoItem("locked_iron_door", () -> new LockedDoorBlock(Blocks.IRON_DOOR, Block.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));

	public static final RegistryObject<LockedDoorBlock> LOCKED_QUARTZ_DOOR = registerNoItem("locked_quartz_door", () -> new LockedDoorBlock(new ResourceLocation("assorteddecor:quartz_door"), Block.Properties.of(Material.METAL, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_GLASS_DOOR = registerNoItem("locked_glass_door", () -> new LockedDoorBlock(new ResourceLocation("assorteddecor:glass_door"), Block.Properties.of(Material.GLASS, Blocks.GLASS.defaultMaterialColor()).strength(0.75F, 7.5F).sound(SoundType.GLASS).noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_STEEL_DOOR = registerNoItem("locked_steel_door", () -> new LockedDoorBlock(new ResourceLocation("assorteddecor:steel_door"), Block.Properties.of(Material.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_CHAIN_LINK_DOOR = registerNoItem("locked_chain_link_door", () -> new LockedDoorBlock(new ResourceLocation("assorteddecor:chain_link_door"), Block.Properties.of(Material.DECORATION).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));

	public static Set<Block> lockedDoors() {
		return Sets.newHashSet(StorageBlocks.LOCKED_OAK_DOOR.get(), StorageBlocks.LOCKED_SPRUCE_DOOR.get(), StorageBlocks.LOCKED_BIRCH_DOOR.get(), StorageBlocks.LOCKED_JUNGLE_DOOR.get(), StorageBlocks.LOCKED_ACACIA_DOOR.get(), StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), StorageBlocks.LOCKED_CRIMSON_DOOR.get(), StorageBlocks.LOCKED_WARPED_DOOR.get(), StorageBlocks.LOCKED_IRON_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get(), StorageBlocks.LOCKED_GLASS_DOOR.get(),
				StorageBlocks.LOCKED_STEEL_DOOR.get(), StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get());
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> item(block));
	}

	private static <T extends Block> RegistryObject<T> registerCrate(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> crateItem(block));
	}

	private static <T extends Block> RegistryObject<T> registerStorageItem(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> storageItem(block));
	}

	private static <T extends Block> RegistryObject<T> registerWithItem(String name, Supplier<? extends T> sup, Supplier<BlockItem> blockItem) {
		return register(name, sup, block -> blockItem);
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
		RegistryObject<T> ret = registerNoItem(name, sup);
		ITEMS.register(name, itemCreator.apply(ret));
		return ret;
	}

	private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<? extends T> sup) {
		return BLOCKS.register(name, sup);
	}

	private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block) {
		return () -> new BlockItem(block.get(), new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP));
	}

	private static Supplier<StorageBlockItem> storageItem(final RegistryObject<? extends Block> block) {
		return () -> new StorageBlockItem(block.get(), new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP));
	}

	private static Supplier<WarehouseCrateBlockItem> crateItem(final RegistryObject<? extends Block> block) {
		return () -> new WarehouseCrateBlockItem(block.get(), new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP));
	}

	private static Supplier<BlockItem> lockerItem() {
		return () -> new LockerItem(new Item.Properties().tab(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP));
	}
}
