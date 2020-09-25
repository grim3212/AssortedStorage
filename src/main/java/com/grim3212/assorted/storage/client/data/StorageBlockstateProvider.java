package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
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
		
		ModelFile model = models().cube(prefix("locksmith_workbench"), 
				new ResourceLocation("block/oak_planks"), 
				new ResourceLocation(prefix("block/locksmith_top")), 
				new ResourceLocation(prefix("block/locksmith_front")), 
				new ResourceLocation(prefix("block/locksmith_side")), 
				new ResourceLocation(prefix("block/locksmith_side")), 
				new ResourceLocation(prefix("block/locksmith_front")))
				.texture("particle", prefix("block/locksmith_front"));
		simpleBlock(StorageBlocks.LOCKSMITH_WORKBENCH.get(), model);
		
		genericBlock(StorageBlocks.LOCKSMITH_WORKBENCH.get());
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
		return new ResourceLocation(AssortedStorage.MODID, name).toString();
	}
}
