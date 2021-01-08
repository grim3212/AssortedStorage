package com.grim3212.assorted.storage.common.block;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Sets;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.tileentity.StorageItemStackRenderer;
import com.grim3212.assorted.storage.common.block.tileentity.GlassCabinetTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.GoldSafeTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.ItemTowerTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.LockerTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.ObsidianSafeTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.WarehouseCrateTileEntity;
import com.grim3212.assorted.storage.common.block.tileentity.WoodCabinetTileEntity;
import com.grim3212.assorted.storage.common.item.LockerItem;
import com.grim3212.assorted.storage.common.item.StorageItems;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.WoodType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageBlocks {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AssortedStorage.MODID);
	public static final DeferredRegister<Item> ITEMS = StorageItems.ITEMS;

	public static final RegistryObject<LocksmithWorkbenchBlock> LOCKSMITH_WORKBENCH = register("locksmith_workbench", () -> new LocksmithWorkbenchBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(3.0f, 5.0f)));
	public static final RegistryObject<WoodCabinetBlock> WOOD_CABINET = register("wood_cabinet", () -> new WoodCabinetBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD)), () -> woodCabinetRenderer());
	public static final RegistryObject<GlassCabinetBlock> GLASS_CABINET = register("glass_cabinet", () -> new GlassCabinetBlock(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD)), () -> glassCabinetRenderer());
	public static final RegistryObject<GoldSafeBlock> GOLD_SAFE = register("gold_safe", () -> new GoldSafeBlock(Block.Properties.create(Material.IRON).sound(SoundType.METAL)), () -> goldSafeRenderer());
	public static final RegistryObject<ObsidianSafeBlock> OBSIDIAN_SAFE = register("obsidian_safe", () -> new ObsidianSafeBlock(Block.Properties.create(Material.ROCK).sound(SoundType.STONE)), () -> obsidianSafeRenderer());
	public static final RegistryObject<LockerBlock> LOCKER = registerWithItem("locker", () -> new LockerBlock(Block.Properties.create(Material.IRON).sound(SoundType.METAL)), lockerItem());
	public static final RegistryObject<ItemTowerBlock> ITEM_TOWER = register("item_tower", () -> new ItemTowerBlock(Block.Properties.create(Material.IRON).sound(SoundType.METAL)), () -> itemTowerRenderer());
	public static final RegistryObject<WarehouseCrateBlock> OAK_WAREHOUSE_CRATE = register("oak_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.OAK), () -> oakWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> BIRCH_WAREHOUSE_CRATE = register("birch_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.BIRCH), () -> birchWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> SPRUCE_WAREHOUSE_CRATE = register("spruce_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.SPRUCE), () -> spruceWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> ACACIA_WAREHOUSE_CRATE = register("acacia_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.ACACIA), () -> acaciaWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> DARK_OAK_WAREHOUSE_CRATE = register("dark_oak_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.DARK_OAK), () -> darkOakWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> JUNGLE_WAREHOUSE_CRATE = register("jungle_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.JUNGLE), () -> jungleWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> WARPED_WAREHOUSE_CRATE = register("warped_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.WARPED), () -> warpedWarehouseCrateRenderer());
	public static final RegistryObject<WarehouseCrateBlock> CRIMSON_WAREHOUSE_CRATE = register("crimson_warehouse_crate", () -> new WarehouseCrateBlock(WoodType.CRIMSON), () -> crimsonWarehouseCrateRenderer());

	public static final RegistryObject<DoorBlock> QUARTZ_DOOR = register("quartz_door", () -> new DoorBlock(Block.Properties.create(Material.IRON, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(5.0F).sound(SoundType.METAL).notSolid()));

	public static final RegistryObject<LockedDoorBlock> LOCKED_OAK_DOOR = register("locked_oak_door", () -> new LockedDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_SPRUCE_DOOR = register("locked_spruce_door", () -> new LockedDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.OBSIDIAN).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_BIRCH_DOOR = register("locked_birch_door", () -> new LockedDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.SAND).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_JUNGLE_DOOR = register("locked_jungle_door", () -> new LockedDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.DIRT).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_ACACIA_DOOR = register("locked_acacia_door", () -> new LockedDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_DARK_OAK_DOOR = register("locked_dark_oak_door", () -> new LockedDoorBlock(Block.Properties.create(Material.WOOD, MaterialColor.BROWN).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_CRIMSON_DOOR = register("locked_crimson_door", () -> new LockedDoorBlock(Block.Properties.create(Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_WARPED_DOOR = register("locked_warped_door", () -> new LockedDoorBlock(Block.Properties.create(Material.NETHER_WOOD, MaterialColor.WARPED_STEM).hardnessAndResistance(3.0F).sound(SoundType.WOOD).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_IRON_DOOR = register("locked_iron_door", () -> new LockedDoorBlock(Block.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(5.0F).sound(SoundType.METAL).notSolid()));
	public static final RegistryObject<LockedDoorBlock> LOCKED_QUARTZ_DOOR = register("locked_quartz_door", () -> new LockedDoorBlock(Block.Properties.create(Material.IRON, MaterialColor.QUARTZ).setRequiresTool().hardnessAndResistance(5.0F).sound(SoundType.METAL).notSolid()));

	public static Set<Block> lockedDoors() {
		return Sets.newHashSet(StorageBlocks.LOCKED_OAK_DOOR.get(), StorageBlocks.LOCKED_SPRUCE_DOOR.get(), StorageBlocks.LOCKED_BIRCH_DOOR.get(), StorageBlocks.LOCKED_JUNGLE_DOOR.get(), StorageBlocks.LOCKED_ACACIA_DOOR.get(), StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), StorageBlocks.LOCKED_CRIMSON_DOOR.get(), StorageBlocks.LOCKED_WARPED_DOOR.get(), StorageBlocks.LOCKED_IRON_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get());
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup) {
		return register(name, sup, block -> item(block));
	}

	private static <T extends Block> RegistryObject<T> register(String name, Supplier<? extends T> sup, Supplier<Callable<ItemStackTileEntityRenderer>> renderMethod) {
		return register(name, sup, block -> item(block, renderMethod));
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
		return () -> new BlockItem(block.get(), new Item.Properties().group(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP));
	}

	private static Supplier<BlockItem> item(final RegistryObject<? extends Block> block, final Supplier<Callable<ItemStackTileEntityRenderer>> renderMethod) {
		return () -> new BlockItem(block.get(), new Item.Properties().group(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP).setISTER(renderMethod));
	}

	private static Supplier<BlockItem> lockerItem() {
		return () -> new LockerItem(new Item.Properties().group(AssortedStorage.ASSORTED_STORAGE_ITEM_GROUP).setISTER(() -> lockerRenderer()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> woodCabinetRenderer() {
		return () -> new StorageItemStackRenderer<>(WoodCabinetTileEntity::new);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> glassCabinetRenderer() {
		return () -> new StorageItemStackRenderer<>(GlassCabinetTileEntity::new);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> oakWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.OAK_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> birchWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> spruceWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> acaciaWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> darkOakWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> jungleWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> warpedWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.WARPED_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> crimsonWarehouseCrateRenderer() {
		return () -> new StorageItemStackRenderer<>(() -> new WarehouseCrateTileEntity(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get()));
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> goldSafeRenderer() {
		return () -> new StorageItemStackRenderer<>(GoldSafeTileEntity::new);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> obsidianSafeRenderer() {
		return () -> new StorageItemStackRenderer<>(ObsidianSafeTileEntity::new);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> lockerRenderer() {
		return () -> new StorageItemStackRenderer<>(LockerTileEntity::new);
	}

	@OnlyIn(Dist.CLIENT)
	private static Callable<ItemStackTileEntityRenderer> itemTowerRenderer() {
		return () -> new StorageItemStackRenderer<>(ItemTowerTileEntity::new);
	}
}
