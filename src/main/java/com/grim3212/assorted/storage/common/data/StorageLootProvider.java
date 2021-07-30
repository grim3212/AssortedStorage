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

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class StorageLootProvider implements DataProvider {

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
	public void run(HashCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(b.getRegistryName(), genRegular(b));
		}

		tables.put(StorageBlocks.GOLD_SAFE.getId(), genInventoryStorage(StorageBlocks.GOLD_SAFE.get()));

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.save(GSON, cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.BLOCK).build()), path);
		}

		door(StorageBlocks.LOCKED_IRON_DOOR.get(), Blocks.IRON_DOOR, cache);
		door(StorageBlocks.LOCKED_OAK_DOOR.get(), Blocks.OAK_DOOR, cache);
		door(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), Blocks.SPRUCE_DOOR, cache);
		door(StorageBlocks.LOCKED_BIRCH_DOOR.get(), Blocks.BIRCH_DOOR, cache);
		door(StorageBlocks.LOCKED_ACACIA_DOOR.get(), Blocks.ACACIA_DOOR, cache);
		door(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), Blocks.JUNGLE_DOOR, cache);
		door(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), Blocks.DARK_OAK_DOOR, cache);
		door(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), Blocks.CRIMSON_DOOR, cache);
		door(StorageBlocks.LOCKED_WARPED_DOOR.get(), Blocks.WARPED_DOOR, cache);
	}

	private void door(Block b, Block out, HashCache cache) throws IOException {
		Path doorPath = getPath(generator.getOutputFolder(), b.getRegistryName());
		DataProvider.save(GSON, cache, LootTables.serialize(genDoor(b, out).setParamSet(LootContextParamSets.BLOCK).build()), doorPath);
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder genDoor(Block b, Block out) {
		LootItemBlockStatePropertyCondition.Builder halfCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(out).when(halfCondition);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genRegular(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genInventoryStorage(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents().withEntry(DynamicLoot.dynamicEntry(GoldSafeBlock.CONTENTS)));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Storage loot tables";
	}
}
