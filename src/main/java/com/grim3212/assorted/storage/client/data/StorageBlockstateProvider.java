package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedDoorBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class StorageBlockstateProvider extends BlockStateProvider {

	private final Map<Block, ResourceLocation> blocks;

	public StorageBlockstateProvider(DataGenerator generator, ExistingFileHelper exFileHelper) {
		super(generator, AssortedStorage.MODID, exFileHelper);

		this.blocks = new HashMap<>();

		blocks.put(StorageBlocks.WOOD_CABINET.get(), new ResourceLocation(AssortedStorage.MODID, "block/cabinet_break"));
		blocks.put(StorageBlocks.GLASS_CABINET.get(), new ResourceLocation(AssortedStorage.MODID, "block/cabinet_break"));
		blocks.put(StorageBlocks.GOLD_SAFE.get(), new ResourceLocation("block/gold_block"));
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

		lockedDoorBlockInternal(StorageBlocks.QUARTZ_LOCKED_DOOR.get(), resource("block/quartz_door_bottom"), resource("block/quartz_door_top"), resource("block/quartz_door_bottom_locked"), resource("block/quartz_door_top_locked"));
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
		return Registry.BLOCK.getKey(i).getPath();
	}

	private String prefix(String name) {
		return resource(name).toString();
	}

	private ResourceLocation resource(String name) {
		return new ResourceLocation(AssortedStorage.MODID, name);
	}

	private void lockedDoorBlockInternal(LockedDoorBlock block, ResourceLocation bottom, ResourceLocation top, ResourceLocation bottomLocked, ResourceLocation topLocked) {
		String baseName = name(block);
		ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom", bottom, top);
		ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_hinge", bottom, top);
		ModelFile topLeft = models().doorTopLeft(baseName + "_top", bottom, top);
		ModelFile topRight = models().doorTopRight(baseName + "_top_hinge", bottom, top);

		ModelFile bottomLeftLocked = models().doorBottomLeft(baseName + "_bottom_locked", bottomLocked, topLocked);
		ModelFile bottomRightLocked = models().doorBottomRight(baseName + "_bottom_hinge_locked", bottomLocked, topLocked);
		ModelFile topLeftLocked = models().doorTopLeft(baseName + "_top_locked", bottomLocked, topLocked);
		ModelFile topRightLocked = models().doorTopRight(baseName + "_top_hinge_locked", bottomLocked, topLocked);
		lockedDoorBlock(block, bottomLeft, bottomRight, topLeft, topRight, bottomLeftLocked, bottomRightLocked, topLeftLocked, topRightLocked);
	}

	public void lockedDoorBlock(LockedDoorBlock block, ModelFile bottomLeft, ModelFile bottomRight, ModelFile topLeft, ModelFile topRight, ModelFile bottomLeftLocked, ModelFile bottomRightLocked, ModelFile topLeftLocked, ModelFile topRightLocked) {
		getVariantBuilder(block).forAllStatesExcept(state -> {
			int yRot = ((int) state.get(DoorBlock.FACING).getHorizontalAngle()) + 90;
			boolean rh = state.get(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
			boolean open = state.get(DoorBlock.OPEN);
			boolean locked = state.get(LockedDoorBlock.LOCKED);
			boolean right = rh ^ open;
			if (open) {
				yRot += 90;
			}
			if (rh && open) {
				yRot += 180;
			}
			yRot %= 360;
			return ConfiguredModel.builder().modelFile(state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? (right ? locked ? bottomRightLocked : bottomRight : locked ? bottomLeftLocked : bottomLeft) : (right ? locked ? topRightLocked : topRight : locked ? topLeftLocked : topLeft)).rotationY(yRot).build();
		}, DoorBlock.POWERED);
	}
}
