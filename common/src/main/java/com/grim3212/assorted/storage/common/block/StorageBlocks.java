package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.common.item.*;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class StorageBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID);

    public static final IRegistryObject<LocksmithWorkbenchBlock> LOCKSMITH_WORKBENCH = register("locksmith_workbench", key -> new LocksmithWorkbenchBlock(Block.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(3.0f, 5.0f)));
    public static final IRegistryObject<WoodCabinetBlock> WOOD_CABINET = registerStorageItem("wood_cabinet", key -> new WoodCabinetBlock(Block.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD)));
    public static final IRegistryObject<GlassCabinetBlock> GLASS_CABINET = registerStorageItem("glass_cabinet", key -> new GlassCabinetBlock(Block.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD)));
    public static final IRegistryObject<GoldSafeBlock> GOLD_SAFE = registerStorageItem("gold_safe", key -> new GoldSafeBlock(Block.Properties.of().setId(key).mapColor(MapColor.METAL).sound(SoundType.METAL)));
    public static final IRegistryObject<ObsidianSafeBlock> OBSIDIAN_SAFE = registerStorageItem("obsidian_safe", key -> new ObsidianSafeBlock(Block.Properties.of().setId(key).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE)));
    public static final IRegistryObject<LockerBlock> LOCKER = registerLocker("locker", key -> new LockerBlock(Block.Properties.of().setId(key).mapColor(MapColor.METAL).sound(SoundType.METAL)));
    public static final IRegistryObject<ItemTowerBlock> ITEM_TOWER = registerStorageItem("item_tower", key -> new ItemTowerBlock(Block.Properties.of().setId(key).mapColor(MapColor.METAL).sound(SoundType.METAL)));
    public static final IRegistryObject<WarehouseCrateBlock> OAK_WAREHOUSE_CRATE = registerCrate("oak_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.OAK, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> BIRCH_WAREHOUSE_CRATE = registerCrate("birch_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.BIRCH, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> SPRUCE_WAREHOUSE_CRATE = registerCrate("spruce_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.SPRUCE, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> ACACIA_WAREHOUSE_CRATE = registerCrate("acacia_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.ACACIA, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> DARK_OAK_WAREHOUSE_CRATE = registerCrate("dark_oak_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.DARK_OAK, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> JUNGLE_WAREHOUSE_CRATE = registerCrate("jungle_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.JUNGLE, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> WARPED_WAREHOUSE_CRATE = registerCrate("warped_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.WARPED, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> CRIMSON_WAREHOUSE_CRATE = registerCrate("crimson_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.CRIMSON, warehouseCrateProps(key)));
    public static final IRegistryObject<WarehouseCrateBlock> MANGROVE_WAREHOUSE_CRATE = registerCrate("mangrove_warehouse_crate", key -> new WarehouseCrateBlock(WoodType.MANGROVE, warehouseCrateProps(key)));

    public static final IRegistryObject<LockedEnderChestBlock> LOCKED_ENDER_CHEST = registerStorageItem("locked_ender_chest", key -> new LockedEnderChestBlock(Block.Properties.of().setId(key).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(22.5F, 600.0F).lightLevel((state) -> {
        return 7;
    })));

    public static final IRegistryObject<LockedChestBlock> LOCKED_CHEST = registerChest("locked_chest", key -> new LockedChestBlock(null, BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)), StorageBlocks::itemProperties);
    public static final IRegistryObject<LockedShulkerBoxBlock> LOCKED_SHULKER_BOX = registerShulker("locked_shulker_box", key -> new LockedShulkerBoxBlock(null, BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.COLOR_PURPLE)), StorageBlocks::itemProperties);
    public static final IRegistryObject<LockedBarrelBlock> LOCKED_BARREL = register("locked_barrel", key -> new LockedBarrelBlock(null, BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD)));
    public static final IRegistryObject<LockedHopperBlock> LOCKED_HOPPER = register("locked_hopper", key -> new LockedHopperBlock(null, BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(3.0F, 4.8F).sound(SoundType.METAL).noOcclusion()));

    public static final IRegistryObject<LockedDoorBlock> LOCKED_OAK_DOOR = registerNoItem("locked_oak_door", key -> new LockedDoorBlock((DoorBlock) Blocks.OAK_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_SPRUCE_DOOR = registerNoItem("locked_spruce_door", key -> new LockedDoorBlock((DoorBlock) Blocks.SPRUCE_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.PODZOL).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_BIRCH_DOOR = registerNoItem("locked_birch_door", key -> new LockedDoorBlock((DoorBlock) Blocks.BIRCH_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.SAND).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_JUNGLE_DOOR = registerNoItem("locked_jungle_door", key -> new LockedDoorBlock((DoorBlock) Blocks.JUNGLE_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.DIRT).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_ACACIA_DOOR = registerNoItem("locked_acacia_door", key -> new LockedDoorBlock((DoorBlock) Blocks.ACACIA_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.COLOR_ORANGE).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_DARK_OAK_DOOR = registerNoItem("locked_dark_oak_door", key -> new LockedDoorBlock((DoorBlock) Blocks.DARK_OAK_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.COLOR_BROWN).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_CRIMSON_DOOR = registerNoItem("locked_crimson_door", key -> new LockedDoorBlock((DoorBlock) Blocks.CRIMSON_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.CRIMSON_STEM).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_WARPED_DOOR = registerNoItem("locked_warped_door", key -> new LockedDoorBlock((DoorBlock) Blocks.WARPED_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.WARPED_STEM).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_MANGROVE_DOOR = registerNoItem("locked_mangrove_door", key -> new LockedDoorBlock((DoorBlock) Blocks.MANGROVE_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.COLOR_RED).ignitedByLava().instrument(NoteBlockInstrument.BASS).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_IRON_DOOR = registerNoItem("locked_iron_door", key -> new LockedDoorBlock((DoorBlock) Blocks.IRON_DOOR, Block.Properties.of().setId(key).mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final IRegistryObject<LockedDoorBlock> LOCKED_QUARTZ_DOOR = registerNoItem("locked_quartz_door", key -> new LockedDoorBlock(Identifier.parse("assorteddecor:quartz_door"), BlockSetType.IRON, Block.Properties.of().setId(key).mapColor(MapColor.QUARTZ).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_GLASS_DOOR = registerNoItem("locked_glass_door", key -> new LockedDoorBlock(Identifier.parse("assorteddecor:glass_door"), BlockSetType.IRON, Block.Properties.of().setId(key).mapColor(Blocks.GLASS.defaultMapColor()).instrument(NoteBlockInstrument.HAT).strength(0.75F, 7.5F).sound(SoundType.GLASS).noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_STEEL_DOOR = registerNoItem("locked_steel_door", key -> new LockedDoorBlock(Identifier.parse("assorteddecor:steel_door"), BlockSetType.IRON, Block.Properties.of().setId(key).mapColor(MapColor.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));
    public static final IRegistryObject<LockedDoorBlock> LOCKED_CHAIN_LINK_DOOR = registerNoItem("locked_chain_link_door", key -> new LockedDoorBlock(Identifier.parse("assorteddecor:chain_link_door"), BlockSetType.IRON, Block.Properties.of().setId(key).mapColor(MapColor.METAL).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));

    public static final IRegistryObject<CrateCompactingBlock> CRATE_COMPACTING = register("crate_compacting", key -> new CrateCompactingBlock(CrateLayout.TRIPLE, BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sound(SoundType.STONE)));
    public static final IRegistryObject<CrateControllerBlock> CRATE_CONTROLLER = register("crate_controller", key -> new CrateControllerBlock(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sound(SoundType.STONE)));
    public static final IRegistryObject<CrateBridgeBlock> CRATE_BRIDGE = register("crate_bridge", key -> new CrateBridgeBlock(BlockBehaviour.Properties.of().setId(key).mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).sound(SoundType.STONE)));

    public static final Map<StorageMaterial, IRegistryObject<LockedChestBlock>> CHESTS = new EnumMap<>(StorageMaterial.class);
    public static final Map<StorageMaterial, IRegistryObject<LockedBarrelBlock>> BARRELS = new EnumMap<>(StorageMaterial.class);
    public static final Map<StorageMaterial, IRegistryObject<LockedHopperBlock>> HOPPERS = new EnumMap<>(StorageMaterial.class);
    public static final Map<StorageMaterial, IRegistryObject<LockedShulkerBoxBlock>> SHULKERS = new EnumMap<>(StorageMaterial.class);
    public static final List<CrateGroup> CRATES = new ArrayList<>();

    static {
        Stream.of(StorageMaterial.values()).forEach((type) -> {
            CHESTS.put(type, registerChest("chest_" + type.toString(), key -> new LockedChestBlock(type, type.getProps().setId(key)), itemProperties(type)));
            BARRELS.put(type, register("barrel_" + type.toString(), key -> new LockedBarrelBlock(type, type.getProps().setId(key)), itemProperties(type)));
            HOPPERS.put(type, register("hopper_" + type.toString(), key -> new LockedHopperBlock(type, type.getProps().setId(key)), itemProperties(type)));
            SHULKERS.put(type, registerShulker("shulker_box_" + type.toString(), key -> new LockedShulkerBoxBlock(type, type.getProps().setId(key)), itemProperties(type)));
        });

        Stream.of(Wood.values()).forEach((type) -> {
            CRATES.add(new CrateGroup(type));
        });
    }

    private static BlockBehaviour.Properties warehouseCrateProps(ResourceKey<Block> key) {
        return Block.Properties.of().setId(key).mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(3.0f, 5.0f);
    }

    public static void initDispenserHandlers() {
        for (IRegistryObject<LockedShulkerBoxBlock> b : SHULKERS.values()) {
            DispenserBlock.registerBehavior(b.get(), new ShulkerBoxDispenseBehavior());
        }
    }

    public static Block[] lockedDoors() {
        return new Block[]{StorageBlocks.LOCKED_OAK_DOOR.get(), StorageBlocks.LOCKED_SPRUCE_DOOR.get(), StorageBlocks.LOCKED_BIRCH_DOOR.get(), StorageBlocks.LOCKED_JUNGLE_DOOR.get(), StorageBlocks.LOCKED_ACACIA_DOOR.get(), StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), StorageBlocks.LOCKED_CRIMSON_DOOR.get(), StorageBlocks.LOCKED_WARPED_DOOR.get(), StorageBlocks.LOCKED_MANGROVE_DOOR.get(), StorageBlocks.LOCKED_IRON_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get(),
                StorageBlocks.LOCKED_GLASS_DOOR.get(), StorageBlocks.LOCKED_STEEL_DOOR.get(), StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get()};
    }

    /**
     * Netherite storage keeps its fire resistance; everything else takes plain item properties.
     * Item properties carry a registry id since 1.21.2, so they have to be built per registration
     * rather than shared, which is why this is a factory.
     */
    private static Item.Properties itemProperties(ResourceKey<Item> key) {
        return new Item.Properties().useBlockDescriptionPrefix().setId(key);
    }

    private static Function<ResourceKey<Item>, Item.Properties> itemProperties(StorageMaterial type) {
        return type == StorageMaterial.NETHERITE ? (key) -> itemProperties(key).fireResistant() : StorageBlocks::itemProperties;
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<ResourceKey<Block>, ? extends T> factory) {
        return register(name, factory, StorageBlocks::itemProperties);
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<ResourceKey<Block>, ? extends T> factory, Function<ResourceKey<Item>, Item.Properties> itemProperties) {
        return register(name, factory, name, (block, key) -> new StorageBlockItem(block.get(), itemProperties.apply(key)));
    }

    private static <T extends Block> IRegistryObject<T> registerCrate(String name, Function<ResourceKey<Block>, ? extends T> factory) {
        return register(name, factory, name, (block, key) -> new WarehouseCrateBlockItem(block.get(), itemProperties(key)));
    }

    private static <T extends Block> IRegistryObject<T> registerStorageItem(String name, Function<ResourceKey<Block>, ? extends T> factory) {
        return register(name, factory, name, (block, key) -> new StorageBlockItem(block.get(), itemProperties(key)));
    }

    private static <T extends LockedChestBlock> IRegistryObject<T> registerChest(String name, Function<ResourceKey<Block>, ? extends T> factory, Function<ResourceKey<Item>, Item.Properties> itemProperties) {
        return register(name, factory, name, (block, key) -> new ChestBlockItem(block.get(), itemProperties.apply(key)));
    }

    private static <T extends LockedShulkerBoxBlock> IRegistryObject<T> registerShulker(String name, Function<ResourceKey<Block>, ? extends T> factory, Function<ResourceKey<Item>, Item.Properties> itemProperties) {
        return register(name, factory, name, (block, key) -> new ShulkerBoxBlockItem(block.get(), itemProperties.apply(key)));
    }

    private static <T extends Block> IRegistryObject<T> registerLocker(String name, Function<ResourceKey<Block>, ? extends T> factory) {
        return register(name, factory, name, (block, key) -> new LockerItem(block.get(), itemProperties(key)));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<ResourceKey<Block>, ? extends T> factory, String itemName, ItemFactory<T> itemFactory) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, itemName));
        ITEMS.register(itemName, () -> itemFactory.create(ret, itemKey));
        return ret;
    }

    // Since 1.21.2 a block has to know its own registry id before it is constructed, so the
    // registration name is turned into a ResourceKey here and handed to the factory to put on the
    // properties. The same is true of items.
    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Function<ResourceKey<Block>, ? extends T> factory) {
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return BLOCKS.register(name, () -> factory.apply(key));
    }

    @FunctionalInterface
    private interface ItemFactory<T extends Block> {
        BlockItem create(IRegistryObject<? extends Block> block, ResourceKey<Item> key);
    }

    public static final class CrateGroup {
        public final IRegistryObject<CrateBlock> SINGLE;
        public final IRegistryObject<CrateBlock> DOUBLE;
        public final IRegistryObject<CrateBlock> TRIPLE;
        public final IRegistryObject<CrateBlock> QUADRUPLE;
        private final Wood type;

        public CrateGroup(Wood type) {
            this.type = type;

            this.SINGLE = register(type.toString() + "_crate", key -> new CrateBlock(type, CrateLayout.SINGLE, crateProps(type, key)));
            this.DOUBLE = register(type.toString() + "_crate_double", key -> new CrateBlock(type, CrateLayout.DOUBLE, crateProps(type, key)));
            this.TRIPLE = register(type.toString() + "_crate_triple", key -> new CrateBlock(type, CrateLayout.TRIPLE, crateProps(type, key)));
            this.QUADRUPLE = register(type.toString() + "_crate_quadruple", key -> new CrateBlock(type, CrateLayout.QUADRUPLE, crateProps(type, key)));
        }

        private static BlockBehaviour.Properties crateProps(Wood type, ResourceKey<Block> key) {
            BlockState woodState = type.getLog().defaultBlockState();
            MapColor color = woodState.getBlock().defaultMapColor();
            SoundType sound = woodState.getSoundType();
            return BlockBehaviour.Properties.of().setId(key).mapColor(color).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(sound);
        }

        public Wood getType() {
            return type;
        }
    }

    public static void init() {

    }
}
