package com.grim3212.assorted.storage.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.DynamicLootEntry;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyName.Source;
import net.minecraft.loot.functions.SetContents;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ResourceLocation;

public class StorageLootProvider implements IDataProvider {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator generator;
	private final List<Block> blocks = new ArrayList<>();

	public StorageLootProvider(DataGenerator generator) {
		this.generator = generator;

		blocks.add(StorageBlocks.WOOD_CABINET.get());
		blocks.add(StorageBlocks.GLASS_CABINET.get());
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

		tables.put(StorageBlocks.GOLD_SAFE.getId(), genInventoryStorage(StorageBlocks.GOLD_SAFE.get()));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
		}

		Path doorPath = getPath(generator.getOutputFolder(), StorageBlocks.QUARTZ_LOCKED_DOOR.get().getRegistryName());
		IDataProvider.save(GSON, cache, LootTableManager.toJson(genDoor(StorageBlocks.QUARTZ_LOCKED_DOOR.get()).setParameterSet(LootParameterSets.BLOCK).build()), doorPath);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genDoor(Block b) {
		BlockStateProperty.Builder halfCondition = BlockStateProperty.builder(b).fromProperties(StatePropertiesPredicate.Builder.newBuilder().withProp(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b).acceptCondition(halfCondition);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	private static LootTable.Builder genRegular(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	private static LootTable.Builder genInventoryStorage(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b).acceptFunction(CopyName.builder(Source.BLOCK_ENTITY)).acceptFunction(SetContents.builderIn().addLootEntry(DynamicLootEntry.func_216162_a(GoldSafeBlock.CONTENTS)));
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry).acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Storage loot tables";
	}
}
