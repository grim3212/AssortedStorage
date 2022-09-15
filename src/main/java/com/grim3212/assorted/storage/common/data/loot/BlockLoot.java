package com.grim3212.assorted.storage.common.data.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.grim3212.assorted.storage.common.block.GoldSafeBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.loot.ModLoadedLootCondition;
import com.grim3212.assorted.storage.common.loot.OptionalLootItem;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

	private final List<Block> blocks = new ArrayList<>();

	public BlockLoot() {
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
	public void accept(BiConsumer<ResourceLocation, Builder> t) {
		for (Block b : blocks) {
			t.accept(prefix(ForgeRegistries.BLOCKS.getKey(b)), genRegular(b));
		}

		t.accept(prefix(StorageBlocks.GOLD_SAFE.getId()), genGoldSafe(StorageBlocks.GOLD_SAFE.get()));

		t.accept(prefix(StorageBlocks.LOCKED_CHEST.getId()), genRegular(Blocks.CHEST));
		t.accept(prefix(StorageBlocks.LOCKED_BARREL.getId()), genRegular(Blocks.BARREL));
		t.accept(prefix(StorageBlocks.LOCKED_HOPPER.getId()), genRegular(Blocks.HOPPER));
		t.accept(prefix(StorageBlocks.LOCKED_ENDER_CHEST.getId()), genInventoryCode(StorageBlocks.LOCKED_ENDER_CHEST.get()));

		t.accept(prefix(StorageBlocks.LOCKED_SHULKER_BOX.getId()), genShulker(StorageBlocks.LOCKED_SHULKER_BOX.get()));
		for (RegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
			t.accept(prefix(b.getId()), genShulker(b.get()));
		}

		t.accept(prefix(StorageBlocks.LOCKED_IRON_DOOR.getId()), genDoor(StorageBlocks.LOCKED_IRON_DOOR.get(), Blocks.IRON_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_OAK_DOOR.getId()), genDoor(StorageBlocks.LOCKED_OAK_DOOR.get(), Blocks.OAK_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_SPRUCE_DOOR.getId()), genDoor(StorageBlocks.LOCKED_SPRUCE_DOOR.get(), Blocks.SPRUCE_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_BIRCH_DOOR.getId()), genDoor(StorageBlocks.LOCKED_BIRCH_DOOR.get(), Blocks.BIRCH_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_ACACIA_DOOR.getId()), genDoor(StorageBlocks.LOCKED_ACACIA_DOOR.get(), Blocks.ACACIA_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_JUNGLE_DOOR.getId()), genDoor(StorageBlocks.LOCKED_JUNGLE_DOOR.get(), Blocks.JUNGLE_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_DARK_OAK_DOOR.getId()), genDoor(StorageBlocks.LOCKED_DARK_OAK_DOOR.get(), Blocks.DARK_OAK_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_CRIMSON_DOOR.getId()), genDoor(StorageBlocks.LOCKED_CRIMSON_DOOR.get(), Blocks.CRIMSON_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_MANGROVE_DOOR.getId()), genDoor(StorageBlocks.LOCKED_MANGROVE_DOOR.get(), Blocks.MANGROVE_DOOR));
		t.accept(prefix(StorageBlocks.LOCKED_WARPED_DOOR.getId()), genDoor(StorageBlocks.LOCKED_WARPED_DOOR.get(), Blocks.WARPED_DOOR));

		t.accept(prefix(StorageBlocks.LOCKED_STEEL_DOOR.getId()), genDecorDoor(StorageBlocks.LOCKED_STEEL_DOOR.get(), new ResourceLocation("assorteddecor:steel_door")));
		t.accept(prefix(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.getId()), genDecorDoor(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), new ResourceLocation("assorteddecor:chain_link_door")));
		t.accept(prefix(StorageBlocks.LOCKED_QUARTZ_DOOR.getId()), genDecorDoor(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), new ResourceLocation("assorteddecor:quartz_door")));
		t.accept(prefix(StorageBlocks.LOCKED_GLASS_DOOR.getId()), genDecorDoor(StorageBlocks.LOCKED_GLASS_DOOR.get(), new ResourceLocation("assorteddecor:glass_door")));
	}

	private ResourceLocation prefix(ResourceLocation location) {
		return new ResourceLocation(location.getNamespace(), "blocks/" + location.getPath());
	}

	private LootTable.Builder genDoor(Block b, Block out) {
		LootItemBlockStatePropertyCondition.Builder halfCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(out).when(halfCondition);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private LootTable.Builder genDecorDoor(Block b, ResourceLocation decorBlockLoc) {
		LootItemBlockStatePropertyCondition.Builder halfCondition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER));
		LootPoolEntryContainer.Builder<?> entry = OptionalLootItem.optionalLootTableItem(decorBlockLoc).when(halfCondition);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).when(ModLoadedLootCondition.isModLoaded("assorteddecor")).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private LootTable.Builder genRegular(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private LootTable.Builder genGoldSafe(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents(StorageBlockEntityTypes.GOLD_SAFE.get()).withEntry(DynamicLoot.dynamicEntry(GoldSafeBlock.CONTENTS)));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private LootTable.Builder genShulker(Block b) {
		CopyNbtFunction.Builder colorFunc = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Color", "Color");
		CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Storage_Lock", "Storage_Lock");
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(func).apply(colorFunc).apply(CopyNameFunction.copyName(NameSource.BLOCK_ENTITY)).apply(SetContainerContents.setContents(StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get()).withEntry(DynamicLoot.dynamicEntry(LockedShulkerBoxBlock.CONTENTS)));
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
		return LootTable.lootTable().withPool(pool);
	}

	private LootTable.Builder genInventoryCode(Block b) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
		CopyNbtFunction.Builder func = CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Storage_Lock", "Storage_Lock");
		LootPool.Builder pool = LootPool.lootPool().name("main").setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
		return LootTable.lootTable().withPool(pool);
	}
}
