package com.grim3212.assorted.storage.data;

import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.common.block.*;
import com.grim3212.assorted.storage.common.loot.ModLoadedLootCondition;
import com.grim3212.assorted.storage.common.loot.OptionalLootItem;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StorageBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    public StorageBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> StorageBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));

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

        blocks.add(StorageBlocks.CRATE_COMPACTING.get());
        blocks.add(StorageBlocks.CRATE_CONTROLLER.get());
        blocks.add(StorageBlocks.CRATE_BRIDGE.get());

        for (IRegistryObject<LockedChestBlock> b : StorageBlocks.CHESTS.values()) {
            blocks.add(b.get());
        }

        for (IRegistryObject<LockedBarrelBlock> b : StorageBlocks.BARRELS.values()) {
            blocks.add(b.get());
        }

        for (IRegistryObject<LockedHopperBlock> b : StorageBlocks.HOPPERS.values()) {
            blocks.add(b.get());
        }

        for (StorageBlocks.CrateGroup group : StorageBlocks.CRATES) {
            blocks.add(group.SINGLE.get());
            blocks.add(group.DOUBLE.get());
            blocks.add(group.TRIPLE.get());
            blocks.add(group.QUADRUPLE.get());
        }
    }

    @Override
    public void generate() {
        for (Block b : blocks) {
            this.dropSelf(b);
        }
        this.dropOther(StorageBlocks.LOCKED_CHEST.get(), Blocks.CHEST);
        this.dropOther(StorageBlocks.LOCKED_BARREL.get(), Blocks.BARREL);
        this.dropOther(StorageBlocks.LOCKED_HOPPER.get(), Blocks.HOPPER);

        this.add(StorageBlocks.GOLD_SAFE.get(), createGoldSafeTable(StorageBlocks.GOLD_SAFE.get()));
        this.add(StorageBlocks.LOCKED_ENDER_CHEST.get(), createInventoryCodeTable(StorageBlocks.LOCKED_ENDER_CHEST.get()));

        this.add(StorageBlocks.LOCKED_SHULKER_BOX.get(), createLockedShulkerTable(StorageBlocks.LOCKED_SHULKER_BOX.get()));
        for (IRegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
            this.add(b.get(), createLockedShulkerTable(b.get()));
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

        this.add(StorageBlocks.LOCKED_STEEL_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_STEEL_DOOR.get(), Identifier.parse("assorteddecor:steel_door")));
        this.add(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get(), Identifier.parse("assorteddecor:chain_link_door")));
        this.add(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_QUARTZ_DOOR.get(), Identifier.parse("assorteddecor:quartz_door")));
        this.add(StorageBlocks.LOCKED_GLASS_DOOR.get(), createDecorTable(StorageBlocks.LOCKED_GLASS_DOOR.get(), Identifier.parse("assorteddecor:glass_door")));
    }

    private LootTable.Builder createLockedDoorTable(Block b, Block out) {
        return LootTable.lootTable().withPool(applyExplosionCondition(b, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(out).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER))))));
    }

    private LootTable.Builder createDecorTable(Block b, Identifier decorBlockLoc) {
        return LootTable.lootTable().withPool(applyExplosionCondition(b, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(ModLoadedLootCondition.isModLoaded("assorteddecor")).add(OptionalLootItem.optionalLootTableItem(decorBlockLoc).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER))))));
    }

    /**
     * Carrying the block entity's state into the dropped item is one function now.
     * <p>
     * {@code CopyNbtFunction} and {@code SetContainerContents} pointed at a block entity type are
     * gone - {@code ContextNbtProvider.BLOCK_ENTITY} has no public factory any more - and the
     * contents, name and lock are all data components the block entity exposes. This is what
     * vanilla's own shulker box drop does.
     */
    private LootTable.Builder createContentsTable(Block b) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME).include(DataComponents.CONTAINER).include(DataComponents.CUSTOM_DATA));
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
        return LootTable.lootTable().withPool(pool);
    }

    /**
     * The safe drops its padlock separately (the block entity does that on removal), so the item
     * itself only carries the name and the contents, not the lock.
     */
    private LootTable.Builder createGoldSafeTable(Block b) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME).include(DataComponents.CONTAINER));
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
        return LootTable.lootTable().withPool(pool);
    }

    private LootTable.Builder createLockedShulkerTable(Block b) {
        return createContentsTable(b);
    }

    private LootTable.Builder createInventoryCodeTable(Block b) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY).include(DataComponents.CUSTOM_DATA));
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion());
        return LootTable.lootTable().withPool(pool);
    }
}
