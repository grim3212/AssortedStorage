package com.grim3212.assorted.storage.common.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.loot.ModLoadedLootCondition;
import com.grim3212.assorted.storage.common.loot.OptionalLootItem;
import com.mojang.datafixers.util.Pair;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

public class StorageLootProvider extends LootTableProvider {

	public StorageLootProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
		return ImmutableList.of(Pair.of(BlockTables::new, LootContextParamSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
		map.forEach((location, lootTable) -> {
			LootTables.validate(validationtracker, location, lootTable);
		});
	}

	@Override
	public String getName() {
		return "Assorted Storage loot tables";
	}

	private class BlockTables extends BlockLoot {

		private final List<Block> blocks = new ArrayList<>();

		public BlockTables() {
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

			for (RegistryObject<LockedBarrelBlock> b : StorageBlocks.BARRELS.values()) {
				blocks.add(b.get());
			}

			for (RegistryObject<LockedHopperBlock> b : StorageBlocks.HOPPERS.values()) {
				blocks.add(b.get());
			}
		}

		@Override
		protected void addTables() {
			for (Block b : blocks) {
				this.dropSelf(b);
			}
			this.dropOther(StorageBlocks.LOCKED_CHEST.get(), Blocks.CHEST);
			this.dropOther(StorageBlocks.LOCKED_BARREL.get(), Blocks.BARREL);
			this.dropOther(StorageBlocks.LOCKED_HOPPER.get(), Blocks.HOPPER);

			this.add(StorageBlocks.GOLD_SAFE.get(), BlockTables::createGoldSafeTable);
			this.add(StorageBlocks.LOCKED_ENDER_CHEST.get(), BlockTables::createInventoryCodeTable);

			this.add(StorageBlocks.LOCKED_SHULKER_BOX.get(), BlockTables::createLockedShulkerTable);
			for (RegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
				this.add(b.get(), BlockTables::createLockedShulkerTable);
			}

			this.add(StorageBlocks.LOCKED_IRON_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_IRON_DOOR.get(), Blocks.IRON_DOOR));
			this.add(StorageBlocks.LOCKED_OAK_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_OAK_DOOR.get(), Blocks.OAK_DOOR));
			this.add(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), Blocks.SPRUCE_DOOR));
			this.add(StorageBlocks.LOCKED_BIRCH_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_BIRCH_DOOR.get(), Blocks.BIRCH_DOOR));
			this.add(StorageBlocks.LOCKED_ACACIA_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_ACACIA_DOOR.get(), Blocks.ACACIA_DOOR));
			this.add(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), Blocks.JUNGLE_DOOR));
			this.add(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), Blocks.DARK_OAK_DOOR));
			this.add(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), Blocks.CRIMSON_DOOR));
			this.add(StorageBlocks.LOCKED_MANGROVE_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_MANGROVE_DOOR.get(), Blocks.MANGROVE_DOOR));
			this.add(StorageBlocks.LOCKED_WARPED_DOOR.get(), createLockedDoorTable(StorageBlocks.LOCKED_WARPED_DOOR.get(), Blocks.WARPED_DOOR));

			this.add(StorageBlocks.LOCKED_STEEL_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_STEEL_DOOR.get(), new ResourceLocation("assorteddecor:steel_door")));
			this.add(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), new ResourceLocation("assorteddecor:chain_link_door")));
			this.add(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), new ResourceLocation("assorteddecor:quartz_door")));
			this.add(StorageBlocks.LOCKED_GLASS_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_GLASS_DOOR.get(), new ResourceLocation("assorteddecor:glass_door")));
		}

		private static LootTable.Builder createLockedDoorTable(Block b, Block out) {
			return LootTable.lootTable().withPool(applyExplosionCondition(b, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(out).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER))))));
		}

		private static LootTable.Builder createDecorTable(Block b, ResourceLocation decorBlockLoc) {
			return LootTable.lootTable().withPool(applyExplosionCondition(b, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(ModLoadedLootCondition.isModLoaded("assorteddecor")).add(OptionalLootItem.optionalLootTableItem(decorBlockLoc).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER))))));
		}

		private static LootTable.Builder createGoldSafeTable(Block b) {
			LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents(StorageBlockEntityTypes.GOLD_SAFE.get()).withEntry(DynamicLoot.dynamicEntry(GoldSafeBlock.CONTENTS)));
			LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
			return LootTable.lootTable().withPool(pool);
		}

		private static LootTable.Builder createLockedShulkerTable(Block b) {
			CopyNbtFunction.Builder colorFunc = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Color", "Color");
			CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Storage_Lock", "Storage_Lock");
			LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(func).apply(colorFunc).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get()).withEntry(DynamicLoot.dynamicEntry(LockedShulkerBoxBlock.CONTENTS)));
			LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
			return LootTable.lootTable().withPool(pool);
		}

		private static LootTable.Builder createInventoryCodeTable(Block b) {
			LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
			CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Storage_Lock", "Storage_Lock");
			LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
			return LootTable.lootTable().withPool(pool);
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return StorageBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList());
		}
	}
}
