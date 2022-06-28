package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageBlockstateProvider extends BlockStateProvider {

	private final Map<Block, ResourceLocation> blocks;

	public StorageBlockstateProvider(DataGenerator generator, ExistingFileHelper exFileHelper) {
		super(generator, AssortedStorage.MODID, exFileHelper);

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

		temporaryDoorBlock(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), resource("block/locked_quartz_door_bottom"), resource("block/locked_quartz_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_GLASS_DOOR.get(), resource("block/locked_glass_door_bottom"), resource("block/locked_glass_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_STEEL_DOOR.get(), resource("block/locked_steel_door_bottom"), resource("block/locked_steel_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), resource("block/locked_chain_link_door_bottom"), resource("block/locked_chain_link_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_OAK_DOOR.get(), resource("block/locked_oak_door_bottom"), resource("block/locked_oak_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), resource("block/locked_spruce_door_bottom"), resource("block/locked_spruce_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_BIRCH_DOOR.get(), resource("block/locked_birch_door_bottom"), resource("block/locked_birch_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_ACACIA_DOOR.get(), resource("block/locked_acacia_door_bottom"), resource("block/locked_acacia_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), resource("block/locked_jungle_door_bottom"), resource("block/locked_jungle_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), resource("block/locked_dark_oak_door_bottom"), resource("block/locked_dark_oak_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), resource("block/locked_crimson_door_bottom"), resource("block/locked_crimson_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_WARPED_DOOR.get(), resource("block/locked_warped_door_bottom"), resource("block/locked_warped_door_top"));
		temporaryDoorBlock(StorageBlocks.LOCKED_IRON_DOOR.get(), resource("block/locked_iron_door_bottom"), resource("block/locked_iron_door_top"));
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

	
	// TODO: Remove once https://github.com/MinecraftForge/MinecraftForge/pull/8687 is merged
	private void temporaryDoorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
		String baseName = ForgeRegistries.BLOCKS.getKey(block).toString();

		ModelFile bottomLeft = doorBottomLeft(baseName + "_bottom_left", bottom, top);
		ModelFile bottomLeftOpen = doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top);
		ModelFile bottomRight = doorBottomRight(baseName + "_bottom_right", bottom, top);
		ModelFile bottomRightOpen = doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top);
		ModelFile topLeft = doorTopLeft(baseName + "_top_left", bottom, top);
		ModelFile topLeftOpen = doorTopLeftOpen(baseName + "_top_left_open", bottom, top);
		ModelFile topRight = doorTopRight(baseName + "_top_right", bottom, top);
		ModelFile topRightOpen = doorTopRightOpen(baseName + "_top_right_open", bottom, top);
		doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
	}

	public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight, ModelFile bottomRightOpen, ModelFile topLeft, ModelFile topLeftOpen, ModelFile topRight, ModelFile topRightOpen) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
			boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
			boolean open = state.getValue(DoorBlock.OPEN);
			if (open) {
				yRot += 90;
			}
			if (right && open) {
				yRot += 180;
			}
			yRot %= 360;
			return ConfiguredModel.builder().modelFile(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? (right ? (open ? bottomRightOpen : bottomRight) : (open ? bottomLeftOpen : bottomLeft)) : (right ? (open ? topRightOpen : topRight) : (open ? topLeftOpen : topLeft))).rotationY(yRot).build();
		}, DoorBlock.POWERED);
	}

	private ModelFile tempDoorModel(String name, String model, ResourceLocation bottom, ResourceLocation top) {
		return models().withExistingParent(name, ModelProvider.BLOCK_FOLDER + "/" + model).texture("bottom", bottom).texture("top", top);
	}

	public ModelFile doorBottomLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_bottom_left", bottom, top);
	}

	public ModelFile doorBottomLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_bottom_left_open", bottom, top);
	}

	public ModelFile doorBottomRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_bottom_right", bottom, top);
	}

	public ModelFile doorBottomRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_bottom_right_open", bottom, top);
	}

	public ModelFile doorTopLeft(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_top_left", bottom, top);
	}

	public ModelFile doorTopLeftOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_top_left_open", bottom, top);
	}

	public ModelFile doorTopRight(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_top_right", bottom, top);
	}

	public ModelFile doorTopRightOpen(String name, ResourceLocation bottom, ResourceLocation top) {
		return tempDoorModel(name, "door_top_right_open", bottom, top);
	}
}
