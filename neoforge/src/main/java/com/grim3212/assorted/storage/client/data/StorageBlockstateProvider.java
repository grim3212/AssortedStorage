package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.lib.client.data.SpecificationBlockStateModelBuilder;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.client.blockentity.item.ItemTowerSpecialRenderer;
import com.grim3212.assorted.storage.client.blockentity.item.LockedChestSpecialRenderer;
import com.grim3212.assorted.storage.client.blockentity.item.LockedShulkerBoxSpecialRenderer;
import com.grim3212.assorted.storage.client.blockentity.WarehouseCrateBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.item.StorageSpecialRenderer;
import com.grim3212.assorted.storage.client.model.StorageModelType;
import com.grim3212.assorted.storage.client.model.StorageModels;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.CrateCompactingBlock;
import com.grim3212.assorted.storage.common.block.CrateControllerBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.StorageBlocks.CrateGroup;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import com.grim3212.assorted.storage.client.properties.HasStorageTagProperty;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item, and
 * {@link StorageItemModelProvider} owns the rest, so the two never write the same file.
 */
public class StorageBlockstateProvider extends ModelProvider {

    // Slots the crate and hopper templates read that vanilla has no constant for. TextureSlot has
    // no equals, so each one has to be created exactly once and shared.
    private static final TextureSlot FACING = TextureSlot.create("facing");
    private static final TextureSlot SIDES = TextureSlot.create("sides");
    private static final TextureSlot EDGES = TextureSlot.create("edges");
    private static final TextureSlot FACING_COLUMNS = TextureSlot.create("facing_columns");
    private static final TextureSlot TOPSIDES = TextureSlot.create("topsides");

    private static final Identifier VANILLA_BLOCK = Identifier.withDefaultNamespace("block/block");
    private static final Identifier VANILLA_HOPPER = Identifier.withDefaultNamespace("block/hopper");
    private static final Identifier VANILLA_HOPPER_SIDE = Identifier.withDefaultNamespace("block/hopper_side");
    private static final Identifier TEMPLATE_HOPPER = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/template_hopper");
    private static final Identifier TEMPLATE_HOPPER_SIDE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/template_hopper_side");

    /**
     * The barrel's own rotation was {@code x = down ? 180 : up ? 0 : 90} and
     * {@code y = vertical ? 0 : (facing.toYRot() + 180) % 360}, which is exactly what
     * {@link BlockModelGenerators#ROTATIONS_COLUMN_WITH_FACING} spells out.
     */
    private static final PropertyDispatch<VariantMutator> BARREL_ROTATION = BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING;

    /**
     * The models with no geometry of their own: the block is drawn by a block entity renderer and
     * the json exists only to name the break/step particle.
     */
    private final Map<Block, Identifier> particleOnly = new LinkedHashMap<>();

    /**
     * Block items drawn by a {@code minecraft:special} renderer rather than a model, on the display
     * transforms of vanilla's {@code minecraft:item/template_chest}.
     */
    private final Map<Block, SpecialModelRenderer.Unbaked<?>> specialItems = new LinkedHashMap<>();

