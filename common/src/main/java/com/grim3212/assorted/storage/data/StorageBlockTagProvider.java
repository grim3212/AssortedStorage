package com.grim3212.assorted.storage.data;

import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.StorageTags;
import com.grim3212.assorted.storage.common.block.*;
import com.grim3212.assorted.storage.common.block.StorageBlocks.CrateGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class StorageBlockTagProvider extends LibBlockTagProvider {

    public StorageBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, IntrinsicTagAppender<Block>> tagger) {
        IntrinsicTagAppender<Block> piglinBuilder = tagger.apply(BlockTags.GUARDED_BY_PIGLINS);
        piglinBuilder.add(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get());
        piglinBuilder.add(StorageBlocks.GLASS_CABINET.get());
        piglinBuilder.add(StorageBlocks.WOOD_CABINET.get());
        piglinBuilder.add(StorageBlocks.GOLD_SAFE.get());
        piglinBuilder.add(StorageBlocks.OBSIDIAN_SAFE.get());
        piglinBuilder.add(StorageBlocks.LOCKER.get());
        piglinBuilder.add(StorageBlocks.ITEM_TOWER.get());
        piglinBuilder.add(StorageBlocks.LOCKED_ENDER_CHEST.get());
        piglinBuilder.add(StorageBlocks.LOCKED_CHEST.get());
        piglinBuilder.add(StorageBlocks.LOCKED_SHULKER_BOX.get());

        for (Entry<StorageMaterial, IRegistryObject<LockedChestBlock>> chest : StorageBlocks.CHESTS.entrySet()) {
            Block block = chest.getValue().get();
            piglinBuilder.add(block);
            tagger.apply(LibCommonTags.Blocks.CHESTS).add(block);
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

            switch (chest.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_1).add(block);
                    break;
                case 2:
                    tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_2).add(block);
                    break;
                case 3:
                    tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_3).add(block);
                    break;
                case 4:
                    tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_4).add(block);
                    break;
                case 5:
                    tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_5).add(block);
                    break;
                default:
                    tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_0).add(block);
                    break;
            }
        }

        for (Entry<StorageMaterial, IRegistryObject<LockedBarrelBlock>> barrel : StorageBlocks.BARRELS.entrySet()) {
            Block block = barrel.getValue().get();
            piglinBuilder.add(block);
            tagger.apply(LibCommonTags.Blocks.BARRELS).add(block);
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

            switch (barrel.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_1).add(block);
                    break;
                case 2:
                    tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_2).add(block);
                    break;
                case 3:
                    tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_3).add(block);
                    break;
                case 4:
                    tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_4).add(block);
                    break;
                case 5:
                    tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_5).add(block);
                    break;
                default:
                    tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_0).add(block);
                    break;
            }
        }

        for (Entry<StorageMaterial, IRegistryObject<LockedHopperBlock>> barrel : StorageBlocks.HOPPERS.entrySet()) {
            Block block = barrel.getValue().get();
            piglinBuilder.add(block);
            tagger.apply(StorageTags.Blocks.HOPPERS).add(block);
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

            switch (barrel.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_1).add(block);
                    break;
                case 2:
                    tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_2).add(block);
                    break;
                case 3:
                    tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_3).add(block);
                    break;
                case 4:
                    tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_4).add(block);
                    break;
                case 5:
                    tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_5).add(block);
                    break;
                default:
                    tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_0).add(block);
                    break;
            }
        }

        for (Entry<StorageMaterial, IRegistryObject<LockedShulkerBoxBlock>> chest : StorageBlocks.SHULKERS.entrySet()) {
            Block block = chest.getValue().get();
            piglinBuilder.add(block);
            tagger.apply(BlockTags.SHULKER_BOXES).add(block);
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block);

            switch (chest.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_1).add(block);
                    break;
                case 2:
                    tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_2).add(block);
                    break;
                case 3:
                    tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_3).add(block);
                    break;
                case 4:
                    tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_4).add(block);
                    break;
                case 5:
                    tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_5).add(block);
                    break;
                default:
                    tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_0).add(block);
                    break;
            }
        }

        tagger.apply(StorageTags.Blocks.CRATES).add(StorageBlocks.CRATE_COMPACTING.get());
        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(StorageBlocks.CRATE_COMPACTING.get(), StorageBlocks.CRATE_CONTROLLER.get(), StorageBlocks.CRATE_BRIDGE.get());
        for (CrateGroup crateGroup : StorageBlocks.CRATES) {
            piglinBuilder.add(crateGroup.SINGLE.get());
            tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(crateGroup.SINGLE.get());
            tagger.apply(StorageTags.Blocks.CRATES).add(crateGroup.SINGLE.get());
            tagger.apply(StorageTags.Blocks.CRATES_SINGLE).add(crateGroup.SINGLE.get());

            piglinBuilder.add(crateGroup.DOUBLE.get());
            tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(crateGroup.DOUBLE.get());
            tagger.apply(StorageTags.Blocks.CRATES).add(crateGroup.DOUBLE.get());
            tagger.apply(StorageTags.Blocks.CRATES_DOUBLE).add(crateGroup.DOUBLE.get());

            piglinBuilder.add(crateGroup.TRIPLE.get());
            tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(crateGroup.TRIPLE.get());
            tagger.apply(StorageTags.Blocks.CRATES).add(crateGroup.TRIPLE.get());
            tagger.apply(StorageTags.Blocks.CRATES_TRIPLE).add(crateGroup.TRIPLE.get());

            piglinBuilder.add(crateGroup.QUADRUPLE.get());
            tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(crateGroup.QUADRUPLE.get());
            tagger.apply(StorageTags.Blocks.CRATES).add(crateGroup.QUADRUPLE.get());
            tagger.apply(StorageTags.Blocks.CRATES_QUADRUPLE).add(crateGroup.QUADRUPLE.get());
        }

        tagger.apply(LibCommonTags.Blocks.CHESTS_WOODEN).add(StorageBlocks.LOCKED_CHEST.get());
        tagger.apply(StorageTags.Blocks.CHESTS_LEVEL_0).addTag(LibCommonTags.Blocks.CHESTS_WOODEN);

        tagger.apply(LibCommonTags.Blocks.BARRELS_WOODEN).add(StorageBlocks.LOCKED_BARREL.get());
        tagger.apply(StorageTags.Blocks.BARRELS_LEVEL_0).addTag(LibCommonTags.Blocks.BARRELS_WOODEN);

        tagger.apply(StorageTags.Blocks.HOPPERS).add(Blocks.HOPPER, StorageBlocks.LOCKED_HOPPER.get());
        tagger.apply(StorageTags.Blocks.HOPPERS_LEVEL_0).add(Blocks.HOPPER, StorageBlocks.LOCKED_HOPPER.get());

        tagger.apply(BlockTags.SHULKER_BOXES).add(StorageBlocks.LOCKED_SHULKER_BOX.get());
        tagger.apply(StorageTags.Blocks.SHULKERS_LEVEL_0).add(Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX,
                Blocks.YELLOW_SHULKER_BOX);
        tagger.apply(StorageTags.Blocks.SHULKERS_NORMAL).add(Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX,
                Blocks.YELLOW_SHULKER_BOX);

        IntrinsicTagAppender<Block> doorBuilder = tagger.apply(BlockTags.DOORS);
        for (Block b : StorageBlocks.lockedDoors()) {
            doorBuilder.add(b);
        }

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(StorageBlocks.ITEM_TOWER.get(), StorageBlocks.LOCKER.get(), StorageBlocks.GOLD_SAFE.get(), StorageBlocks.OBSIDIAN_SAFE.get(), StorageBlocks.LOCKED_IRON_DOOR.get(), StorageBlocks.LOCKED_QUARTZ_DOOR.get(), StorageBlocks.LOCKED_STEEL_DOOR.get(), StorageBlocks.LOCKED_ENDER_CHEST.get(), StorageBlocks.LOCKED_HOPPER.get());
        tagger.apply(LibCommonTags.Blocks.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get());
        tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(StorageBlocks.LOCKED_CHEST.get(), StorageBlocks.LOCKED_BARREL.get());

        tagger.apply(StorageTags.Blocks.DEEPSLATE).add(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES, Blocks.CHISELED_DEEPSLATE, Blocks.REINFORCED_DEEPSLATE);
        tagger.apply(StorageTags.Blocks.PISTONS).add(Blocks.PISTON, Blocks.STICKY_PISTON);
    }
}
