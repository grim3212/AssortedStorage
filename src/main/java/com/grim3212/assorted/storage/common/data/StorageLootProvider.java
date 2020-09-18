package com.grim3212.assorted.storage.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.ResourceLocation;

public class StorageLootProvider implements IDataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public StorageLootProvider(DataGenerator generator) {
		this.generator = generator;

		blocks.add(StorageBlocks.WOOD_CABINET.get());
		blocks.add(StorageBlocks.GLASS_CABINET.get());
		blocks.add(StorageBlocks.GOLD_SAFE.get());
		blocks.add(StorageBlocks.OBSIDIAN_SAFE.get());
		blocks.add(StorageBlocks.LOCKER.get());
		blocks.add(StorageBlocks.ITEM_TOWER.get());
		blocks.add(StorageBlocks.LOCKSMITH_WORKBENCH.get());
		blocks.add(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
		blocks.add(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(b.getRegistryName(), genRegular(b));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
		}
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Storage loot tables";
	}
}
