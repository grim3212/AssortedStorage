package com.grim3212.assorted.storage.data;

import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.StorageTags;
import com.grim3212.assorted.storage.common.block.*;
import com.grim3212.assorted.storage.common.crafting.*;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;
import java.util.function.Consumer;

public class StorageRecipes extends ConditionalRecipeProvider {
    public StorageRecipes(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public void registerConditions() {
        this.addConditions(partEnabled(StorageConditions.Parts.UPGRADES), StorageItems.BLANK_UPGRADE.getId());
        this.addConditions(partEnabled(StorageConditions.Parts.BAGS), StorageItems.BAG.getId(), StorageItems.ENDER_BAG.getId());

        this.addConditions(partEnabled(StorageConditions.Parts.CRATES), StorageBlocks.CRATE_CONTROLLER.getId(), new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId() + "_gold"), new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId() + "_copper"), new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId() + "_iron"));
        this.addConditions(and(partEnabled(StorageConditions.Parts.UPGRADES), itemTagExists(StorageTags.Items.INGOTS_BRONZE)), new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId() + "_bronze"));
        this.addConditions(and(partEnabled(StorageConditions.Parts.UPGRADES), itemTagExists(StorageTags.Items.INGOTS_ALUMINUM)), new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId() + "_aluminum"));
        this.addConditions(and(partEnabled(StorageConditions.Parts.UPGRADES), itemTagExists(StorageTags.Items.INGOTS_STEEL)), new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId() + "_steel"));
        this.addConditions(and(partEnabled(StorageConditions.Parts.CRATES), partEnabled(StorageConditions.Parts.UPGRADES)), StorageItems.AMOUNT_UPGRADE.getId(), StorageItems.GLOW_UPGRADE.getId(), new ResourceLocation(StorageItems.GLOW_UPGRADE.getId() + "_glowstone"), StorageItems.REDSTONE_UPGRADE.getId(), StorageItems.VOID_UPGRADE.getId());

        this.addConditions(itemTagExists(StorageTags.Items.INGOTS_ALUMINUM), new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_aluminum"), new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_aluminum_alt"));
        this.addConditions(itemTagExists(StorageTags.Items.INGOTS_STEEL), new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_steel"), new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_steel_alt"));
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        super.buildRecipes(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.WOOD_CABINET.get()).define('X', ItemTags.PLANKS).define('C', LibCommonTags.Items.CHESTS_WOODEN).pattern(" X ").pattern("XCX").pattern(" X ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.GLASS_CABINET.get()).define('X', ItemTags.PLANKS).define('G', LibCommonTags.Items.GLASS).define('C', LibCommonTags.Items.CHESTS_WOODEN).pattern(" X ").pattern("GCG").pattern(" X ").unlockedBy("has_glass", has(LibCommonTags.Items.GLASS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.GOLD_SAFE.get()).define('G', LibCommonTags.Items.INGOTS_GOLD).define('C', StorageBlocks.OBSIDIAN_SAFE.get()).pattern(" G ").pattern("GCG").pattern(" G ").unlockedBy("has_obsidian_chest", has(StorageBlocks.OBSIDIAN_SAFE.get())).unlockedBy("has_gold", has(LibCommonTags.Items.INGOTS_GOLD)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.OBSIDIAN_SAFE.get()).define('G', LibCommonTags.Items.CHESTS_WOODEN).define('X', LibCommonTags.Items.OBSIDIAN).pattern(" X ").pattern("XGX").pattern(" X ").unlockedBy("has_obsidian", has(LibCommonTags.Items.OBSIDIAN)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.LOCKER.get()).define('X', LibCommonTags.Items.INGOTS_IRON).define('C', LibCommonTags.Items.CHESTS_WOODEN).pattern(" X ").pattern("XCX").pattern(" X ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.ITEM_TOWER.get(), 2).define('I', LibCommonTags.Items.INGOTS_IRON).define('C', LibCommonTags.Items.CHESTS_WOODEN).pattern("I I").pattern("ICI").pattern("I I").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.OAK_WAREHOUSE_CRATE.get()).define('P', Items.OAK_PLANKS).define('L', ItemTags.OAK_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_oak_planks", has(Items.OAK_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.BIRCH_WAREHOUSE_CRATE.get()).define('P', Items.BIRCH_PLANKS).define('L', ItemTags.BIRCH_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_birch_planks", has(Items.BIRCH_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get()).define('P', Items.SPRUCE_PLANKS).define('L', ItemTags.SPRUCE_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_spruce_planks", has(Items.SPRUCE_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.ACACIA_WAREHOUSE_CRATE.get()).define('P', Items.ACACIA_PLANKS).define('L', ItemTags.ACACIA_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_acacia_planks", has(Items.ACACIA_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get()).define('P', Items.DARK_OAK_PLANKS).define('L', ItemTags.DARK_OAK_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_dark_oak_planks", has(Items.DARK_OAK_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get()).define('P', Items.JUNGLE_PLANKS).define('L', ItemTags.JUNGLE_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_jungle_planks", has(Items.JUNGLE_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.WARPED_WAREHOUSE_CRATE.get()).define('P', Items.WARPED_PLANKS).define('L', Items.WARPED_STEM).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_warped_planks", has(Items.WARPED_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get()).define('P', Items.CRIMSON_PLANKS).define('L', Items.CRIMSON_STEM).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_crimson_planks", has(Items.CRIMSON_PLANKS)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get()).define('P', Items.MANGROVE_PLANKS).define('L', Items.MANGROVE_LOG).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_mangrove_planks", has(Items.MANGROVE_PLANKS)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.LOCKSMITH_LOCK.get(), 3).define('X', LibCommonTags.Items.INGOTS_IRON).pattern(" X ").pattern("X X").pattern("XXX").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.LOCKSMITH_KEY.get(), 3).define('X', LibCommonTags.Items.INGOTS_IRON).pattern("XX").pattern("XX").pattern("X ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.KEY_RING.get(), 1).define('X', LibCommonTags.Items.INGOTS_IRON).define('K', StorageItems.LOCKSMITH_KEY.get()).pattern(" X ").pattern("XKX").pattern(" X ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).unlockedBy("has_key", has(StorageItems.LOCKSMITH_KEY.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).define('L', StorageItems.LOCKSMITH_LOCK.get()).define('K', StorageItems.LOCKSMITH_KEY.get()).define('W', Blocks.CRAFTING_TABLE).pattern("L").pattern("K").pattern("W").unlockedBy("has_lock", has(StorageItems.LOCKSMITH_LOCK.get())).save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).define('L', StorageItems.LOCKSMITH_LOCK.get()).define('K', StorageItems.LOCKSMITH_KEY.get()).define('W', Blocks.CRAFTING_TABLE).pattern("K").pattern("L").pattern("W").unlockedBy("has_lock", has(StorageItems.LOCKSMITH_LOCK.get())).save(consumer, new ResourceLocation(Constants.MOD_ID, "locksmith_workbench_alt"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.BLANK_UPGRADE.get(), 1).define('W', StorageTags.Items.PAPER).define('P', ItemTags.PLANKS).pattern("PPP").pattern("PWP").pattern("PPP").unlockedBy("has_paper", has(StorageTags.Items.PAPER)).save(consumer, StorageItems.BLANK_UPGRADE.getId());
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.ENDER_BAG.get(), 1).define('S', LibCommonTags.Items.STRING).define('C', LibCommonTags.Items.CHESTS_ENDER).define('L', LibCommonTags.Items.LEATHER).pattern("SLS").pattern("LCL").pattern("LLL").unlockedBy("has_leather", has(LibCommonTags.Items.LEATHER)).unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_ENDER)).save(consumer, StorageItems.ENDER_BAG.getId());

        SpecialRecipeBuilder.special(LockedEnderChestRecipe.SERIALIZER).save(consumer, new ResourceLocation(Constants.MOD_ID, "locked_ender_chest").toString());
        SpecialRecipeBuilder.special(LockedChestRecipe.SERIALIZER).save(consumer, new ResourceLocation(Constants.MOD_ID, "locked_chest").toString());
        SpecialRecipeBuilder.special(LockedBarrelRecipe.SERIALIZER).save(consumer, new ResourceLocation(Constants.MOD_ID, "locked_barrel").toString());
        SpecialRecipeBuilder.special(LockedShulkerBoxRecipe.SERIALIZER).save(consumer, new ResourceLocation(Constants.MOD_ID, "locked_shulker_box").toString());
        SpecialRecipeBuilder.special(LockedShulkerBoxColoring.SERIALIZER).save(consumer, new ResourceLocation(Constants.MOD_ID, "shulker_box_coloring").toString());
        SpecialRecipeBuilder.special(BagColoringRecipe.SERIALIZER).save(consumer, new ResourceLocation(Constants.MOD_ID, "bag_coloring").toString());

        for (Map.Entry<StorageMaterial, IRegistryObject<LockedChestBlock>> chest : StorageBlocks.CHESTS.entrySet()) {
            TagKey<Item> mat = chest.getKey().getMaterial();
            this.addConditions(and(partEnabled(StorageConditions.Parts.CHESTS), itemTagExists(mat)), chest.getValue().getId());

            switch (chest.getKey().getStorageLevel()) {
                case 1:
                    LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_0)).unlockedBy("has_material", has(mat)).save(consumer, chest.getValue().getId());
                    break;
                case 2:
                    LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_1)).unlockedBy("has_material", has(mat)).save(consumer, chest.getValue().getId());
                    break;
                case 3:
                    LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_2)).unlockedBy("has_material", has(mat)).save(consumer, chest.getValue().getId());
                    break;
                case 4:
                    LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_3)).unlockedBy("has_material", has(mat)).save(consumer, chest.getValue().getId());
                    break;
                case 5:
                    LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_4)).unlockedBy("has_material", has(mat)).save(consumer, chest.getValue().getId());
                    break;
                default:
                    LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', LibCommonTags.Items.CHESTS_WOODEN).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_WOODEN)).unlockedBy("has_material", has(mat)).save(consumer, chest.getValue().getId());
                    break;
            }


        }

        for (Map.Entry<StorageMaterial, IRegistryObject<LockedHopperBlock>> hopper : StorageBlocks.HOPPERS.entrySet()) {
            TagKey<Item> mat = hopper.getKey().getMaterial();
            Block matchChest = StorageBlocks.CHESTS.get(hopper.getKey()).get();

            this.addConditions(partEnabled(StorageConditions.Parts.HOPPERS), new ResourceLocation(hopper.getValue().getId() + "_chest"));
            this.addConditions(and(partEnabled(StorageConditions.Parts.HOPPERS), itemTagExists(mat)), hopper.getValue().getId());

            LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', matchChest).define('M', LibCommonTags.Items.INGOTS_IRON).pattern("M M").pattern("MCM").pattern(" M ").unlockedBy("has_chest", has(matchChest)).unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)).save(consumer, new ResourceLocation(hopper.getValue().getId() + "_chest"));


            switch (hopper.getKey().getStorageLevel()) {
                case 1:
                    LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_0)).unlockedBy("has_material", has(mat)).save(consumer, hopper.getValue().getId());
                    break;
                case 2:
                    LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_1)).unlockedBy("has_material", has(mat)).save(consumer, hopper.getValue().getId());
                    break;
                case 3:
                    LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_2)).unlockedBy("has_material", has(mat)).save(consumer, hopper.getValue().getId());
                    break;
                case 4:
                    LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_3)).unlockedBy("has_material", has(mat)).save(consumer, hopper.getValue().getId());
                    break;
                case 5:
                    LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_4)).unlockedBy("has_material", has(mat)).save(consumer, hopper.getValue().getId());
                    break;
                default:
                    LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', Items.HOPPER).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(Items.HOPPER)).unlockedBy("has_material", has(mat)).save(consumer, hopper.getValue().getId());
                    break;
            }
        }

        for (Map.Entry<StorageMaterial, IRegistryObject<LockedBarrelBlock>> barrel : StorageBlocks.BARRELS.entrySet()) {
            TagKey<Item> mat = barrel.getKey().getMaterial();

            this.addConditions(and(partEnabled(StorageConditions.Parts.BARRELS), itemTagExists(mat)), barrel.getValue().getId());

            switch (barrel.getKey().getStorageLevel()) {
                case 1:
                    LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_0)).unlockedBy("has_material", has(mat)).save(consumer, barrel.getValue().getId());
                    break;
                case 2:
                    LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_1)).unlockedBy("has_material", has(mat)).save(consumer, barrel.getValue().getId());
                    break;
                case 3:
                    LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_2)).unlockedBy("has_material", has(mat)).save(consumer, barrel.getValue().getId());
                    break;
                case 4:
                    LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_3)).unlockedBy("has_material", has(mat)).save(consumer, barrel.getValue().getId());
                    break;
                case 5:
                    LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_4)).unlockedBy("has_material", has(mat)).save(consumer, barrel.getValue().getId());
                    break;
                default:
                    LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', LibCommonTags.Items.BARRELS_WOODEN).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(LibCommonTags.Items.BARRELS_WOODEN)).unlockedBy("has_material", has(mat)).save(consumer, barrel.getValue().getId());
                    break;
            }
        }

        for (Map.Entry<StorageMaterial, IRegistryObject<LockedShulkerBoxBlock>> shulker : StorageBlocks.SHULKERS.entrySet()) {
            TagKey<Item> mat = shulker.getKey().getMaterial();

            this.addConditions(and(partEnabled(StorageConditions.Parts.SHULKERS), itemTagExists(mat)), shulker.getValue().getId());

            switch (shulker.getKey().getStorageLevel()) {
                case 1:
                    LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_0)).unlockedBy("has_material", has(mat)).save(consumer, shulker.getValue().getId());
                    break;
                case 2:
                    LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_1)).unlockedBy("has_material", has(mat)).save(consumer, shulker.getValue().getId());
                    break;
                case 3:
                    LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_2)).unlockedBy("has_material", has(mat)).save(consumer, shulker.getValue().getId());
                    break;
                case 4:
                    LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_3)).unlockedBy("has_material", has(mat)).save(consumer, shulker.getValue().getId());
                    break;
                case 5:
                    LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_4)).unlockedBy("has_material", has(mat)).save(consumer, shulker.getValue().getId());
                    break;
                default:
                    LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_NORMAL).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_NORMAL)).unlockedBy("has_material", has(mat)).save(consumer, shulker.getValue().getId());
                    break;
            }
        }

        LockedUpgradingRecipeBuilder.shaped(StorageItems.BAG.get(), 1).define('S', LibCommonTags.Items.STRING).define('C', LibCommonTags.Items.CHESTS_WOODEN).define('L', LibCommonTags.Items.LEATHER).pattern("SLS").pattern("LCL").pattern("LLL").unlockedBy("has_leather", has(LibCommonTags.Items.LEATHER)).unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_WOODEN)).save(consumer, StorageItems.BAG.getId());

        for (Map.Entry<StorageMaterial, IRegistryObject<BagItem>> bag : StorageItems.BAGS.entrySet()) {
            TagKey<Item> mat = bag.getKey().getMaterial();
            Block matchChest = StorageBlocks.CHESTS.get(bag.getKey()).get();

            this.addConditions(partEnabled(StorageConditions.Parts.BAGS), new ResourceLocation(bag.getValue().getId() + "_chest"));
            this.addConditions(and(partEnabled(StorageConditions.Parts.BAGS), itemTagExists(mat)), bag.getValue().getId());

            LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', matchChest).define('S', LibCommonTags.Items.STRING).define('L', LibCommonTags.Items.LEATHER).pattern("SLS").pattern("LCL").pattern("LLL").unlockedBy("has_chest", has(matchChest)).unlockedBy("has_leather", has(LibCommonTags.Items.LEATHER)).save(consumer, new ResourceLocation(bag.getValue().getId() + "_chest"));

            switch (bag.getKey().getStorageLevel()) {
                case 1:
                    LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_0)).unlockedBy("has_material", has(mat)).save(consumer, bag.getValue().getId());
                    break;
                case 2:
                    LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_1)).unlockedBy("has_material", has(mat)).save(consumer, bag.getValue().getId());
                    break;
                case 3:
                    LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_2)).unlockedBy("has_material", has(mat)).save(consumer, bag.getValue().getId());
                    break;
                case 4:
                    LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_3)).unlockedBy("has_material", has(mat)).save(consumer, bag.getValue().getId());
                    break;
                case 5:
                    LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_4)).unlockedBy("has_material", has(mat)).save(consumer, bag.getValue().getId());
                    break;
                default:
                    LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageItems.BAG.get()).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageItems.BAG.get())).unlockedBy("has_material", has(mat)).save(consumer, bag.getValue().getId());
                    break;
            }
        }

        for (Map.Entry<StorageMaterial, IRegistryObject<LevelUpgradeItem>> levelUpgrade : StorageItems.LEVEL_UPGRADES.entrySet()) {
            this.addConditions(and(partEnabled(StorageConditions.Parts.UPGRADES), itemTagExists(levelUpgrade.getKey().getMaterial())), levelUpgrade.getValue().getId());
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, levelUpgrade.getValue().get(), 1).define('M', levelUpgrade.getKey().getMaterial()).define('B', StorageItems.BLANK_UPGRADE.get()).pattern("MMM").pattern("MBM").pattern("MMM").unlockedBy("has_upgrade", has(StorageItems.BLANK_UPGRADE.get())).unlockedBy("has_material", has(levelUpgrade.getKey().getMaterial())).save(consumer, levelUpgrade.getValue().getId());
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_CONTROLLER.get(), 1).define('R', Items.COMPARATOR).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('E', LibCommonTags.Items.ENDER_PEARLS).pattern("DDD").pattern("CRC").pattern("DED").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_ender_pearls", has(LibCommonTags.Items.ENDER_PEARLS)).save(consumer, StorageBlocks.CRATE_CONTROLLER.getId());
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_BRIDGE.get(), 1).define('R', Items.REPEATER).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('I', LibCommonTags.Items.INGOTS_GOLD).pattern("DDD").pattern("RCR").pattern("DID").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_gold", has(LibCommonTags.Items.INGOTS_GOLD)).save(consumer, new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId() + "_gold"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_BRIDGE.get(), 1).define('R', Items.REPEATER).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('I', LibCommonTags.Items.INGOTS_COPPER).pattern("DDD").pattern("RCR").pattern("DID").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_copper", has(LibCommonTags.Items.INGOTS_COPPER)).save(consumer, new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId() + "_copper"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_BRIDGE.get(), 1).define('R', Items.REPEATER).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('I', StorageTags.Items.INGOTS_BRONZE).pattern("DDD").pattern("RCR").pattern("DID").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_bronze", has(StorageTags.Items.INGOTS_BRONZE)).save(consumer, new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId() + "_bronze"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_COMPACTING.get(), 1).define('P', StorageTags.Items.PISTONS).define('C', StorageTags.Items.CRATES_TRIPLE).define('D', StorageTags.Items.DEEPSLATE).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("DDD").pattern("PCP").pattern("DID").unlockedBy("has_triple_crate", has(StorageTags.Items.CRATES_TRIPLE)).unlockedBy("has_pistons", has(StorageTags.Items.PISTONS)).save(consumer, new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId() + "_iron"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_COMPACTING.get(), 1).define('P', StorageTags.Items.PISTONS).define('C', StorageTags.Items.CRATES_TRIPLE).define('D', StorageTags.Items.DEEPSLATE).define('I', StorageTags.Items.INGOTS_ALUMINUM).pattern("DDD").pattern("PCP").pattern("DID").unlockedBy("has_triple_crate", has(StorageTags.Items.CRATES_TRIPLE)).unlockedBy("has_pistons", has(StorageTags.Items.PISTONS)).save(consumer, new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId() + "_aluminum"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_COMPACTING.get(), 1).define('P', StorageTags.Items.PISTONS).define('C', StorageTags.Items.CRATES_TRIPLE).define('D', StorageTags.Items.DEEPSLATE).define('I', StorageTags.Items.INGOTS_STEEL).pattern("DDD").pattern("PCP").pattern("DID").unlockedBy("has_triple_crate", has(StorageTags.Items.CRATES_TRIPLE)).unlockedBy("has_pistons", has(StorageTags.Items.PISTONS)).save(consumer, new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId() + "_steel"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, StorageItems.AMOUNT_UPGRADE.get(), 1).requires(StorageItems.BLANK_UPGRADE.get()).requires(ItemTags.SIGNS).unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).save(consumer, StorageItems.AMOUNT_UPGRADE.getId());
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, StorageItems.GLOW_UPGRADE.get(), 1).requires(StorageItems.BLANK_UPGRADE.get()).requires(Items.GLOW_INK_SAC).unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC)).save(consumer, StorageItems.GLOW_UPGRADE.getId());
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, StorageItems.GLOW_UPGRADE.get(), 1).requires(StorageItems.BLANK_UPGRADE.get()).requires(LibCommonTags.Items.DUSTS_GLOWSTONE).unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).unlockedBy("has_glowstone_dust", has(LibCommonTags.Items.DUSTS_GLOWSTONE)).save(consumer, new ResourceLocation(StorageItems.GLOW_UPGRADE.getId() + "_glowstone"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.REDSTONE_UPGRADE.get(), 2).define('U', StorageItems.BLANK_UPGRADE.get()).define('D', LibCommonTags.Items.DUSTS_REDSTONE).define('R', Items.REPEATER).define('C', Items.COMPARATOR).pattern("DCD").pattern("RUR").pattern("DCD").unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).save(consumer, StorageItems.REDSTONE_UPGRADE.getId());
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.VOID_UPGRADE.get(), 1).define('U', StorageItems.BLANK_UPGRADE.get()).define('O', LibCommonTags.Items.OBSIDIAN).pattern("OOO").pattern("OUO").pattern("OOO").unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).save(consumer, StorageItems.VOID_UPGRADE.getId());

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', LibCommonTags.Items.RODS_BLAZE).define('S', LibCommonTags.Items.RODS_WOODEN).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("I I").pattern(" B ").pattern(" S ").unlockedBy("has_blaze_rod", has(LibCommonTags.Items.RODS_BLAZE)).save(consumer, new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_iron"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', LibCommonTags.Items.RODS_BLAZE).define('S', LibCommonTags.Items.RODS_WOODEN).define('I', LibCommonTags.Items.INGOTS_IRON).pattern("I I").pattern(" S ").pattern(" B ").unlockedBy("has_blaze_rod", has(LibCommonTags.Items.RODS_BLAZE)).save(consumer, new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_iron_alt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', LibCommonTags.Items.RODS_BLAZE).define('S', LibCommonTags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_ALUMINUM).pattern("I I").pattern(" B ").pattern(" S ").unlockedBy("has_blaze_rod", has(LibCommonTags.Items.RODS_BLAZE)).save(consumer, new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_aluminum"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', LibCommonTags.Items.RODS_BLAZE).define('S', LibCommonTags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_ALUMINUM).pattern("I I").pattern(" S ").pattern(" B ").unlockedBy("has_blaze_rod", has(LibCommonTags.Items.RODS_BLAZE)).save(consumer, new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_aluminum_alt"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', LibCommonTags.Items.RODS_BLAZE).define('S', LibCommonTags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_STEEL).pattern("I I").pattern(" B ").pattern(" S ").unlockedBy("has_blaze_rod", has(LibCommonTags.Items.RODS_BLAZE)).save(consumer, new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_steel"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', LibCommonTags.Items.RODS_BLAZE).define('S', LibCommonTags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_STEEL).pattern("I I").pattern(" S ").pattern(" B ").unlockedBy("has_blaze_rod", has(LibCommonTags.Items.RODS_BLAZE)).save(consumer, new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_steel_alt"));

        for (StorageBlocks.CrateGroup group : StorageBlocks.CRATES) {
            this.addConditions(partEnabled(StorageConditions.Parts.CRATES), group.SINGLE.getId(), group.DOUBLE.getId(), group.TRIPLE.getId(), group.QUADRUPLE.getId());

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.SINGLE.get(), 1).define('L', group.getType().getLogTag()).define('C', LibCommonTags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern(" D ").pattern("LCL").pattern(" D ").unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE)).save(consumer, group.SINGLE.getId());
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.DOUBLE.get(), 1).define('L', group.getType().getLogTag()).define('C', LibCommonTags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern("DCD").pattern("L L").pattern("DCD").unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE)).save(consumer, group.DOUBLE.getId());
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.TRIPLE.get(), 1).define('L', group.getType().getLogTag()).define('C', LibCommonTags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern(" D ").pattern("LCL").pattern("CDC").unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE)).save(consumer, group.TRIPLE.getId());
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.QUADRUPLE.get(), 1).define('L', group.getType().getLogTag()).define('C', LibCommonTags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern("CDC").pattern("L L").pattern("CDC").unlockedBy("has_chest", has(LibCommonTags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE)).save(consumer, group.QUADRUPLE.getId());
        }
    }
}
