package com.grim3212.assorted.storage.common.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
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
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageLootProvider implements DataProvider {

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
		blocks.add(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get());

		for (RegistryObject<LockedChestBlock> b : StorageBlocks.CHESTS.values()) {
			blocks.add(b.get());
		}
	}

	@Override
	public void run(CachedOutput cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : blocks) {
			tables.put(ForgeRegistries.BLOCKS.getKey(b), genRegular(b));
		}

		tables.put(StorageBlocks.GOLD_SAFE.getId(), genGoldSafe(StorageBlocks.GOLD_SAFE.get()));

		tables.put(StorageBlocks.LOCKED_CHEST.getId(), genRegular(Blocks.CHEST));
		tables.put(StorageBlocks.LOCKED_ENDER_CHEST.getId(), genInventoryCode(StorageBlocks.LOCKED_ENDER_CHEST.get()));

		tables.put(StorageBlocks.LOCKED_SHULKER_BOX.getId(), genShulker(StorageBlocks.LOCKED_SHULKER_BOX.get()));
		for (RegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
			tables.put(b.getId(), genShulker(b.get()));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(generator.getOutputFolder(), e.getKey());
			DataProvider.saveStable(cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.BLOCK).build()), path);
		}

		door(StorageBlocks.LOCKED_IRON_DOOR.get(), Blocks.IRON_DOOR, cache);
		door(StorageBlocks.LOCKED_OAK_DOOR.get(), Blocks.OAK_DOOR, cache);
		door(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), Blocks.SPRUCE_DOOR, cache);
		door(StorageBlocks.LOCKED_BIRCH_DOOR.get(), Blocks.BIRCH_DOOR, cache);
		door(StorageBlocks.LOCKED_ACACIA_DOOR.get(), Blocks.ACACIA_DOOR, cache);
		door(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), Blocks.JUNGLE_DOOR, cache);
		door(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), Blocks.DARK_OAK_DOOR, cache);
		door(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), Blocks.CRIMSON_DOOR, cache);
		door(StorageBlocks.LOCKED_MANGROVE_DOOR.get(), Blocks.MANGROVE_DOOR, cache);
		door(StorageBlocks.LOCKED_WARPED_DOOR.get(), Blocks.WARPED_DOOR, cache);
	}

	private void door(Block b, Block out, CachedOutput cache) throws IOException {
		Path doorPath = getPath(generator.getOutputFolder(), ForgeRegistries.BLOCKS.getKey(b));
		DataProvider.saveStable(cache, LootTables.serialize(genDoor(b, out).setParamSet(LootContextParamSets.BLOCK).build()), doorPath);
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

	private static LootTable.Builder genGoldSafe(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents(StorageBlockEntityTypes.GOLD_SAFE.get()).withEntry(DynamicLoot.dynamicEntry(GoldSafeBlock.CONTENTS)));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genShulker(Block b) {
		CopyNbtFunction.Builder colorFunc = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Color", "Color");
		CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Storage_Lock", "Storage_Lock");
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(func).apply(colorFunc).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get()).withEntry(DynamicLoot.dynamicEntry(LockedShulkerBoxBlock.CONTENTS)));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private static LootTable.Builder genInventoryCode(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Storage_Lock", "Storage_Lock");
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
		return LootTable.lootTable().withPool(pool);
	}

	@Override
	public String getName() {
		return "Assorted Storage loot tables";
	}
}
