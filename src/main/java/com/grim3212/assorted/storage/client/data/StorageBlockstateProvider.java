package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageBlockstateProvider extends BlockStateProvider {

	private final LockedModelProvider loaderModels;

	private final Map<Block, ResourceLocation> blocks;

	private static final ResourceLocation CUTOUT_RENDER_TYPE = new ResourceLocation("minecraft:cutout");

	public StorageBlockstateProvider(DataGenerator generator, ExistingFileHelper exFileHelper, LockedModelProvider loader) {
		super(generator, AssortedStorage.MODID, exFileHelper);
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
