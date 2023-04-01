package com.grim3212.assorted.storage.data;

import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.StorageTags;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class StorageItemTagProvider extends LibItemTagProvider {


    public StorageItemTagProvider(PackOutput output, CompletableFuture<Provider> lookup, TagsProvider<Block> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, IntrinsicTagAppender<Item>> tagger, Consumer<Tuple<TagKey<Block>, TagKey<Item>>> copier) {
        tagger.apply(ItemTags.PIGLIN_LOVED).add(StorageBlocks.GOLD_SAFE.get().asItem(), StorageBlocks.CHESTS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.BARRELS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.HOPPERS.get(StorageMaterial.GOLD).get().asItem(), StorageBlocks.SHULKERS.get(StorageMaterial.GOLD).get().asItem(), StorageItems.LEVEL_UPGRADES.get(StorageMaterial.GOLD).get().asItem());

        copier.accept(new Tuple(StorageTags.Blocks.DEEPSLATE, StorageTags.Items.DEEPSLATE));
        copier.accept(new Tuple(StorageTags.Blocks.PISTONS, StorageTags.Items.PISTONS));

        tagger.apply(LibCommonTags.Items.CHESTS_ENDER).add(StorageBlocks.LOCKED_ENDER_CHEST.get().asItem());
        tagger.apply(LibCommonTags.Items.CHESTS_WOODEN).add(StorageBlocks.LOCKED_CHEST.get().asItem());
        tagger.apply(LibCommonTags.Items.BARRELS_WOODEN).add(StorageBlocks.LOCKED_BARREL.get().asItem());

        copier.accept(new Tuple(LibCommonTags.Blocks.CHESTS, LibCommonTags.Items.CHESTS));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_0, StorageTags.Items.CHESTS_LEVEL_0));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_1, StorageTags.Items.CHESTS_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_2, StorageTags.Items.CHESTS_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_3, StorageTags.Items.CHESTS_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_4, StorageTags.Items.CHESTS_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_5, StorageTags.Items.CHESTS_LEVEL_5));

        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(LibCommonTags.Items.CHESTS_WOODEN);
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.CHESTS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5));

        copier.accept(new Tuple(LibCommonTags.Blocks.BARRELS, LibCommonTags.Items.BARRELS));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_0, StorageTags.Items.BARRELS_LEVEL_0));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_1, StorageTags.Items.BARRELS_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_2, StorageTags.Items.BARRELS_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_3, StorageTags.Items.BARRELS_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_4, StorageTags.Items.BARRELS_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_5, StorageTags.Items.BARRELS_LEVEL_5));

        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(LibCommonTags.Items.BARRELS_WOODEN);
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.BARRELS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5));

        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS, StorageTags.Items.HOPPERS));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_0, StorageTags.Items.HOPPERS_LEVEL_0));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_1, StorageTags.Items.HOPPERS_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_2, StorageTags.Items.HOPPERS_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_3, StorageTags.Items.HOPPERS_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_4, StorageTags.Items.HOPPERS_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_5, StorageTags.Items.HOPPERS_LEVEL_5));

        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(StorageTags.Items.HOPPERS);
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.HOPPERS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5));

        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_NORMAL, StorageTags.Items.SHULKERS_NORMAL));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_0, StorageTags.Items.SHULKERS_LEVEL_0));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_1, StorageTags.Items.SHULKERS_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_2, StorageTags.Items.SHULKERS_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_3, StorageTags.Items.SHULKERS_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_4, StorageTags.Items.SHULKERS_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_5, StorageTags.Items.SHULKERS_LEVEL_5));

        tagger.apply(StorageTags.Items.CAN_UPGRADE_LEVEL_0).addTag(StorageTags.Items.SHULKERS_NORMAL);
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_0, StorageTags.Items.CAN_UPGRADE_LEVEL_1));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_1, StorageTags.Items.CAN_UPGRADE_LEVEL_2));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_2, StorageTags.Items.CAN_UPGRADE_LEVEL_3));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_3, StorageTags.Items.CAN_UPGRADE_LEVEL_4));
        copier.accept(new Tuple(StorageTags.Blocks.SHULKERS_LEVEL_4, StorageTags.Items.CAN_UPGRADE_LEVEL_5));

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

        copier.accept(new Tuple(StorageTags.Blocks.CRATES, StorageTags.Items.CRATES));
        copier.accept(new Tuple(StorageTags.Blocks.CRATES_SINGLE, StorageTags.Items.CRATES_SINGLE));
        copier.accept(new Tuple(StorageTags.Blocks.CRATES_DOUBLE, StorageTags.Items.CRATES_DOUBLE));
        copier.accept(new Tuple(StorageTags.Blocks.CRATES_TRIPLE, StorageTags.Items.CRATES_TRIPLE));
        copier.accept(new Tuple(StorageTags.Blocks.CRATES_QUADRUPLE, StorageTags.Items.CRATES_QUADRUPLE));
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
