package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.util.CrateLayout;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageBlockstateProvider extends BlockStateProvider {

	private final LockedModelProvider loaderModels;

	private final Map<Block, ResourceLocation> blocks;

	private static final ResourceLocation CUTOUT_RENDER_TYPE = new ResourceLocation("minecraft:cutout");

	public StorageBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper, LockedModelProvider loader) {
		super(output, AssortedStorage.MODID, exFileHelper);
		this.loaderModels = loader;

		this.blocks = new HashMap<>();

		blocks.put(StorageBlocks.WOOD_CABINET.get(), new ResourceLocation(AssortedStorage.MODID, "block/cabinet_break"));
		blocks.put(StorageBlocks.GLASS_CABINET.get(), new ResourceLocation(AssortedStorage.MODID, "block/cabinet_break"));
		blocks.put(StorageBlocks.GOLD_SAFE.get(), new ResourceLocation("block/gold_block"));
		blocks.put(StorageBlocks.LOCKED_ENDER_CHEST.get(), new ResourceLocation("block/obsidian"));
		blocks.put(StorageBlocks.OBSIDIAN_SAFE.get(), new ResourceLocation("block/obsidian"));
		blocks.put(StorageBlocks.LOCKER.get(), new ResourceLocation("block/iron_block"));
		blocks.put(StorageBlocks.ITEM_TOWER.get(), new ResourceLocation("block/iron_block"));
		blocks.put(StorageBlocks.OAK_WAREHOUSE_CRATE.get(), new ResourceLocation("block/oak_log_top"));
		blocks.put(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get(), new ResourceLocation("block/birch_log_top"));
		blocks.put(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get(), new ResourceLocation("block/spruce_log_top"));
		blocks.put(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get(), new ResourceLocation("block/acacia_log_top"));
		blocks.put(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get(), new ResourceLocation("block/dark_oak_log_top"));
		blocks.put(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get(), new ResourceLocation("block/jungle_log_top"));
		blocks.put(StorageBlocks.WARPED_WAREHOUSE_CRATE.get(), new ResourceLocation("block/warped_stem_top"));
		blocks.put(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get(), new ResourceLocation("block/crimson_stem_top"));
		blocks.put(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get(), new ResourceLocation("block/mangrove_log_top"));
		blocks.put(StorageBlocks.LOCKED_CHEST.get(), new ResourceLocation("block/oak_planks"));
		blocks.put(StorageBlocks.LOCKED_SHULKER_BOX.get(), new ResourceLocation("block/shulker_box"));

		for (RegistryObject<LockedChestBlock> b : StorageBlocks.CHESTS.values()) {
			blocks.put(b.get(), b.get().getStorageMaterial().getParticle());
		}

		for (RegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
			blocks.put(b.get(), b.get().getStorageMaterial().getParticle());
		}
	}

	@Override
	public String getName() {
		return "Assorted Storage block states";
	}

	@Override
	protected void registerStatesAndModels() {
		blocks.forEach((block, tex) -> particleOnly(block, tex));

		ModelFile model = models().cube(prefix("locksmith_workbench"), new ResourceLocation("block/oak_planks"), new ResourceLocation(prefix("block/locksmith_top")), new ResourceLocation(prefix("block/locksmith_front")), new ResourceLocation(prefix("block/locksmith_side")), new ResourceLocation(prefix("block/locksmith_side")), new ResourceLocation(prefix("block/locksmith_front"))).texture("particle", prefix("block/locksmith_front"));
		simpleBlock(StorageBlocks.LOCKSMITH_WORKBENCH.get(), model);
		genericBlock(StorageBlocks.LOCKSMITH_WORKBENCH.get());

		door(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), resource("block/locked_quartz_door_bottom"), resource("block/locked_quartz_door_top"));
		door(StorageBlocks.LOCKED_GLASS_DOOR.get(), resource("block/locked_glass_door_bottom"), resource("block/locked_glass_door_top"));
		door(StorageBlocks.LOCKED_STEEL_DOOR.get(), resource("block/locked_steel_door_bottom"), resource("block/locked_steel_door_top"));
		door(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), resource("block/locked_chain_link_door_bottom"), resource("block/locked_chain_link_door_top"));
		door(StorageBlocks.LOCKED_OAK_DOOR.get(), resource("block/locked_oak_door_bottom"), resource("block/locked_oak_door_top"));
		door(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), resource("block/locked_spruce_door_bottom"), resource("block/locked_spruce_door_top"));
		door(StorageBlocks.LOCKED_BIRCH_DOOR.get(), resource("block/locked_birch_door_bottom"), resource("block/locked_birch_door_top"));
		door(StorageBlocks.LOCKED_ACACIA_DOOR.get(), resource("block/locked_acacia_door_bottom"), resource("block/locked_acacia_door_top"));
		door(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), resource("block/locked_jungle_door_bottom"), resource("block/locked_jungle_door_top"));
		door(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), resource("block/locked_dark_oak_door_bottom"), resource("block/locked_dark_oak_door_top"));
		door(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), resource("block/locked_crimson_door_bottom"), resource("block/locked_crimson_door_top"));
		door(StorageBlocks.LOCKED_MANGROVE_DOOR.get(), resource("block/locked_mangrove_door_bottom"), resource("block/locked_mangrove_door_top"));
		door(StorageBlocks.LOCKED_WARPED_DOOR.get(), resource("block/locked_warped_door_bottom"), resource("block/locked_warped_door_top"));
		door(StorageBlocks.LOCKED_IRON_DOOR.get(), resource("block/locked_iron_door_bottom"), resource("block/locked_iron_door_top"));

		createNormalBarrel(StorageBlocks.LOCKED_BARREL.get());
		for (RegistryObject<LockedBarrelBlock> b : StorageBlocks.BARRELS.values()) {
			createMaterialBarrel(b.get());
		}

		createNormalHopper(StorageBlocks.LOCKED_HOPPER.get());
		for (RegistryObject<LockedHopperBlock> b : StorageBlocks.HOPPERS.values()) {
			createMaterialHopper(b.get());
		}

		createBaseStorageCrateModels();

		createStorageCrate(StorageBlocks.CRATE.get());
		createStorageCrate(StorageBlocks.CRATE_DOUBLE.get());
		createStorageCrate(StorageBlocks.CRATE_TRIPLE.get());
		createStorageCrate(StorageBlocks.CRATE_QUADRUPLE.get());

		createStorageCrate(StorageBlocks.CRATE_COMPACTING.get());

		this.createCrateController();

		simpleBlock(StorageBlocks.CRATE_BRIDGE.get(), models().cubeAll(name(StorageBlocks.CRATE_BRIDGE.get()), resource("block/crates/crate_bridge")));
		genericBlock(StorageBlocks.CRATE_BRIDGE.get());

		this.loaderModels.previousModels();
	}

	private void door(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
		doorBlockCutoutInternal(block, ForgeRegistries.BLOCKS.getKey(block).toString(), bottom, top);
	}

	private void doorBlockCutoutInternal(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
		ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom_left", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile bottomLeftOpen = models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_right", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile bottomRightOpen = models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile topLeft = models().doorTopLeft(baseName + "_top_left", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile topLeftOpen = models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile topRight = models().doorTopRight(baseName + "_top_right", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		ModelFile topRightOpen = models().doorTopRightOpen(baseName + "_top_right_open", bottom, top).renderType(CUTOUT_RENDER_TYPE);
		doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
	}

	private void createCrateController() {
		Block b = StorageBlocks.CRATE_CONTROLLER.get();
		String controllerName = name(b);
		ResourceLocation controllerTop = resource("block/crates/crate_controller_top");
		ResourceLocation controllerSide = resource("block/crates/crate_controller_side");
		ResourceLocation controllerFront = resource("block/crates/crate_controller_front");
		ResourceLocation controllerFrontLocked = resource("block/crates/crate_controller_front_locked");
		ModelFile controllerUnlockedModel = models().orientable(controllerName + "_unlocked", controllerSide, controllerFront, controllerTop);
		ModelFile controllerLockedModel = models().orientable(controllerName + "_locked", controllerSide, controllerFrontLocked, controllerTop);

		LockedModelBuilder controllerModelBuilder = this.loaderModels.getBuilder(controllerName).unlockedModel(controllerUnlockedModel.getLocation()).lockedModel(controllerLockedModel.getLocation());

		getVariantBuilder(b).forAllStates(state -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			return ConfiguredModel.builder().modelFile(controllerModelBuilder).rotationX(dir == Direction.UP ? 270 : dir.getAxis().isHorizontal() ? 0 : 90).rotationY(dir.getAxis().isVertical() ? 180 : (((int) dir.toYRot()) + 180) % 360).build();
		});
		itemModels().getBuilder(controllerName).parent(controllerUnlockedModel);
	}

	private void createStorageCrate(CrateBlock b) {
		CrateLayout layout = b.getLayout();

		//@formatter:off
		BlockModelBuilder storageCrateModel = models().withExistingParent(name(b), prefix("block/base_crate_" + layout.getName()))
				.texture("inner_facing",prefix("block/crates/inner_facing"))
				.texture("inner_sides",prefix("block/crates/inner_sides"))
				.texture("top_bottom_edges",prefix("block/crates/top_bottom_edges"))
				.texture("top_bottom",prefix("block/crates/top_bottom"))
				.texture("columns",prefix("block/crates/columns"));
		
		BlockModelBuilder storageCrateVericalModel = models().withExistingParent(name(b) + "_vertical", prefix("block/base_crate_" + layout.getName() + "_vertical"))
				.texture("inner_facing",prefix("block/crates/inner_facing"))
				.texture("inner_sides",prefix("block/crates/inner_sides"))
				.texture("top_bottom_edges",prefix("block/crates/top_bottom_edges"))
				.texture("top_bottom",prefix("block/crates/top_bottom"))
				.texture("columns",prefix("block/crates/columns"));
		//@formatter:on

		if (layout != CrateLayout.SINGLE) {
			storageCrateModel.texture("facing_columns", prefix("block/crates/facing_columns"));
			storageCrateVericalModel.texture("facing_columns", prefix("block/crates/facing_columns"));
		}

		Function<BlockState, ModelFile> modelFunc = (state) -> {
			return state.getValue(CrateBlock.FACING).getAxis().isVertical() ? storageCrateVericalModel : storageCrateModel;
		};

		getVariantBuilder(b).forAllStatesExcept(state -> {
			Direction dir = state.getValue(CrateBlock.FACING);
			return ConfiguredModel.builder().modelFile(modelFunc.apply(state)).rotationX(dir == Direction.DOWN ? 180 : 0).rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360).build();
		}, CrateBlock.WATERLOGGED);

		itemModels().getBuilder(name(b)).parent(storageCrateModel);
	}

	private void createBaseStorageCrateModels() {
		//@formatter:off
		models().getBuilder(prefix("block/base_crate_single")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.NORTH).uvs(1, 1, 15, 15).texture("#inner_facing").end()
				.face(Direction.EAST).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.SOUTH).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 2, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).texture("#top_bottom").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 0).to(16, 16, 16)
				.face(Direction.NORTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(16, 16, 0, 0).texture("#top_bottom").end().end()
				.element().from(0, 2, 14).to(2, 14, 16)
				.face(Direction.NORTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(12, 2, 10, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(0, 2, 0).to(2, 14, 2)
				.face(Direction.NORTH).uvs(4, 2, 6, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(2, 2, 4, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(14, 2, 14).to(16, 14, 16)
				.face(Direction.NORTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).texture("#columns").end().end()
				.element().from(14, 2, 0).to(16, 14, 2)
				.face(Direction.NORTH).uvs(6, 2, 4, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).texture("#columns").end().end();
		
		
		models().getBuilder(prefix("block/base_crate_single_vertical")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.EAST).uvs(1, 1, 15, 15).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).rotation(FaceRotation.CLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).rotation(FaceRotation.UPSIDE_DOWN).texture("#inner_facing").end()
				.face(Direction.DOWN).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 16, 2)
				.face(Direction.NORTH).uvs(16, 0, 0, 16).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 14).to(16, 16, 16)
				.face(Direction.NORTH).uvs(16, 16, 0, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 2, 16, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 2).to(2, 2, 14)
				.face(Direction.EAST).uvs(12, 2, 10, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(10, 2, 8, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 2).to(2, 16, 14)
				.face(Direction.EAST).uvs(2, 2, 4, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(4, 2, 6, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(8, 2, 10, 14).texture("#columns").end().end()
				.element().from(14, 0, 2).to(16, 2, 14)
				.face(Direction.EAST).uvs(6, 2, 8, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(8, 2, 10, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(14, 14, 2).to(16, 16, 14)
				.face(Direction.EAST).uvs(8, 2, 6, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(6, 2, 4, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(10, 2, 8, 14).texture("#columns").end().end();
		
		models().getBuilder(prefix("block/base_crate_double")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.NORTH).uvs(1, 1, 15, 15).texture("#inner_facing").end()
				.face(Direction.EAST).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.SOUTH).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 2, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).texture("#top_bottom").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 0).to(16, 16, 16)
				.face(Direction.NORTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(16, 16, 0, 0).texture("#top_bottom").end().end()
				.element().from(0, 2, 14).to(2, 14, 16)
				.face(Direction.NORTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(12, 2, 10, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(0, 2, 0).to(2, 14, 2)
				.face(Direction.NORTH).uvs(4, 2, 6, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(2, 2, 4, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(14, 2, 14).to(16, 14, 16)
				.face(Direction.NORTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).texture("#columns").end().end()
				.element().from(14, 2, 0).to(16, 14, 2)
				.face(Direction.NORTH).uvs(6, 2, 4, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).texture("#columns").end().end()
				.element().from(2, 7, 0).to(14, 9, 1)
				.face(Direction.NORTH).uvs(0, 0, 12, 2).texture("#facing_columns").cullface(Direction.NORTH).end()
				.face(Direction.UP).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.DOWN).uvs(0, 0, 12, 1).texture("#facing_columns").end().end();
	
	
		models().getBuilder(prefix("block/base_crate_double_vertical")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.EAST).uvs(1, 1, 15, 15).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).rotation(FaceRotation.CLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).rotation(FaceRotation.UPSIDE_DOWN).texture("#inner_facing").end()
				.face(Direction.DOWN).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 16, 2)
				.face(Direction.NORTH).uvs(16, 0, 0, 16).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 14).to(16, 16, 16)
				.face(Direction.NORTH).uvs(16, 16, 0, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 2, 16, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 2).to(2, 2, 14)
				.face(Direction.EAST).uvs(12, 2, 10, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(10, 2, 8, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 2).to(2, 16, 14)
				.face(Direction.EAST).uvs(2, 2, 4, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(4, 2, 6, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(8, 2, 10, 14).texture("#columns").end().end()
				.element().from(14, 0, 2).to(16, 2, 14)
				.face(Direction.EAST).uvs(6, 2, 8, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(8, 2, 10, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(14, 14, 2).to(16, 16, 14)
				.face(Direction.EAST).uvs(8, 2, 6, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(6, 2, 4, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(10, 2, 8, 14).texture("#columns").end().end()
				.element().from(2, 15, 7).to(14, 16, 9)
				.face(Direction.NORTH).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.SOUTH).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.UP).uvs(0, 0, 12, 2).texture("#facing_columns").cullface(Direction.UP).end().end();
		
		models().getBuilder(prefix("block/base_crate_triple")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.NORTH).uvs(1, 1, 15, 15).texture("#inner_facing").end()
				.face(Direction.EAST).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.SOUTH).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 2, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).texture("#top_bottom").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 0).to(16, 16, 16)
				.face(Direction.NORTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(16, 16, 0, 0).texture("#top_bottom").end().end()
				.element().from(0, 2, 14).to(2, 14, 16)
				.face(Direction.NORTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(12, 2, 10, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(0, 2, 0).to(2, 14, 2)
				.face(Direction.NORTH).uvs(4, 2, 6, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(2, 2, 4, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(14, 2, 14).to(16, 14, 16)
				.face(Direction.NORTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).texture("#columns").end().end()
				.element().from(14, 2, 0).to(16, 14, 2)
				.face(Direction.NORTH).uvs(6, 2, 4, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).texture("#columns").end().end()
				.element().from(2, 7, 0).to(14, 9, 1)
				.face(Direction.NORTH).uvs(0, 0, 12, 2).texture("#facing_columns").cullface(Direction.NORTH).end()
				.face(Direction.UP).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.DOWN).uvs(0, 0, 12, 1).texture("#facing_columns").end().end()
				.element().from(7, 2, 0).to(9, 7, 1)
				.face(Direction.NORTH).uvs(0, 0, 2, 5).texture("#facing_columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 1, 5).texture("#facing_columns").end()
				.face(Direction.WEST).uvs(0, 0, 1, 5).texture("#facing_columns").end().end();
		
		
		models().getBuilder(prefix("block/base_crate_triple_vertical")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.EAST).uvs(1, 1, 15, 15).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).rotation(FaceRotation.CLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).rotation(FaceRotation.UPSIDE_DOWN).texture("#inner_facing").end()
				.face(Direction.DOWN).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 16, 2)
				.face(Direction.NORTH).uvs(16, 0, 0, 16).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 14).to(16, 16, 16)
				.face(Direction.NORTH).uvs(16, 16, 0, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 2, 16, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 2).to(2, 2, 14)
				.face(Direction.EAST).uvs(12, 2, 10, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(10, 2, 8, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 2).to(2, 16, 14)
				.face(Direction.EAST).uvs(2, 2, 4, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(4, 2, 6, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(8, 2, 10, 14).texture("#columns").end().end()
				.element().from(14, 0, 2).to(16, 2, 14)
				.face(Direction.EAST).uvs(6, 2, 8, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(8, 2, 10, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(14, 14, 2).to(16, 16, 14)
				.face(Direction.EAST).uvs(8, 2, 6, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(6, 2, 4, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(10, 2, 8, 14).texture("#columns").end().end()
				.element().from(2, 15, 7).to(14, 16, 9)
				.face(Direction.NORTH).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.SOUTH).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.UP).uvs(0, 0, 12, 2).texture("#facing_columns").cullface(Direction.UP).end().end()
				.element().from(7, 15, 9).to(9, 16, 14)
				.face(Direction.EAST).uvs(0, 0, 5, 1).texture("#facing_columns").end()
				.face(Direction.WEST).uvs(0, 0, 5, 1).texture("#facing_columns").end()
				.face(Direction.UP).uvs(0, 0, 2, 5).texture("#facing_columns").cullface(Direction.UP).end().end();
		
		models().getBuilder(prefix("block/base_crate_quadruple")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.NORTH).uvs(1, 1, 15, 15).texture("#inner_facing").end()
				.face(Direction.EAST).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.SOUTH).uvs(1, 1, 15, 15).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 2, 16)
				.face(Direction.NORTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).texture("#top_bottom").end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 0).to(16, 16, 16)
				.face(Direction.NORTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(16, 16, 0, 0).texture("#top_bottom").end().end()
				.element().from(0, 2, 14).to(2, 14, 16)
				.face(Direction.NORTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(12, 2, 10, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(0, 2, 0).to(2, 14, 2)
				.face(Direction.NORTH).uvs(4, 2, 6, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(2, 2, 4, 14).texture("#columns").end()
				.face(Direction.SOUTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.WEST).end().end()
				.element().from(14, 2, 14).to(16, 14, 16)
				.face(Direction.NORTH).uvs(8, 2, 10, 14).texture("#columns").end()
				.face(Direction.EAST).uvs(6, 2, 8, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).texture("#columns").end().end()
				.element().from(14, 2, 0).to(16, 14, 2)
				.face(Direction.NORTH).uvs(6, 2, 4, 14).texture("#columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(8, 2, 6, 14).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(10, 2, 8, 14).texture("#columns").end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).texture("#columns").end().end()
				.element().from(2, 7, 0).to(14, 9, 1)
				.face(Direction.NORTH).uvs(0, 0, 12, 2).texture("#facing_columns").cullface(Direction.NORTH).end()
				.face(Direction.UP).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.DOWN).uvs(0, 0, 12, 1).texture("#facing_columns").end().end()
				.element().from(7, 2, 0).to(9, 7, 1)
				.face(Direction.NORTH).uvs(0, 0, 2, 5).texture("#facing_columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 1, 5).texture("#facing_columns").end()
				.face(Direction.WEST).uvs(0, 0, 1, 5).texture("#facing_columns").end().end()
				.element().from(7, 9, 0).to(9, 14, 1)
				.face(Direction.NORTH).uvs(0, 0, 2, 5).texture("#facing_columns").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 1, 5).texture("#facing_columns").end()
				.face(Direction.WEST).uvs(0, 0, 1, 5).texture("#facing_columns").end().end();
		
		
		models().getBuilder(prefix("block/base_crate_quadruple_vertical")).texture("particle", "#top_bottom")
				.parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
				.element().from(1, 1, 1).to(15, 15, 15)
				.face(Direction.EAST).uvs(1, 1, 15, 15).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.WEST).uvs(1, 1, 15, 15).rotation(FaceRotation.CLOCKWISE_90).texture("#inner_sides").end()
				.face(Direction.UP).uvs(1, 1, 15, 15).rotation(FaceRotation.UPSIDE_DOWN).texture("#inner_facing").end()
				.face(Direction.DOWN).uvs(1, 1, 15, 15).texture("#inner_sides").end().end()
				.element().from(0, 0, 0).to(16, 16, 2)
				.face(Direction.NORTH).uvs(16, 0, 0, 16).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").cullface(Direction.NORTH).end()
				.face(Direction.EAST).uvs(0, 0, 16, 2).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").end()
				.face(Direction.WEST).uvs(0, 0, 16, 2).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 0, 16, 2).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 0, 16, 2).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 14).to(16, 16, 16)
				.face(Direction.NORTH).uvs(16, 16, 0, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom").end()
				.face(Direction.EAST).uvs(0, 2, 16, 0).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.EAST).end()
				.face(Direction.SOUTH).uvs(16, 16, 0, 0).texture("#top_bottom").cullface(Direction.SOUTH).end()
				.face(Direction.WEST).uvs(0, 2, 16, 0).rotation(FaceRotation.CLOCKWISE_90).texture("#top_bottom_edges").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(0, 2, 16, 0).rotation(FaceRotation.UPSIDE_DOWN).texture("#top_bottom_edges").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(0, 2, 16, 0).texture("#top_bottom_edges").cullface(Direction.DOWN).end().end()
				.element().from(0, 0, 2).to(2, 2, 14)
				.face(Direction.EAST).uvs(12, 2, 10, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(8, 2, 6, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(10, 2, 8, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(14, 2, 12, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(0, 14, 2).to(2, 16, 14)
				.face(Direction.EAST).uvs(2, 2, 4, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").end()
				.face(Direction.WEST).uvs(6, 2, 8, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").cullface(Direction.WEST).end()
				.face(Direction.UP).uvs(4, 2, 6, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(8, 2, 10, 14).texture("#columns").end().end()
				.element().from(14, 0, 2).to(16, 2, 14)
				.face(Direction.EAST).uvs(6, 2, 8, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(10, 2, 12, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(8, 2, 10, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").end()
				.face(Direction.DOWN).uvs(12, 2, 14, 14).texture("#columns").cullface(Direction.DOWN).end().end()
				.element().from(14, 14, 2).to(16, 16, 14)
				.face(Direction.EAST).uvs(8, 2, 6, 14).rotation(FaceRotation.COUNTERCLOCKWISE_90).texture("#columns").cullface(Direction.EAST).end()
				.face(Direction.WEST).uvs(4, 2, 2, 14).rotation(FaceRotation.CLOCKWISE_90).texture("#columns").end()
				.face(Direction.UP).uvs(6, 2, 4, 14).rotation(FaceRotation.UPSIDE_DOWN).texture("#columns").cullface(Direction.UP).end()
				.face(Direction.DOWN).uvs(10, 2, 8, 14).texture("#columns").end().end()
				.element().from(2, 15, 7).to(14, 16, 9)
				.face(Direction.NORTH).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.SOUTH).uvs(0, 0, 12, 1).texture("#facing_columns").end()
				.face(Direction.UP).uvs(0, 0, 12, 2).texture("#facing_columns").cullface(Direction.UP).end().end()
				.element().from(7, 15, 9).to(9, 16, 14)
				.face(Direction.EAST).uvs(0, 0, 5, 1).texture("#facing_columns").end()
				.face(Direction.WEST).uvs(0, 0, 5, 1).texture("#facing_columns").end()
				.face(Direction.UP).uvs(0, 0, 2, 5).texture("#facing_columns").cullface(Direction.UP).end().end()
				.element().from(7, 15, 2).to(9, 16, 7)
				.face(Direction.EAST).uvs(0, 0, 5, 1).texture("#facing_columns").end()
				.face(Direction.WEST).uvs(0, 0, 5, 1).texture("#facing_columns").end()
				.face(Direction.UP).uvs(0, 0, 2, 5).texture("#facing_columns").cullface(Direction.UP).end().end();
		//@formatter:on
	}

	private void createNormalHopper(LockedHopperBlock b) {
		ModelFile unlocked = models().getExistingFile(new ResourceLocation("block/hopper"));
		ModelFile locked = models().withExistingParent(name(b) + "_locked", new ResourceLocation(prefix("block/template_hopper")));
		ModelFile unlockedSide = models().getExistingFile(new ResourceLocation("block/hopper_side"));
		ModelFile lockedSide = models().withExistingParent(name(b) + "_locked_side", new ResourceLocation(prefix("block/template_hopper_side")));

		LockedModelBuilder hopperModel = this.loaderModels.getBuilder(name(b)).unlockedModel(unlocked.getLocation()).lockedModel(locked.getLocation());
		LockedModelBuilder hopperSideModel = this.loaderModels.getBuilder(name(b) + "_side").unlockedModel(unlockedSide.getLocation()).lockedModel(lockedSide.getLocation());

		getVariantBuilder(StorageBlocks.LOCKED_HOPPER.get()).forAllStatesExcept(state -> {
			Direction dir = state.getValue(LockedHopperBlock.FACING);
			int rotation = 0;
			switch (dir) {
				case EAST:
					rotation = 90;
					break;
				case WEST:
					rotation = 270;
					break;
				case SOUTH:
					rotation = 180;
					break;
				default:
					rotation = 0;
					break;
			}
			return ConfiguredModel.builder().modelFile(dir == Direction.DOWN ? hopperModel : hopperSideModel).rotationY(rotation).build();
		}, LockedHopperBlock.ENABLED);
	}

	private void createMaterialHopper(LockedHopperBlock b) {
		StorageMaterial material = b.getStorageMaterial();

		ModelFile unlocked = models().withExistingParent(name(b) + "_unlocked", new ResourceLocation("block/hopper")).texture("particle", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside"))).texture("top", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_top"))).texture("side", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside"))).texture("inside",
				new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_inside")));
		ModelFile locked = models().withExistingParent(name(b) + "_locked", new ResourceLocation(prefix("block/template_hopper"))).texture("particle", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside"))).texture("top", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_top"))).texture("side", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside")))
				.texture("inside", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_inside"))).texture("topsides", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/locked_hopper_outside")));
		ModelFile unlockedSide = models().withExistingParent(name(b) + "_unlocked_side", new ResourceLocation("block/hopper_side")).texture("particle", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside"))).texture("top", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_top"))).texture("side", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside"))).texture("inside",
				new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_inside")));
		ModelFile lockedSide = models().withExistingParent(name(b) + "_locked_side", new ResourceLocation(prefix("block/template_hopper_side"))).texture("particle", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside"))).texture("top", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_top"))).texture("side", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_outside")))
				.texture("inside", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/hopper_inside"))).texture("topsides", new ResourceLocation(prefix("block/hoppers/" + material.toString() + "/locked_hopper_outside")));

		LockedModelBuilder hopperModel = this.loaderModels.getBuilder(name(b)).unlockedModel(unlocked.getLocation()).lockedModel(locked.getLocation());
		LockedModelBuilder hopperSideModel = this.loaderModels.getBuilder(name(b) + "_side").unlockedModel(unlockedSide.getLocation()).lockedModel(lockedSide.getLocation());

		getVariantBuilder(b).forAllStatesExcept(state -> {
			Direction dir = state.getValue(LockedHopperBlock.FACING);
			int rotation = 0;
			switch (dir) {
				case EAST:
					rotation = 90;
					break;
				case WEST:
					rotation = 270;
					break;
				case SOUTH:
					rotation = 180;
					break;
				default:
					rotation = 0;
					break;
			}
			return ConfiguredModel.builder().modelFile(dir == Direction.DOWN ? hopperModel : hopperSideModel).rotationY(rotation).build();
		}, LockedHopperBlock.ENABLED);
	}

	private void createNormalBarrel(LockedBarrelBlock b) {
		ModelFile unlocked = models().cubeBottomTop(name(b) + "_unlocked", new ResourceLocation("block/barrel_side"), new ResourceLocation("block/barrel_bottom"), new ResourceLocation("block/barrel_top"));
		ModelFile locked = models().cubeBottomTop(name(b) + "_locked", new ResourceLocation("block/barrel_side"), new ResourceLocation("block/barrel_bottom"), new ResourceLocation(prefix("block/barrels/locked_barrel_top")));
		ModelFile open = models().cubeBottomTop(name(b) + "_open", new ResourceLocation("block/barrel_side"), new ResourceLocation("block/barrel_bottom"), new ResourceLocation("block/barrel_top_open"));

		LockedModelBuilder barrelModel = this.loaderModels.getBuilder(name(b)).unlockedModel(unlocked.getLocation()).lockedModel(locked.getLocation());

		getVariantBuilder(StorageBlocks.LOCKED_BARREL.get()).forAllStates(state -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			return ConfiguredModel.builder().modelFile(state.getValue(LockedBarrelBlock.OPEN) ? open : barrelModel).rotationX(dir == Direction.DOWN ? 180 : dir == Direction.UP ? 0 : 90).rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360).build();
		});
		itemModels().getBuilder(prefix("item/" + name(b))).parent(barrelModel);
	}

	private void createMaterialBarrel(LockedBarrelBlock b) {
		StorageMaterial material = b.getStorageMaterial();

		ModelFile unlocked = models().cubeBottomTop(name(b) + "_unlocked", new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_side")), new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_bottom")), new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_top")));
		ModelFile locked = models().cubeBottomTop(name(b) + "_locked", new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_side")), new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_bottom")), new ResourceLocation(prefix("block/barrels/" + material.toString() + "/locked_barrel_top")));
		ModelFile open = models().cubeBottomTop(name(b) + "_open", new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_side")), new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_bottom")), new ResourceLocation(prefix("block/barrels/" + material.toString() + "/barrel_top_open")));

		LockedModelBuilder barrelModel = this.loaderModels.getBuilder(name(b)).unlockedModel(unlocked.getLocation()).lockedModel(locked.getLocation());

		getVariantBuilder(b).forAllStates(state -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			return ConfiguredModel.builder().modelFile(state.getValue(LockedBarrelBlock.OPEN) ? open : barrelModel).rotationX(dir == Direction.DOWN ? 180 : dir == Direction.UP ? 0 : 90).rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360).build();
		});
		itemModels().getBuilder(prefix("item/" + name(b))).parent(barrelModel);
	}

	private ItemModelBuilder genericBlock(Block b) {
		String name = name(b);
		return itemModels().withExistingParent(name, prefix("block/" + name));
	}

	private void particleOnly(Block b, ResourceLocation particle) {
		String name = name(b);
		ModelFile f = models().getBuilder(name).texture("particle", particle);
		simpleBlock(b, f);
	}

	private static String name(Block i) {
		return ForgeRegistries.BLOCKS.getKey(i).getPath();
	}

	private String prefix(String name) {
		return resource(name).toString();
	}

	private ResourceLocation resource(String name) {
		return new ResourceLocation(AssortedStorage.MODID, name);
	}
}