    public StorageBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);

        particle(StorageBlocks.WOOD_CABINET.get(), resource("block/cabinet_break"));
        particle(StorageBlocks.GLASS_CABINET.get(), resource("block/cabinet_break"));
        particle(StorageBlocks.GOLD_SAFE.get(), Identifier.parse("block/gold_block"));
        particle(StorageBlocks.LOCKED_ENDER_CHEST.get(), Identifier.parse("block/obsidian"));
        particle(StorageBlocks.OBSIDIAN_SAFE.get(), Identifier.parse("block/obsidian"));
        particle(StorageBlocks.LOCKER.get(), Identifier.parse("block/iron_block"));
        particle(StorageBlocks.ITEM_TOWER.get(), Identifier.parse("block/iron_block"));
        StorageBlocks.WAREHOUSE_CRATES.forEach((wood, crate) -> particle(crate.get(), Identifier.parse("block/" + wood.getLogTopTextureName())));
        particle(StorageBlocks.LOCKED_CHEST.get(), Identifier.parse("block/oak_planks"));
        particle(StorageBlocks.LOCKED_SHULKER_BOX.get(), Identifier.parse("block/shulker_box"));

        for (IRegistryObject<LockedChestBlock> b : StorageBlocks.CHESTS.values()) {
            particle(b.get(), b.get().getStorageMaterial().getParticle());
        }

        for (IRegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
            particle(b.get(), b.get().getStorageMaterial().getParticle());
        }

        // Which renderer each of those items uses. The textures are the ones the matching block
        // entity renderer resolves, so the item and the placed block look the same.
        special(StorageBlocks.WOOD_CABINET.get(), StorageModelType.CABINET, modelTexture("cabinet"));
        special(StorageBlocks.GLASS_CABINET.get(), StorageModelType.GLASS_CABINET, modelTexture("cabinet"));
        special(StorageBlocks.GOLD_SAFE.get(), StorageModelType.SAFE, modelTexture("gold_safe"));
        special(StorageBlocks.OBSIDIAN_SAFE.get(), StorageModelType.SAFE, modelTexture("obsidian_safe"));
        special(StorageBlocks.LOCKER.get(), StorageModelType.LOCKER, modelTexture("locker"));
        special(StorageBlocks.LOCKED_ENDER_CHEST.get(), StorageModelType.CHEST, modelTexture("locked_ender_chest"));
        StorageBlocks.WAREHOUSE_CRATES.forEach((wood, crate) -> special(crate.get(), StorageModelType.WAREHOUSE_CRATE, WarehouseCrateBlockEntityRenderer.texture(wood)));

        this.specialItems.put(StorageBlocks.ITEM_TOWER.get(), new ItemTowerSpecialRenderer.Unbaked());

        // The locked chests and shulker boxes are textured from their own atlas, so the renderer is
        // handed a sprite path rather than a standalone png.
        this.specialItems.put(StorageBlocks.LOCKED_CHEST.get(), new LockedChestSpecialRenderer.Unbaked(StorageModels.CHEST_LOCATIONS.get(null)));
        this.specialItems.put(StorageBlocks.LOCKED_SHULKER_BOX.get(), new LockedShulkerBoxSpecialRenderer.Unbaked(StorageModels.SHULKER_LOCATIONS.get(null)));

        for (Map.Entry<StorageMaterial, IRegistryObject<LockedChestBlock>> e : StorageBlocks.CHESTS.entrySet()) {
            this.specialItems.put(e.getValue().get(), new LockedChestSpecialRenderer.Unbaked(StorageModels.CHEST_LOCATIONS.get(e.getKey())));
        }

        for (Map.Entry<StorageMaterial, IRegistryObject<LockedShulkerBoxBlock>> e : StorageBlocks.SHULKERS.entrySet()) {
            this.specialItems.put(e.getValue().get(), new LockedShulkerBoxSpecialRenderer.Unbaked(StorageModels.SHULKER_LOCATIONS.get(e.getKey())));
        }
    }

    private void particle(Block block, Identifier texture) {
        this.particleOnly.put(block, texture);
    }

    private void special(Block block, StorageModelType model, Identifier texture) {
        this.specialItems.put(block, new StorageSpecialRenderer.Unbaked(model, texture));
    }

    @Override
    public String getName() {
        return "Assorted Storage block states";
    }

    /**
     * Only the block items belong here; every other item is {@link StorageItemModelProvider}'s, so
     * the two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        this.particleOnly.forEach((block, texture) -> particleOnlyBlock(blockModels, block, texture));
        this.specialItems.forEach((block, renderer) -> specialItem(blockModels, block, renderer));

        locksmithWorkbench(blockModels);

        for (Block door : StorageBlocks.lockedDoors()) {
            door(blockModels, door);
        }

        normalBarrel(blockModels, StorageBlocks.LOCKED_BARREL.get());
        for (IRegistryObject<LockedBarrelBlock> b : StorageBlocks.BARRELS.values()) {
            materialBarrel(blockModels, b.get());
        }

        normalHopper(blockModels, StorageBlocks.LOCKED_HOPPER.get());
        for (IRegistryObject<LockedHopperBlock> b : StorageBlocks.HOPPERS.values()) {
            materialHopper(blockModels, b.get());
        }

        baseCrateModels(blockModels.modelOutput);

        for (CrateGroup group : StorageBlocks.CRATES) {
            storageCrate(blockModels, group.SINGLE.get());
            storageCrate(blockModels, group.DOUBLE.get());
            storageCrate(blockModels, group.TRIPLE.get());
            storageCrate(blockModels, group.QUADRUPLE.get());
        }

        compactingStorageCrate(blockModels, StorageBlocks.CRATE_COMPACTING.get());
        crateController(blockModels);

        Block bridge = StorageBlocks.CRATE_BRIDGE.get();
        MultiVariant bridgeModel = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_ALL.create(bridge, TextureMapping.cube(new Material(resource("block/crates/crate_bridge"))), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(bridge, bridgeModel));
    }

    // ------------------------------------------------------------------ particle-only blocks

    private void particleOnlyBlock(BlockModelGenerators blockModels, Block block, Identifier texture) {
        MultiVariant model = BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create(block, TextureMapping.particle(new Material(texture)), blockModels.modelOutput));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, model));
    }

    private void specialItem(BlockModelGenerators blockModels, Block block, SpecialModelRenderer.Unbaked<?> renderer) {
        Item item = block.asItem();
        Identifier base = ModelTemplates.CHEST_INVENTORY.create(item, TextureMapping.particle(new Material(this.particleOnly.get(block))), blockModels.modelOutput);
        blockModels.itemModelOutput.accept(item, ItemModelUtils.specialModel(base, specialItemTransform(renderer), renderer));
    }

    /**
     * {@code SpecialModelRenderer#submit} applies no transform, so a renderer authored in block-entity
     * space needs the block transform supplied here - as vanilla does in
     * {@code BlockModelGenerators#createShulkerBox}. The other renderers draw in item space already.
     */
    private static Optional<Transformation> specialItemTransform(SpecialModelRenderer.Unbaked<?> renderer) {
        return renderer instanceof LockedShulkerBoxSpecialRenderer.Unbaked
                ? Optional.of(ShulkerBoxRenderer.modelTransform(Direction.UP))
                : Optional.empty();
    }

    // ------------------------------------------------------------------ simple blocks

    private void locksmithWorkbench(BlockModelGenerators blockModels) {
        Block b = StorageBlocks.LOCKSMITH_WORKBENCH.get();
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.PARTICLE, texture("block/locksmith_front"))
                .put(TextureSlot.DOWN, new Material(Identifier.parse("block/oak_planks")))
                .put(TextureSlot.UP, texture("block/locksmith_top"))
                .put(TextureSlot.NORTH, texture("block/locksmith_front"))
                .put(TextureSlot.SOUTH, texture("block/locksmith_side"))
                .put(TextureSlot.EAST, texture("block/locksmith_side"))
                .put(TextureSlot.WEST, texture("block/locksmith_front"));

        MultiVariant model = BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create(b, textures, blockModels.modelOutput));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, model));
    }

    /**
     * Which other locked door a door takes its textures from. A waxed copper door looks exactly like
     * the unwaxed one of the same oxidation stage - vanilla shares those textures too - so only the
     * four unwaxed locked copper doors ship a png.
     */
    private static final Map<Block, Block> DOOR_TEXTURE_SOURCE = new HashMap<>();

    static {
        Blocks.COPPER_DOOR.zipUnwaxedWaxed((unwaxed, waxed) ->
                DOOR_TEXTURE_SOURCE.put(StorageBlocks.VANILLA_DOORS.get(waxed).get(), StorageBlocks.VANILLA_DOORS.get(unwaxed).get()));
    }

    /**
     * A locked door's block state and models. The doors have no item, so vanilla's {@code
     * createDoor}, which also writes an item model, cannot be used.
     */
    private void door(BlockModelGenerators blockModels, Block door) {
        TextureMapping mapping = TextureMapping.door(DOOR_TEXTURE_SOURCE.getOrDefault(door, door));
        MultiVariant bottomLeft = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT.create(door, mapping, blockModels.modelOutput));
        MultiVariant bottomLeftOpen = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(door, mapping, blockModels.modelOutput));
        MultiVariant bottomRight = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT.create(door, mapping, blockModels.modelOutput));
        MultiVariant bottomRightOpen = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(door, mapping, blockModels.modelOutput));
        MultiVariant topLeft = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_LEFT.create(door, mapping, blockModels.modelOutput));
        MultiVariant topLeftOpen = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_LEFT_OPEN.create(door, mapping, blockModels.modelOutput));
        MultiVariant topRight = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_RIGHT.create(door, mapping, blockModels.modelOutput));
        MultiVariant topRightOpen = BlockModelGenerators.plainVariant(ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(door, mapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createDoor(door, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen));
    }

    // ------------------------------------------------------------------ barrels

    private void normalBarrel(BlockModelGenerators blockModels, LockedBarrelBlock b) {
        Identifier unlocked = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_unlocked", barrelTextures(
                new Material(Identifier.parse("block/barrel_side")),
                new Material(Identifier.parse("block/barrel_bottom")),
                new Material(Identifier.parse("block/barrel_top"))), blockModels.modelOutput);
        Identifier locked = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_locked", barrelTextures(
                new Material(Identifier.parse("block/barrel_side")),
                new Material(Identifier.parse("block/barrel_bottom")),
                texture("block/barrels/locked_barrel_top")), blockModels.modelOutput);
        Identifier open = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_open", barrelTextures(
                new Material(Identifier.parse("block/barrel_side")),
                new Material(Identifier.parse("block/barrel_bottom")),
                new Material(Identifier.parse("block/barrel_top_open"))), blockModels.modelOutput);

        barrelState(blockModels, b, unlocked, locked, open, new Material(Identifier.parse("block/barrel_side")));
    }

    private void materialBarrel(BlockModelGenerators blockModels, LockedBarrelBlock b) {
        String material = b.getStorageMaterial().toString();
        Material side = texture("block/barrels/" + material + "/barrel_side");
        Material bottom = texture("block/barrels/" + material + "/barrel_bottom");

        Identifier unlocked = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_unlocked", barrelTextures(side, bottom, texture("block/barrels/" + material + "/barrel_top")), blockModels.modelOutput);
        Identifier locked = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_locked", barrelTextures(side, bottom, texture("block/barrels/" + material + "/locked_barrel_top")), blockModels.modelOutput);
        Identifier open = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_open", barrelTextures(side, bottom, texture("block/barrels/" + material + "/barrel_top_open")), blockModels.modelOutput);

        barrelState(blockModels, b, unlocked, locked, open, side);
    }

    private static TextureMapping barrelTextures(Material side, Material bottom, Material top) {
        return new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, top);
    }

    /**
     * The closed barrel is the {@code assortedstorage:locked} loader model; the open one is plain.
     * The item picks between the locked and unlocked children itself, on the stack's lock, because
     * an item has no block entity for the loader to read.
     */
    private void barrelState(BlockModelGenerators blockModels, LockedBarrelBlock b, Identifier unlocked, Identifier locked, Identifier open, Material particle) {
        MultiVariant closedModel = SpecificationBlockStateModelBuilder.specificationVariant(lockedModel(blockModels.modelOutput, ModelLocationUtils.getModelLocation(b), unlocked, locked, particle));
        MultiVariant openModel = BlockModelGenerators.plainVariant(open);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(LockedBarrelBlock.OPEN, openModel, closedModel))
                .with(BARREL_ROTATION));

        blockModels.itemModelOutput.accept(b.asItem(), ItemModelUtils.conditional(HasStorageTagProperty.LOCKED,
                ItemModelUtils.plainModel(locked), ItemModelUtils.plainModel(unlocked)));
    }

    // ------------------------------------------------------------------ hoppers

    private void normalHopper(BlockModelGenerators blockModels, LockedHopperBlock b) {
        // The unlocked halves are vanilla's own hopper models, referenced rather than regenerated.
        Identifier locked = parented(blockModels.modelOutput, ModelLocationUtils.getModelLocation(b, "_locked"), TEMPLATE_HOPPER);
        Identifier lockedSide = parented(blockModels.modelOutput, ModelLocationUtils.getModelLocation(b, "_locked_side"), TEMPLATE_HOPPER_SIDE);

        hopperState(blockModels, b, VANILLA_HOPPER, locked, VANILLA_HOPPER_SIDE, lockedSide, new Material(Identifier.parse("block/hopper_outside")));
    }

    private void materialHopper(BlockModelGenerators blockModels, LockedHopperBlock b) {
        String material = b.getStorageMaterial().toString();
        Material outside = texture("block/hoppers/" + material + "/hopper_outside");
        Material top = texture("block/hoppers/" + material + "/hopper_top");
        Material inside = texture("block/hoppers/" + material + "/hopper_inside");
        Material lockedOutside = texture("block/hoppers/" + material + "/locked_hopper_outside");

        TextureMapping unlockedTextures = new TextureMapping()
                .put(TextureSlot.PARTICLE, outside)
                .put(TextureSlot.TOP, top)
                .put(TextureSlot.SIDE, outside)
                .put(TextureSlot.INSIDE, inside);
        TextureMapping lockedTextures = unlockedTextures.copyAndUpdate(TOPSIDES, lockedOutside);

        Identifier unlocked = HOPPER_TEXTURED.create(ModelLocationUtils.getModelLocation(b, "_unlocked"), unlockedTextures, blockModels.modelOutput);
        Identifier locked = LOCKED_HOPPER_TEXTURED.create(ModelLocationUtils.getModelLocation(b, "_locked"), lockedTextures, blockModels.modelOutput);
        Identifier unlockedSide = HOPPER_SIDE_TEXTURED.create(ModelLocationUtils.getModelLocation(b, "_unlocked_side"), unlockedTextures, blockModels.modelOutput);
        Identifier lockedSide = LOCKED_HOPPER_SIDE_TEXTURED.create(ModelLocationUtils.getModelLocation(b, "_locked_side"), lockedTextures, blockModels.modelOutput);

        hopperState(blockModels, b, unlocked, locked, unlockedSide, lockedSide, outside);
    }

    private static final ModelTemplate HOPPER_TEXTURED = ExtendedModelTemplateBuilder.builder().parent(VANILLA_HOPPER)
            .requiredTextureSlot(TextureSlot.PARTICLE).requiredTextureSlot(TextureSlot.TOP).requiredTextureSlot(TextureSlot.SIDE).requiredTextureSlot(TextureSlot.INSIDE).build();
    private static final ModelTemplate HOPPER_SIDE_TEXTURED = ExtendedModelTemplateBuilder.builder().parent(VANILLA_HOPPER_SIDE)
            .requiredTextureSlot(TextureSlot.PARTICLE).requiredTextureSlot(TextureSlot.TOP).requiredTextureSlot(TextureSlot.SIDE).requiredTextureSlot(TextureSlot.INSIDE).build();
    private static final ModelTemplate LOCKED_HOPPER_TEXTURED = ExtendedModelTemplateBuilder.builder().parent(TEMPLATE_HOPPER)
            .requiredTextureSlot(TextureSlot.PARTICLE).requiredTextureSlot(TextureSlot.TOP).requiredTextureSlot(TextureSlot.SIDE).requiredTextureSlot(TextureSlot.INSIDE).requiredTextureSlot(TOPSIDES).build();
    private static final ModelTemplate LOCKED_HOPPER_SIDE_TEXTURED = ExtendedModelTemplateBuilder.builder().parent(TEMPLATE_HOPPER_SIDE)
            .requiredTextureSlot(TextureSlot.PARTICLE).requiredTextureSlot(TextureSlot.TOP).requiredTextureSlot(TextureSlot.SIDE).requiredTextureSlot(TextureSlot.INSIDE).requiredTextureSlot(TOPSIDES).build();

    /**
     * {@code ENABLED} is not dispatched on, which is how the 1.20.1 {@code forAllStatesExcept} call
     * expressed the same thing: a property nothing selects on simply does not appear in the
     * blockstate key. The rotations are vanilla's own hopper dispatch.
     */
    private void hopperState(BlockModelGenerators blockModels, LockedHopperBlock b, Identifier unlocked, Identifier locked, Identifier unlockedSide, Identifier lockedSide, Material particle) {
        MultiVariant down = SpecificationBlockStateModelBuilder.specificationVariant(lockedModel(blockModels.modelOutput, ModelLocationUtils.getModelLocation(b), unlocked, locked, particle));
        MultiVariant side = SpecificationBlockStateModelBuilder.specificationVariant(lockedModel(blockModels.modelOutput, ModelLocationUtils.getModelLocation(b, "_side"), unlockedSide, lockedSide, particle));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(BlockStateProperties.FACING_HOPPER)
                        .select(Direction.DOWN, down)
                        .select(Direction.NORTH, side)
                        .select(Direction.EAST, side.with(BlockModelGenerators.Y_ROT_90))
                        .select(Direction.SOUTH, side.with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.WEST, side.with(BlockModelGenerators.Y_ROT_270))));

        // A hopper's item is a flat sprite, not its block model - the default BlockItem fallback
        // would point it at the locked-loader model instead, which is not what 1.20.1 shipped.
        Item item = b.asItem();
        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), blockModels.modelOutput);
        blockModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(itemModel));
    }

    // ------------------------------------------------------------------ crates

    private void storageCrate(BlockModelGenerators blockModels, CrateBlock b) {
        String name = b.getWoodType() != null ? b.getWoodType().toString() : name(b);
        Material sides = b.getWoodType() != null ? new Material(Identifier.parse("block/" + b.getWoodType().getLogTextureName())) : texture("block/crates/" + name + "_sides");
        TextureMapping textures = crateTextures(texture("block/crates/" + name + "_facing"), sides, sides);

        crateState(blockModels, b, b.getLayout(), textures);
    }

    private void compactingStorageCrate(BlockModelGenerators blockModels, CrateCompactingBlock b) {
        TextureMapping textures = crateTextures(
                texture("block/crates/" + name(b) + "_facing"),
                texture("block/crates/crate_bridge"),
                texture("block/crates/crate_compacting_columns"));

        crateState(blockModels, b, b.getLayout(), textures);
    }

    /**
     * The crate texture mapping. {@code facing_columns} names its texture directly, because a
     * texture entry cannot refer to another slot.
     */
    private static TextureMapping crateTextures(Material facing, Material sides, Material facingColumns) {
        return new TextureMapping().put(FACING, facing).put(SIDES, sides).put(FACING_COLUMNS, facingColumns);
    }

    /**
     * A crate's block state: rotated to its facing, with separate models for the two vertical
     * faces. {@code WATERLOGGED} is not dispatched on.
     */
    private void crateState(BlockModelGenerators blockModels, Block b, CrateLayout layout, TextureMapping textures) {
        Identifier horizontal = crateChild(layout, false).create(ModelLocationUtils.getModelLocation(b), textures, blockModels.modelOutput);
        Identifier vertical = crateChild(layout, true).create(ModelLocationUtils.getModelLocation(b, "_vertical"), textures, blockModels.modelOutput);

        MultiVariant flat = BlockModelGenerators.plainVariant(horizontal);
        MultiVariant upright = BlockModelGenerators.plainVariant(vertical);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(CrateBlock.FACING)
                        .select(Direction.DOWN, upright.with(BlockModelGenerators.X_ROT_180))
                        .select(Direction.UP, upright)
                        .select(Direction.NORTH, flat)
                        .select(Direction.SOUTH, flat.with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.WEST, flat.with(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.EAST, flat.with(BlockModelGenerators.Y_ROT_90))));
    }

    private static final Map<String, ModelTemplate> CRATE_CHILDREN = new HashMap<>();

    private static ModelTemplate crateChild(CrateLayout layout, boolean vertical) {
        return CRATE_CHILDREN.computeIfAbsent(layout.getName() + (vertical ? "_vertical" : ""), key -> {
            ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                    .parent(resource("block/base_crate_" + key))
                    .requiredTextureSlot(FACING)
                    .requiredTextureSlot(SIDES);
            if (layout != CrateLayout.SINGLE) {
                builder.requiredTextureSlot(FACING_COLUMNS);
            }
            return builder.build();
        });
    }

    private void crateController(BlockModelGenerators blockModels) {
        Block b = StorageBlocks.CRATE_CONTROLLER.get();
        Material top = texture("block/crates/crate_top");
        Material side = texture("block/crates/crate_controller_side");

        Identifier unlocked = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(b, "_unlocked", orientable(side, texture("block/crates/crate_controller_front"), top), blockModels.modelOutput);
        Identifier locked = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(b, "_locked", orientable(side, texture("block/crates/crate_controller_front_locked"), top), blockModels.modelOutput);

        MultiVariant model = SpecificationBlockStateModelBuilder.specificationVariant(lockedModel(blockModels.modelOutput, ModelLocationUtils.getModelLocation(b), unlocked, locked, texture("block/crates/crate_controller_front")));

        // x = up ? 270 : horizontal ? 0 : 90, y = vertical ? 180 : (facing.toYRot() + 180) % 360.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(CrateControllerBlock.FACING)
                        .select(Direction.DOWN, model.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.UP, model.with(BlockModelGenerators.X_ROT_270).with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.NORTH, model)
                        .select(Direction.SOUTH, model.with(BlockModelGenerators.Y_ROT_180))
                        .select(Direction.WEST, model.with(BlockModelGenerators.Y_ROT_270))
                        .select(Direction.EAST, model.with(BlockModelGenerators.Y_ROT_90))));

        // The item shows the unlocked face rather than the loader model, as it always has.
        blockModels.itemModelOutput.accept(b.asItem(), ItemModelUtils.plainModel(unlocked));
    }

    private static TextureMapping orientable(Material side, Material front, Material top) {
        return new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.FRONT, front).put(TextureSlot.TOP, top);
    }

    // ------------------------------------------------------------------ base crate geometry

    /**
     * The eight parent models the crates share. Each layout adds a column to the one before it, so
     * the horizontal and vertical sets are built cumulatively.
     */
    private void baseCrateModels(BiConsumer<Identifier, ModelInstance> output) {
        for (CrateLayout layout : CrateLayout.values()) {
            ExtendedModelTemplateBuilder flat = baseCrateBuilder();
            ExtendedModelTemplateBuilder upright = baseCrateVerticalBuilder();

            if (layout != CrateLayout.SINGLE) {
                crateColumnWide(flat);
                crateColumnWideVertical(upright);
            }
            if (layout == CrateLayout.TRIPLE || layout == CrateLayout.QUADRUPLE) {
                flat.element(e -> crateColumnNarrow(e, 2, 7));
                upright.element(e -> crateColumnNarrowVertical(e, 9, 14));
            }
            if (layout == CrateLayout.QUADRUPLE) {
                flat.element(e -> crateColumnNarrow(e, 9, 14));
                upright.element(e -> crateColumnNarrowVertical(e, 2, 7));
            }

            TextureMapping textures = new TextureMapping()
                    .put(TextureSlot.PARTICLE, texture("block/crates/crate_top"))
                    .put(TextureSlot.TOP, texture("block/crates/crate_top"))
                    .put(EDGES, texture("block/crates/crate_edges"));

            flat.build().create(resource("block/base_crate_" + layout.getName()), textures, output);
            upright.build().create(resource("block/base_crate_" + layout.getName() + "_vertical"), textures, output);
        }
    }

    private static ExtendedModelTemplateBuilder baseCrateBuilder() {
        return ExtendedModelTemplateBuilder.builder()
                .parent(VANILLA_BLOCK)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.TOP)
                .requiredTextureSlot(EDGES)
                .element(e -> e.from(1, 1, 1).to(15, 15, 15)
                        .face(Direction.NORTH, f -> f.uvs(1, 1, 15, 15).texture(FACING))
                        .face(Direction.EAST, f -> f.uvs(1, 1, 15, 15).texture(SIDES))
                        .face(Direction.SOUTH, f -> f.uvs(1, 1, 15, 15).texture(SIDES))
                        .face(Direction.WEST, f -> f.uvs(1, 1, 15, 15).texture(SIDES)))
                .element(e -> e.from(0, 0, 0).to(16, 2, 16)
                        .face(Direction.NORTH, f -> f.uvs(0, 0, 16, 2).texture(EDGES).cullface(Direction.NORTH))
                        .face(Direction.EAST, f -> f.uvs(0, 0, 16, 2).texture(EDGES).cullface(Direction.EAST))
                        .face(Direction.SOUTH, f -> f.uvs(0, 0, 16, 2).texture(EDGES).cullface(Direction.SOUTH))
                        .face(Direction.WEST, f -> f.uvs(0, 0, 16, 2).texture(EDGES).cullface(Direction.WEST))
                        .face(Direction.UP, f -> f.uvs(0, 0, 16, 2).texture(TextureSlot.TOP))
                        .face(Direction.DOWN, f -> f.uvs(0, 0, 16, 2).texture(TextureSlot.TOP).cullface(Direction.DOWN)))
                .element(e -> e.from(0, 14, 0).to(16, 16, 16)
                        .face(Direction.NORTH, f -> f.uvs(0, 2, 16, 0).texture(EDGES).cullface(Direction.NORTH))
                        .face(Direction.EAST, f -> f.uvs(0, 2, 16, 0).texture(EDGES).cullface(Direction.EAST))
                        .face(Direction.SOUTH, f -> f.uvs(0, 2, 16, 0).texture(EDGES).cullface(Direction.SOUTH))
                        .face(Direction.WEST, f -> f.uvs(0, 2, 16, 0).texture(EDGES).cullface(Direction.WEST))
                        .face(Direction.UP, f -> f.uvs(16, 16, 0, 0).texture(TextureSlot.TOP).cullface(Direction.UP))
                        .face(Direction.DOWN, f -> f.uvs(16, 16, 0, 0).texture(TextureSlot.TOP)))
                .element(e -> e.from(0, 2, 14).to(2, 14, 16)
                        .face(Direction.NORTH, f -> f.uvs(10, 2, 8, 14).texture(SIDES))
                        .face(Direction.EAST, f -> f.uvs(12, 2, 10, 14).texture(SIDES))
                        .face(Direction.SOUTH, f -> f.uvs(14, 2, 12, 14).texture(SIDES).cullface(Direction.SOUTH))
                        .face(Direction.WEST, f -> f.uvs(8, 2, 6, 14).texture(SIDES).cullface(Direction.WEST)))
                .element(e -> e.from(0, 2, 0).to(2, 14, 2)
                        .face(Direction.NORTH, f -> f.uvs(4, 2, 6, 14).texture(SIDES).cullface(Direction.NORTH))
                        .face(Direction.EAST, f -> f.uvs(2, 2, 4, 14).texture(SIDES))
                        .face(Direction.SOUTH, f -> f.uvs(8, 2, 10, 14).texture(SIDES))
                        .face(Direction.WEST, f -> f.uvs(6, 2, 8, 14).texture(SIDES).cullface(Direction.WEST)))
                .element(e -> e.from(14, 2, 14).to(16, 14, 16)
                        .face(Direction.NORTH, f -> f.uvs(8, 2, 10, 14).texture(SIDES))
                        .face(Direction.EAST, f -> f.uvs(6, 2, 8, 14).texture(SIDES).cullface(Direction.EAST))
                        .face(Direction.SOUTH, f -> f.uvs(12, 2, 14, 14).texture(SIDES).cullface(Direction.SOUTH))
                        .face(Direction.WEST, f -> f.uvs(10, 2, 12, 14).texture(SIDES)))
                .element(e -> e.from(14, 2, 0).to(16, 14, 2)
                        .face(Direction.NORTH, f -> f.uvs(6, 2, 4, 14).texture(SIDES).cullface(Direction.NORTH))
                        .face(Direction.EAST, f -> f.uvs(8, 2, 6, 14).texture(SIDES).cullface(Direction.EAST))
                        .face(Direction.SOUTH, f -> f.uvs(10, 2, 8, 14).texture(SIDES))
                        .face(Direction.WEST, f -> f.uvs(4, 2, 2, 14).texture(SIDES)));
    }

    private static ExtendedModelTemplateBuilder baseCrateVerticalBuilder() {
        return ExtendedModelTemplateBuilder.builder()
                .parent(VANILLA_BLOCK)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.TOP)
                .requiredTextureSlot(EDGES)
                .element(e -> e.from(1, 1, 1).to(15, 15, 15)
                        .face(Direction.EAST, f -> f.uvs(1, 1, 15, 15).rotation(Quadrant.R270).texture(SIDES))
                        .face(Direction.WEST, f -> f.uvs(1, 1, 15, 15).rotation(Quadrant.R90).texture(SIDES))
                        .face(Direction.UP, f -> f.uvs(1, 1, 15, 15).rotation(Quadrant.R180).texture(FACING))
                        .face(Direction.DOWN, f -> f.uvs(1, 1, 15, 15).texture(SIDES)))
                .element(e -> e.from(0, 0, 0).to(16, 16, 2)
                        .face(Direction.NORTH, f -> f.uvs(16, 0, 0, 16).rotation(Quadrant.R180).texture(TextureSlot.TOP).cullface(Direction.NORTH))
                        .face(Direction.EAST, f -> f.uvs(0, 0, 16, 2).rotation(Quadrant.R270).texture(EDGES).cullface(Direction.EAST))
                        .face(Direction.SOUTH, f -> f.uvs(16, 16, 0, 0).texture(TextureSlot.TOP))
                        .face(Direction.WEST, f -> f.uvs(0, 0, 16, 2).rotation(Quadrant.R90).texture(EDGES).cullface(Direction.WEST))
                        .face(Direction.UP, f -> f.uvs(0, 0, 16, 2).rotation(Quadrant.R180).texture(EDGES).cullface(Direction.UP))
                        .face(Direction.DOWN, f -> f.uvs(0, 0, 16, 2).texture(EDGES).cullface(Direction.DOWN)))
                .element(e -> e.from(0, 0, 14).to(16, 16, 16)
                        .face(Direction.NORTH, f -> f.uvs(16, 16, 0, 0).rotation(Quadrant.R180).texture(TextureSlot.TOP))
                        .face(Direction.EAST, f -> f.uvs(0, 2, 16, 0).rotation(Quadrant.R270).texture(EDGES).cullface(Direction.EAST))
                        .face(Direction.SOUTH, f -> f.uvs(16, 16, 0, 0).texture(TextureSlot.TOP).cullface(Direction.SOUTH))
                        .face(Direction.WEST, f -> f.uvs(0, 2, 16, 0).rotation(Quadrant.R90).texture(EDGES).cullface(Direction.WEST))
                        .face(Direction.UP, f -> f.uvs(0, 2, 16, 0).rotation(Quadrant.R180).texture(EDGES).cullface(Direction.UP))
                        .face(Direction.DOWN, f -> f.uvs(0, 2, 16, 0).texture(EDGES).cullface(Direction.DOWN)))
                .element(e -> e.from(0, 0, 2).to(2, 2, 14)
                        .face(Direction.EAST, f -> f.uvs(12, 2, 10, 14).rotation(Quadrant.R270).texture(SIDES))
                        .face(Direction.WEST, f -> f.uvs(8, 2, 6, 14).rotation(Quadrant.R90).texture(SIDES).cullface(Direction.WEST))
                        .face(Direction.UP, f -> f.uvs(10, 2, 8, 14).rotation(Quadrant.R180).texture(SIDES))
                        .face(Direction.DOWN, f -> f.uvs(14, 2, 12, 14).texture(SIDES).cullface(Direction.DOWN)))
                .element(e -> e.from(0, 14, 2).to(2, 16, 14)
                        .face(Direction.EAST, f -> f.uvs(2, 2, 4, 14).rotation(Quadrant.R270).texture(SIDES))
                        .face(Direction.WEST, f -> f.uvs(6, 2, 8, 14).rotation(Quadrant.R90).texture(SIDES).cullface(Direction.WEST))
                        .face(Direction.UP, f -> f.uvs(4, 2, 6, 14).rotation(Quadrant.R180).texture(SIDES).cullface(Direction.UP))
                        .face(Direction.DOWN, f -> f.uvs(8, 2, 10, 14).texture(SIDES)))
                .element(e -> e.from(14, 0, 2).to(16, 2, 14)
                        .face(Direction.EAST, f -> f.uvs(6, 2, 8, 14).rotation(Quadrant.R270).texture(SIDES).cullface(Direction.EAST))
                        .face(Direction.WEST, f -> f.uvs(10, 2, 12, 14).rotation(Quadrant.R90).texture(SIDES))
                        .face(Direction.UP, f -> f.uvs(8, 2, 10, 14).rotation(Quadrant.R180).texture(SIDES))
                        .face(Direction.DOWN, f -> f.uvs(12, 2, 14, 14).texture(SIDES).cullface(Direction.DOWN)))
                .element(e -> e.from(14, 14, 2).to(16, 16, 14)
                        .face(Direction.EAST, f -> f.uvs(8, 2, 6, 14).rotation(Quadrant.R270).texture(SIDES).cullface(Direction.EAST))
                        .face(Direction.WEST, f -> f.uvs(4, 2, 2, 14).rotation(Quadrant.R90).texture(SIDES))
                        .face(Direction.UP, f -> f.uvs(6, 2, 4, 14).rotation(Quadrant.R180).texture(SIDES).cullface(Direction.UP))
                        .face(Direction.DOWN, f -> f.uvs(10, 2, 8, 14).texture(SIDES)));
    }

    private static void crateColumnWide(ExtendedModelTemplateBuilder builder) {
        builder.element(e -> e.from(2, 7, 0).to(14, 9, 1)
                .face(Direction.NORTH, f -> f.uvs(0, 0, 12, 2).texture(FACING_COLUMNS).cullface(Direction.NORTH))
                .face(Direction.UP, f -> f.uvs(0, 0, 12, 1).texture(FACING_COLUMNS))
                .face(Direction.DOWN, f -> f.uvs(0, 0, 12, 1).texture(FACING_COLUMNS)));
    }

    private static void crateColumnWideVertical(ExtendedModelTemplateBuilder builder) {
        builder.element(e -> e.from(2, 15, 7).to(14, 16, 9)
                .face(Direction.NORTH, f -> f.uvs(0, 0, 12, 1).texture(FACING_COLUMNS))
                .face(Direction.SOUTH, f -> f.uvs(0, 0, 12, 1).texture(FACING_COLUMNS))
                .face(Direction.UP, f -> f.uvs(0, 0, 12, 2).texture(FACING_COLUMNS).cullface(Direction.UP)));
    }

    private static void crateColumnNarrow(ElementBuilder e, int fromY, int toY) {
        e.from(7, fromY, 0).to(9, toY, 1)
                .face(Direction.NORTH, f -> f.uvs(4, 0, 2, 5).texture(FACING_COLUMNS).cullface(Direction.NORTH))
                .face(Direction.EAST, f -> f.uvs(4, 0, 1, 5).texture(FACING_COLUMNS))
                .face(Direction.WEST, f -> f.uvs(4, 0, 1, 5).texture(FACING_COLUMNS));
    }

    private static void crateColumnNarrowVertical(ElementBuilder e, int fromZ, int toZ) {
        e.from(7, 15, fromZ).to(9, 16, toZ)
                .face(Direction.EAST, f -> f.uvs(4, 0, 5, 1).texture(FACING_COLUMNS))
                .face(Direction.WEST, f -> f.uvs(4, 0, 5, 1).texture(FACING_COLUMNS))
                .face(Direction.UP, f -> f.uvs(4, 0, 2, 5).texture(FACING_COLUMNS).cullface(Direction.UP));
    }

    // ------------------------------------------------------------------ helpers

    /**
     * Writes a model whose whole body is the {@code assortedstorage:locked} loader block. The loader
     * replaces the geometry outright, so the template declares no slots and no elements.
     */
    private static Identifier lockedModel(BiConsumer<Identifier, ModelInstance> output, Identifier target, Identifier unlocked, Identifier locked, Material particle) {
        return ExtendedModelTemplateBuilder.builder()
                // A block item reads its transforms from the block model's parent chain
                // (ResolvedModel#getTopTransforms); parenting to nothing yields ItemTransforms.NO_TRANSFORMS.
                // block/block supplies the standard display block and gui_light, and no geometry.
                .parent(Identifier.withDefaultNamespace("block/block"))
                .customLoader(LockedModelBuilder::begin, b -> b.unlockedModel(unlocked).lockedModel(locked))
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .build()
                .create(target, new TextureMapping().put(TextureSlot.PARTICLE, particle), output);
    }

    /** A model that is nothing but a parent reference, which is what {@code withExistingParent} was. */
    private static Identifier parented(BiConsumer<Identifier, ModelInstance> output, Identifier target, Identifier parent) {
        return ExtendedModelTemplateBuilder.builder().parent(parent).build().create(target, new TextureMapping(), output);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Material texture(String path) {
        return new Material(resource(path));
    }

    private static Identifier modelTexture(String name) {
        return resource("textures/model/" + name + ".png");
    }
}
