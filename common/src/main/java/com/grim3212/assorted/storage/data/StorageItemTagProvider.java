package com.grim3212.assorted.storage.data;

import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.StorageTags;
import com.grim3212.assorted.storage.common.block.*;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class StorageItemTagProvider extends LibItemTagProvider {


    public StorageItemTagProvider(PackOutput output, CompletableFuture<Provider> lookup, CompletableFuture<TagLookup<Block>> blockTagsProvider) {
        super(output, lookup, blockTagsProvider);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        tagger.apply(ItemTags.PIGLIN_LOVED).add(StorageBlocks.GOLD_SAFE.get().asItem(), StorageBlocks.CHESTS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.BARRELS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.HOPPERS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.SHULKERS.get(StorageMaterial.GOLD).get().asItem(), StorageItems.LEVEL_UPGRADES.get(StorageMaterial.GOLD).get().asItem());

        copier.accept(StorageTags.Blocks.DEEPSLATE, StorageTags.Items.DEEPSLATE);
        copier.accept(StorageTags.Blocks.PISTONS, StorageTags.Items.PISTONS);

        tagger.apply(LibCommonTags.Items.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get().asItem());
        tagger.apply(LibCommonTags.Items.CHESTS_WOODEN).add(StorageBlocks.LOCKED_CHEST.get().asItem());
        tagger.apply(LibCommonTags.Items.BARRELS_WOODEN).add(StorageBlocks.LOCKED_BARREL.get().asItem());

        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(LibCommonTags.Items.CHESTS_WOODEN);
        for (Entry<StorageMaterial, IRegistryObject<LockedChestBlock>> chest : StorageBlocks.CHESTS.entrySet()) {
            Item item = chest.getValue().get().asItem();

            switch (chest.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Items.CHESTS_LEVEL_1).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_2).add(item);
                    break;
                case 2:
                    tagger.apply(StorageTags.Items.CHESTS_LEVEL_2).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_3).add(item);
                    break;
                case 3:
                    tagger.apply(StorageTags.Items.CHESTS_LEVEL_3).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_4).add(item);
                    break;
                case 4:
                    tagger.apply(StorageTags.Items.CHESTS_LEVEL_4).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_5).add(item);
                    break;
                case 5:
                    tagger.apply(StorageTags.Items.CHESTS_LEVEL_5).add(item);
                    break;
                default:
                    tagger.apply(StorageTags.Items.CHESTS_LEVEL_0).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_1).add(item);
                    break;
            }
        }

        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(LibCommonTags.Items.BARRELS_WOODEN);
        for (Entry<StorageMaterial, IRegistryObject<LockedBarrelBlock>> barrel : StorageBlocks.BARRELS.entrySet()) {
            Item item = barrel.getValue().get().asItem();

            switch (barrel.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Items.BARRELS_LEVEL_1).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_2).add(item);
                    break;
                case 2:
                    tagger.apply(StorageTags.Items.BARRELS_LEVEL_2).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_3).add(item);
                    break;
                case 3:
                    tagger.apply(StorageTags.Items.BARRELS_LEVEL_3).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_4).add(item);
                    break;
                case 4:
                    tagger.apply(StorageTags.Items.BARRELS_LEVEL_4).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_5).add(item);
                    break;
                case 5:
                    tagger.apply(StorageTags.Items.BARRELS_LEVEL_5).add(item);
                    break;
                default:
                    tagger.apply(StorageTags.Items.BARRELS_LEVEL_0).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_1).add(item);
                    break;
            }
        }

        copier.accept(StorageTags.Blocks.HOPPERS, StorageTags.Items.HOPPERS);
        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(StorageTags.Items.HOPPERS);
        for (Entry<StorageMaterial, IRegistryObject<LockedHopperBlock>> hopper : StorageBlocks.HOPPERS.entrySet()) {
            Item item = hopper.getValue().get().asItem();

            switch (hopper.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Items.HOPPERS_LEVEL_1).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_2).add(item);
                    break;
                case 2:
                    tagger.apply(StorageTags.Items.HOPPERS_LEVEL_2).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_3).add(item);
                    break;
                case 3:
                    tagger.apply(StorageTags.Items.HOPPERS_LEVEL_3).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_4).add(item);
                    break;
                case 4:
                    tagger.apply(StorageTags.Items.HOPPERS_LEVEL_4).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_5).add(item);
                    break;
                case 5:
                    tagger.apply(StorageTags.Items.HOPPERS_LEVEL_5).add(item);
                    break;
                default:
                    tagger.apply(StorageTags.Items.HOPPERS_LEVEL_0).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_1).add(item);
                    break;
            }
        }

        copier.accept(StorageTags.Blocks.SHULKERS_NORMAL, StorageTags.Items.SHULKERS_NORMAL);
        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(StorageTags.Items.SHULKERS_NORMAL);
        for (Entry<StorageMaterial, IRegistryObject<LockedShulkerBoxBlock>> shulker : StorageBlocks.SHULKERS.entrySet()) {
            Item item = shulker.getValue().get().asItem();

            switch (shulker.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Items.SHULKERS_LEVEL_1).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_2).add(item);
                    break;
                case 2:
                    tagger.apply(StorageTags.Items.SHULKERS_LEVEL_2).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_3).add(item);
                    break;
                case 3:
                    tagger.apply(StorageTags.Items.SHULKERS_LEVEL_3).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_4).add(item);
                    break;
                case 4:
                    tagger.apply(StorageTags.Items.SHULKERS_LEVEL_4).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_5).add(item);
                    break;
                case 5:
                    tagger.apply(StorageTags.Items.SHULKERS_LEVEL_5).add(item);
                    break;
                default:
                    tagger.apply(StorageTags.Items.SHULKERS_LEVEL_0).add(item);
                    tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_1).add(item);
                    break;
            }
        }

        tagger.apply(StorageTags.Items.PAPER).add(Items.PAPER);

        // Logic from FunctionalStorage
        //@formatter:off
        tagger.apply(StorageTags.Items.CRAFTING_OVERRIDE)
                .add(Items.MELON, Items.MELON_SLICE)
                .add(Items.CLAY, Items.CLAY_BALL)
                .add(Items.GLOWSTONE, Items.GLOWSTONE_DUST)
                .add(Items.QUARTZ, Items.QUARTZ_BLOCK)
                .add(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE)
                .add(Items.AMETHYST_BLOCK, Items.AMETHYST_SHARD)
                .add(Items.SNOWBALL, Items.SNOW_BLOCK)
                .add(Items.BRICKS, Items.BRICK)
                .add(Items.NETHER_BRICK, Items.NETHER_BRICKS)
                .add(Items.NETHER_WART_BLOCK, Items.NETHER_WART)
                .add(Items.SANDSTONE, Items.SAND)
                .add(Items.RED_SANDSTONE, Items.RED_SAND);
        //@formatter:on

        copier.accept(StorageTags.Blocks.CRATES, StorageTags.Items.CRATES);
        copier.accept(StorageTags.Blocks.CRATES_SINGLE, StorageTags.Items.CRATES_SINGLE);
        copier.accept(StorageTags.Blocks.CRATES_DOUBLE, StorageTags.Items.CRATES_DOUBLE);
        copier.accept(StorageTags.Blocks.CRATES_TRIPLE, StorageTags.Items.CRATES_TRIPLE);
        copier.accept(StorageTags.Blocks.CRATES_QUADRUPLE, StorageTags.Items.CRATES_QUADRUPLE);
        tagger.apply(StorageTags.Items.UPGRADES).add(StorageItems.AMOUNT_UPGRADE.get(), StorageItems.GLOW_UPGRADE.get(), StorageItems.REDSTONE_UPGRADE.get(), StorageItems.VOID_UPGRADE.get());

        for (Entry<StorageMaterial, IRegistryObject<LevelUpgradeItem>> levelUpgrade : StorageItems.LEVEL_UPGRADES.entrySet()) {
            LevelUpgradeItem item = levelUpgrade.getValue().get();

            switch (levelUpgrade.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Items.STORAGE_LEVEL_1_UPGRADES).add(item);
                    break;
                case 2:
                    tagger.apply(StorageTags.Items.STORAGE_LEVEL_2_UPGRADES).add(item);
                    break;
                case 3:
                    tagger.apply(StorageTags.Items.STORAGE_LEVEL_3_UPGRADES).add(item);
                    break;
                case 4:
                    tagger.apply(StorageTags.Items.STORAGE_LEVEL_4_UPGRADES).add(item);
                    break;
                case 5:
                    tagger.apply(StorageTags.Items.STORAGE_LEVEL_5_UPGRADES).add(item);
                    break;
                default:
                    tagger.apply(StorageTags.Items.STORAGE_LEVEL_0_UPGRADES).add(item);
                    break;
            }
        }
        tagger.apply(StorageTags.Items.STORAGE_LEVEL_UPGRADES).addTag(StorageTags.Items.STORAGE_LEVEL_0_UPGRADES).addTag(StorageTags.Items.STORAGE_LEVEL_1_UPGRADES).addTag(StorageTags.Items.STORAGE_LEVEL_2_UPGRADES).addTag(StorageTags.Items.STORAGE_LEVEL_3_UPGRADES).addTag(StorageTags.Items.STORAGE_LEVEL_4_UPGRADES).addTag(StorageTags.Items.STORAGE_LEVEL_5_UPGRADES);

        for (Entry<StorageMaterial, IRegistryObject<BagItem>> bag : StorageItems.BAGS.entrySet()) {
            BagItem item = bag.getValue().get();

            switch (bag.getKey().getStorageLevel()) {
                case 1:
                    tagger.apply(StorageTags.Items.BAGS_LEVEL_1).add(item);
                    break;
                case 2:
                    tagger.apply(StorageTags.Items.BAGS_LEVEL_2).add(item);
                    break;
                case 3:
                    tagger.apply(StorageTags.Items.BAGS_LEVEL_3).add(item);
                    break;
                case 4:
                    tagger.apply(StorageTags.Items.BAGS_LEVEL_4).add(item);
                    break;
                case 5:
                    tagger.apply(StorageTags.Items.BAGS_LEVEL_5).add(item);
                    break;
                default:
                    tagger.apply(StorageTags.Items.BAGS_LEVEL_0).add(item);
                    break;
            }
        }

        tagger.apply(StorageTags.Items.BAGS_LEVEL_0).add(StorageItems.BAG.get());
        tagger.apply(StorageTags.Items.BAGS).add(StorageItems.ENDER_BAG.get());
        tagger.apply(StorageTags.Items.BAGS).addTag(StorageTags.Items.BAGS_LEVEL_0).addTag(StorageTags.Items.BAGS_LEVEL_1).addTag(StorageTags.Items.BAGS_LEVEL_2).addTag(StorageTags.Items.BAGS_LEVEL_3).addTag(StorageTags.Items.BAGS_LEVEL_4).addTag(StorageTags.Items.BAGS_LEVEL_5);

    }
}
