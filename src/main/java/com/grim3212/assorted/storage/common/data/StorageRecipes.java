package com.grim3212.assorted.storage.common.data;

import java.util.Map.Entry;
import java.util.function.Consumer;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.StorageBlocks.CrateGroup;
import com.grim3212.assorted.storage.common.crafting.BagColoringRecipe;
import com.grim3212.assorted.storage.common.crafting.LockedBarrelRecipe;
import com.grim3212.assorted.storage.common.crafting.LockedChestRecipe;
import com.grim3212.assorted.storage.common.crafting.LockedEnderChestRecipe;
import com.grim3212.assorted.storage.common.crafting.LockedShulkerBoxColoring;
import com.grim3212.assorted.storage.common.crafting.LockedShulkerBoxRecipe;
import com.grim3212.assorted.storage.common.crafting.LockedUpgradingRecipeBuilder;
import com.grim3212.assorted.storage.common.handler.EnabledCondition;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.item.upgrades.LevelUpgradeItem;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageTags;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.RegistryObject;

public class StorageRecipes extends RecipeProvider implements IConditionBuilder {

	public StorageRecipes(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.WOOD_CABINET.get()).define('X', ItemTags.PLANKS).define('C', Tags.Items.CHESTS_WOODEN).pattern(" X ").pattern("XCX").pattern(" X ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.GLASS_CABINET.get()).define('X', ItemTags.PLANKS).define('G', Tags.Items.GLASS).define('C', Tags.Items.CHESTS_WOODEN).pattern(" X ").pattern("GCG").pattern(" X ").unlockedBy("has_glass", has(Tags.Items.GLASS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.GOLD_SAFE.get()).define('G', Tags.Items.INGOTS_GOLD).define('C', StorageBlocks.OBSIDIAN_SAFE.get()).pattern(" G ").pattern("GCG").pattern(" G ").unlockedBy("has_obsidian_chest", has(StorageBlocks.OBSIDIAN_SAFE.get())).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.OBSIDIAN_SAFE.get()).define('G', Tags.Items.CHESTS_WOODEN).define('X', Tags.Items.OBSIDIAN).pattern(" X ").pattern("XGX").pattern(" X ").unlockedBy("has_obsidian", has(Tags.Items.OBSIDIAN)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.LOCKER.get()).define('X', Tags.Items.INGOTS_IRON).define('C', Tags.Items.CHESTS_WOODEN).pattern(" X ").pattern("XCX").pattern(" X ").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.ITEM_TOWER.get(), 2).define('I', Tags.Items.INGOTS_IRON).define('C', Tags.Items.CHESTS_WOODEN).pattern("I I").pattern("ICI").pattern("I I").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.OAK_WAREHOUSE_CRATE.get()).define('P', Items.OAK_PLANKS).define('L', ItemTags.OAK_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_oak_planks", has(Items.OAK_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.BIRCH_WAREHOUSE_CRATE.get()).define('P', Items.BIRCH_PLANKS).define('L', ItemTags.BIRCH_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_birch_planks", has(Items.BIRCH_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get()).define('P', Items.SPRUCE_PLANKS).define('L', ItemTags.SPRUCE_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_spruce_planks", has(Items.SPRUCE_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.ACACIA_WAREHOUSE_CRATE.get()).define('P', Items.ACACIA_PLANKS).define('L', ItemTags.ACACIA_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_acacia_planks", has(Items.ACACIA_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get()).define('P', Items.DARK_OAK_PLANKS).define('L', ItemTags.DARK_OAK_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_dark_oak_planks", has(Items.DARK_OAK_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get()).define('P', Items.JUNGLE_PLANKS).define('L', ItemTags.JUNGLE_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_jungle_planks", has(Items.JUNGLE_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.WARPED_WAREHOUSE_CRATE.get()).define('P', Items.WARPED_PLANKS).define('L', Items.WARPED_STEM).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_warped_planks", has(Items.WARPED_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get()).define('P', Items.CRIMSON_PLANKS).define('L', Items.CRIMSON_STEM).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_crimson_planks", has(Items.CRIMSON_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get()).define('P', Items.MANGROVE_PLANKS).define('L', Items.MANGROVE_LOG).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_mangrove_planks", has(Items.MANGROVE_PLANKS)).save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.LOCKSMITH_LOCK.get(), 3).define('X', Tags.Items.INGOTS_IRON).pattern(" X ").pattern("X X").pattern("XXX").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.LOCKSMITH_KEY.get(), 3).define('X', Tags.Items.INGOTS_IRON).pattern("XX").pattern("XX").pattern("X ").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.KEY_RING.get(), 1).define('X', Tags.Items.INGOTS_IRON).define('K', StorageItems.LOCKSMITH_KEY.get()).pattern(" X ").pattern("XKX").pattern(" X ").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).unlockedBy("has_key", has(StorageItems.LOCKSMITH_KEY.get())).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).define('L', StorageItems.LOCKSMITH_LOCK.get()).define('K', StorageItems.LOCKSMITH_KEY.get()).define('W', Blocks.CRAFTING_TABLE).pattern("L").pattern("K").pattern("W").unlockedBy("has_lock", has(StorageItems.LOCKSMITH_LOCK.get())).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).define('L', StorageItems.LOCKSMITH_LOCK.get()).define('K', StorageItems.LOCKSMITH_KEY.get()).define('W', Blocks.CRAFTING_TABLE).pattern("K").pattern("L").pattern("W").unlockedBy("has_lock", has(StorageItems.LOCKSMITH_LOCK.get())).save(consumer, new ResourceLocation(AssortedStorage.MODID, "locksmith_workbench_alt"));

		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.BLANK_UPGRADE.get(), 1).define('W', StorageTags.Items.PAPER).define('P', ItemTags.PLANKS).pattern("PPP").pattern("PWP").pattern("PPP").unlockedBy("has_paper", has(StorageTags.Items.PAPER))::save).generateAdvancement().build(consumer, StorageItems.BLANK_UPGRADE.getId());
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.ENDER_BAG.get(), 1).define('S', Tags.Items.STRING).define('C', Tags.Items.CHESTS_ENDER).define('L', Tags.Items.LEATHER).pattern("SLS").pattern("LCL").pattern("LLL").unlockedBy("has_leather", has(Tags.Items.LEATHER)).unlockedBy("has_chest", has(Tags.Items.CHESTS_ENDER))::save).generateAdvancement().build(consumer,
				StorageItems.ENDER_BAG.getId());

		SpecialRecipeBuilder.special(LockedEnderChestRecipe.SERIALIZER).save(consumer, new ResourceLocation(AssortedStorage.MODID, "locked_ender_chest").toString());
		SpecialRecipeBuilder.special(LockedChestRecipe.SERIALIZER).save(consumer, new ResourceLocation(AssortedStorage.MODID, "locked_chest").toString());
		SpecialRecipeBuilder.special(LockedBarrelRecipe.SERIALIZER).save(consumer, new ResourceLocation(AssortedStorage.MODID, "locked_barrel").toString());
		SpecialRecipeBuilder.special(LockedShulkerBoxRecipe.SERIALIZER).save(consumer, new ResourceLocation(AssortedStorage.MODID, "locked_shulker_box").toString());
		SpecialRecipeBuilder.special(LockedShulkerBoxColoring.SERIALIZER).save(consumer, new ResourceLocation(AssortedStorage.MODID, "shulker_box_coloring").toString());
		SpecialRecipeBuilder.special(BagColoringRecipe.SERIALIZER).save(consumer, new ResourceLocation(AssortedStorage.MODID, "bag_coloring").toString());

		for (Entry<StorageMaterial, RegistryObject<LockedChestBlock>> chest : StorageBlocks.CHESTS.entrySet()) {
			TagKey<Item> mat = chest.getKey().getMaterial();

			switch (chest.getKey().getStorageLevel()) {
				case 1:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CHESTS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_0)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							chest.getValue().getId());
					break;
				case 2:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CHESTS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_1)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							chest.getValue().getId());
					break;
				case 3:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CHESTS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_2)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							chest.getValue().getId());
					break;
				case 4:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CHESTS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_3)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							chest.getValue().getId());
					break;
				case 5:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CHESTS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', StorageTags.Items.CHESTS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(StorageTags.Items.CHESTS_LEVEL_4)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							chest.getValue().getId());
					break;
				default:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CHESTS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(chest.getValue().get(), 1).define('C', Tags.Items.CHESTS_WOODEN).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, chest.getValue().getId());
					break;
			}
		}

		for (Entry<StorageMaterial, RegistryObject<LockedHopperBlock>> hopper : StorageBlocks.HOPPERS.entrySet()) {
			TagKey<Item> mat = hopper.getKey().getMaterial();
			Block matchChest = StorageBlocks.CHESTS.get(hopper.getKey()).get();

			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', matchChest).define('M', Tags.Items.INGOTS_IRON).pattern("M M").pattern("MCM").pattern(" M ").unlockedBy("has_chest", has(matchChest)).unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))::save).generateAdvancement().build(consumer, new ResourceLocation(hopper.getValue().getId() + "_chest"));

			switch (hopper.getKey().getStorageLevel()) {
				case 1:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_0)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							hopper.getValue().getId());
					break;
				case 2:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_1)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							hopper.getValue().getId());
					break;
				case 3:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_2)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							hopper.getValue().getId());
					break;
				case 4:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_3)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							hopper.getValue().getId());
					break;
				case 5:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', StorageTags.Items.HOPPERS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(StorageTags.Items.HOPPERS_LEVEL_4)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							hopper.getValue().getId());
					break;
				default:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.HOPPERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(hopper.getValue().get(), 1).define('C', Items.HOPPER).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_hopper", has(Items.HOPPER)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, hopper.getValue().getId());
					break;
			}
		}

		for (Entry<StorageMaterial, RegistryObject<LockedBarrelBlock>> barrel : StorageBlocks.BARRELS.entrySet()) {
			TagKey<Item> mat = barrel.getKey().getMaterial();

			switch (barrel.getKey().getStorageLevel()) {
				case 1:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BARRELS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_0)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							barrel.getValue().getId());
					break;
				case 2:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BARRELS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_1)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							barrel.getValue().getId());
					break;
				case 3:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BARRELS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_2)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							barrel.getValue().getId());
					break;
				case 4:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BARRELS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_3)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							barrel.getValue().getId());
					break;
				case 5:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BARRELS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', StorageTags.Items.BARRELS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(StorageTags.Items.BARRELS_LEVEL_4)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							barrel.getValue().getId());
					break;
				default:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BARRELS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(barrel.getValue().get(), 1).define('C', Tags.Items.BARRELS_WOODEN).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_barrel", has(Tags.Items.BARRELS_WOODEN)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, barrel.getValue().getId());
					break;
			}
		}

		for (Entry<StorageMaterial, RegistryObject<LockedShulkerBoxBlock>> shulker : StorageBlocks.SHULKERS.entrySet()) {
			TagKey<Item> mat = shulker.getKey().getMaterial();

			switch (shulker.getKey().getStorageLevel()) {
				case 1:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.SHULKERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_0)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							shulker.getValue().getId());
					break;
				case 2:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.SHULKERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_1)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							shulker.getValue().getId());
					break;
				case 3:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.SHULKERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_2)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							shulker.getValue().getId());
					break;
				case 4:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.SHULKERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_3)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							shulker.getValue().getId());
					break;
				case 5:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.SHULKERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_LEVEL_4)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							shulker.getValue().getId());
					break;
				default:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.SHULKERS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(shulker.getValue().get(), 1).define('C', StorageTags.Items.SHULKERS_NORMAL).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_shulker", has(StorageTags.Items.SHULKERS_NORMAL)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer,
							shulker.getValue().getId());
					break;
			}
		}

		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addRecipe(LockedUpgradingRecipeBuilder.shaped(StorageItems.BAG.get(), 1).define('S', Tags.Items.STRING).define('C', Tags.Items.CHESTS_WOODEN).define('L', Tags.Items.LEATHER).pattern("SLS").pattern("LCL").pattern("LLL").unlockedBy("has_leather", has(Tags.Items.LEATHER)).unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))::save).generateAdvancement().build(consumer, StorageItems.BAG.getId());

		for (Entry<StorageMaterial, RegistryObject<BagItem>> bag : StorageItems.BAGS.entrySet()) {
			TagKey<Item> mat = bag.getKey().getMaterial();
			Block matchChest = StorageBlocks.CHESTS.get(bag.getKey()).get();

			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', matchChest).define('S', Tags.Items.STRING).define('L', Tags.Items.LEATHER).pattern("SLS").pattern("LCL").pattern("LLL").unlockedBy("has_chest", has(matchChest)).unlockedBy("has_leather", has(Tags.Items.LEATHER))::save).generateAdvancement().build(consumer,
					new ResourceLocation(bag.getValue().getId() + "_chest"));

			switch (bag.getKey().getStorageLevel()) {
				case 1:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_0).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_0)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, bag.getValue().getId());
					break;
				case 2:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_1).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_1)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, bag.getValue().getId());
					break;
				case 3:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_2).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_2)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, bag.getValue().getId());
					break;
				case 4:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_3).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_3)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, bag.getValue().getId());
					break;
				case 5:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageTags.Items.BAGS_LEVEL_4).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageTags.Items.BAGS_LEVEL_4)).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, bag.getValue().getId());
					break;
				default:
					ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.BAGS_CONDITION)).addCondition(not(tagEmpty(mat))).addRecipe(LockedUpgradingRecipeBuilder.shaped(bag.getValue().get(), 1).define('C', StorageItems.BAG.get()).define('M', mat).pattern("MMM").pattern("MCM").pattern("MMM").unlockedBy("has_bag", has(StorageItems.BAG.get())).unlockedBy("has_material", has(mat))::save).generateAdvancement().build(consumer, bag.getValue().getId());
					break;
			}
		}

		for (Entry<StorageMaterial, RegistryObject<LevelUpgradeItem>> levelUpgrade : StorageItems.LEVEL_UPGRADES.entrySet()) {
			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addCondition(not(tagEmpty(levelUpgrade.getKey().getMaterial())))
					.addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, levelUpgrade.getValue().get(), 1).define('M', levelUpgrade.getKey().getMaterial()).define('B', StorageItems.BLANK_UPGRADE.get()).pattern("MMM").pattern("MBM").pattern("MMM").unlockedBy("has_upgrade", has(StorageItems.BLANK_UPGRADE.get())).unlockedBy("has_material", has(levelUpgrade.getKey().getMaterial()))::save).generateAdvancement().build(consumer, levelUpgrade.getValue().getId());
		}
		
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_CONTROLLER.get(), 1).define('R', Items.COMPARATOR).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('E', Tags.Items.ENDER_PEARLS).pattern("DDD").pattern("CRC").pattern("DED").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_ender_pearls", has(Tags.Items.ENDER_PEARLS))::save).generateAdvancement().build(consumer, StorageBlocks.CRATE_CONTROLLER.getId());
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_BRIDGE.get(), 1).define('R', Items.REPEATER).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('I', Tags.Items.INGOTS_GOLD).pattern("DDD").pattern("RCR").pattern("DID").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))::save).generateAdvancement().build(consumer, new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId()+"_gold"));
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_BRIDGE.get(), 1).define('R', Items.REPEATER).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('I', Tags.Items.INGOTS_COPPER).pattern("DDD").pattern("RCR").pattern("DID").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER))::save).generateAdvancement().build(consumer, new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId()+"_copper"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_BRONZE))).addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_BRIDGE.get(), 1).define('R', Items.REPEATER).define('C', StorageTags.Items.CRATES).define('D', StorageTags.Items.DEEPSLATE).define('I', StorageTags.Items.INGOTS_BRONZE).pattern("DDD").pattern("RCR").pattern("DID").unlockedBy("has_crates", has(StorageTags.Items.CRATES)).unlockedBy("has_bronze", has(StorageTags.Items.INGOTS_BRONZE))::save).generateAdvancement().build(consumer, new ResourceLocation(StorageBlocks.CRATE_BRIDGE.getId()+"_bronze"));
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_COMPACTING.get(), 1).define('P', StorageTags.Items.PISTONS).define('C', StorageTags.Items.CRATES_TRIPLE).define('D', StorageTags.Items.DEEPSLATE).define('I', Tags.Items.INGOTS_IRON).pattern("DDD").pattern("PCP").pattern("DID").unlockedBy("has_triple_crate", has(StorageTags.Items.CRATES_TRIPLE)).unlockedBy("has_pistons", has(StorageTags.Items.PISTONS))::save).generateAdvancement().build(consumer, new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId()+"_iron"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_ALUMINUM))).addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_COMPACTING.get(), 1).define('P', StorageTags.Items.PISTONS).define('C', StorageTags.Items.CRATES_TRIPLE).define('D', StorageTags.Items.DEEPSLATE).define('I', StorageTags.Items.INGOTS_ALUMINUM).pattern("DDD").pattern("PCP").pattern("DID").unlockedBy("has_triple_crate", has(StorageTags.Items.CRATES_TRIPLE)).unlockedBy("has_pistons", has(StorageTags.Items.PISTONS))::save).generateAdvancement().build(consumer, new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId()+"_aluminum"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_STEEL))).addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageBlocks.CRATE_COMPACTING.get(), 1).define('P', StorageTags.Items.PISTONS).define('C', StorageTags.Items.CRATES_TRIPLE).define('D', StorageTags.Items.DEEPSLATE).define('I', StorageTags.Items.INGOTS_STEEL).pattern("DDD").pattern("PCP").pattern("DID").unlockedBy("has_triple_crate", has(StorageTags.Items.CRATES_TRIPLE)).unlockedBy("has_pistons", has(StorageTags.Items.PISTONS))::save).generateAdvancement().build(consumer, new ResourceLocation(StorageBlocks.CRATE_COMPACTING.getId()+"_steel"));
		
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addRecipe(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, StorageItems.AMOUNT_UPGRADE.get(), 1).requires(StorageItems.BLANK_UPGRADE.get()).requires(ItemTags.SIGNS).unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get()))::save).generateAdvancement().build(consumer,StorageItems.AMOUNT_UPGRADE.getId());
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addRecipe(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, StorageItems.GLOW_UPGRADE.get(), 1).requires(StorageItems.BLANK_UPGRADE.get()).requires(Items.GLOW_INK_SAC).unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC))::save).generateAdvancement().build(consumer,StorageItems.GLOW_UPGRADE.getId());
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addRecipe(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, StorageItems.GLOW_UPGRADE.get(), 1).requires(StorageItems.BLANK_UPGRADE.get()).requires(Tags.Items.DUSTS_GLOWSTONE).unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get())).unlockedBy("has_glowstone_dust", has(Tags.Items.DUSTS_GLOWSTONE))::save).generateAdvancement().build(consumer,new ResourceLocation(StorageItems.GLOW_UPGRADE.getId()+ "_glowstone"));
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.REDSTONE_UPGRADE.get(), 2).define('U', StorageItems.BLANK_UPGRADE.get()).define('D', Tags.Items.DUSTS_REDSTONE).define('R', Items.REPEATER).define('C', Items.COMPARATOR).pattern("DCD").pattern("RUR").pattern("DCD").unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get()))::save).generateAdvancement().build(consumer,StorageItems.REDSTONE_UPGRADE.getId());
		ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addCondition(new EnabledCondition(EnabledCondition.UPGRADES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, StorageItems.VOID_UPGRADE.get(), 1).define('U', StorageItems.BLANK_UPGRADE.get()).define('O', Tags.Items.OBSIDIAN).pattern("OOO").pattern("OUO").pattern("OOO").unlockedBy("has_blank_upgrade", has(StorageItems.BLANK_UPGRADE.get()))::save).generateAdvancement().build(consumer,StorageItems.VOID_UPGRADE.getId());
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', Tags.Items.RODS_BLAZE).define('S', Tags.Items.RODS_WOODEN).define('I', Tags.Items.INGOTS_IRON).pattern("I I").pattern(" B ").pattern(" S ").unlockedBy("has_blaze_rod", has(Tags.Items.RODS_BLAZE)).save(consumer,new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_iron"));
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', Tags.Items.RODS_BLAZE).define('S', Tags.Items.RODS_WOODEN).define('I', Tags.Items.INGOTS_IRON).pattern("I I").pattern(" S ").pattern(" B ").unlockedBy("has_blaze_rod", has(Tags.Items.RODS_BLAZE)).save(consumer,new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_iron_alt"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_ALUMINUM))).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', Tags.Items.RODS_BLAZE).define('S', Tags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_ALUMINUM).pattern("I I").pattern(" B ").pattern(" S ").unlockedBy("has_blaze_rod", has(Tags.Items.RODS_BLAZE))::save).generateAdvancement().build(consumer,new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_aluminum"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_ALUMINUM))).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', Tags.Items.RODS_BLAZE).define('S', Tags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_ALUMINUM).pattern("I I").pattern(" S ").pattern(" B ").unlockedBy("has_blaze_rod", has(Tags.Items.RODS_BLAZE))::save).generateAdvancement().build(consumer,new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_aluminum_alt"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_STEEL))).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', Tags.Items.RODS_BLAZE).define('S', Tags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_STEEL).pattern("I I").pattern(" B ").pattern(" S ").unlockedBy("has_blaze_rod", has(Tags.Items.RODS_BLAZE))::save).generateAdvancement().build(consumer,new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_steel"));
		ConditionalRecipe.builder().addCondition(not(tagEmpty(StorageTags.Items.INGOTS_STEEL))).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, StorageItems.ROTATOR_MAJIG.get(), 1).define('B', Tags.Items.RODS_BLAZE).define('S', Tags.Items.RODS_WOODEN).define('I', StorageTags.Items.INGOTS_STEEL).pattern("I I").pattern(" S ").pattern(" B ").unlockedBy("has_blaze_rod", has(Tags.Items.RODS_BLAZE))::save).generateAdvancement().build(consumer,new ResourceLocation(StorageItems.ROTATOR_MAJIG.getId() + "_steel_alt"));
		
		for (CrateGroup group : StorageBlocks.CRATES) {
			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.SINGLE.get(), 1).define('L', group.getType().getLogTag()).define('C', Tags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern(" D ").pattern("LCL").pattern(" D ").unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE))::save).generateAdvancement().build(consumer, group.SINGLE.getId());
			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.DOUBLE.get(), 1).define('L', group.getType().getLogTag()).define('C', Tags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern("DCD").pattern("L L").pattern("DCD").unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE))::save).generateAdvancement().build(consumer, group.DOUBLE.getId());
			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.TRIPLE.get(), 1).define('L', group.getType().getLogTag()).define('C', Tags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern(" D ").pattern("LCL").pattern("CDC").unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE))::save).generateAdvancement().build(consumer, group.TRIPLE.getId());
			ConditionalRecipe.builder().addCondition(new EnabledCondition(EnabledCondition.CRATES_CONDITION)).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, group.QUADRUPLE.get(), 1).define('L', group.getType().getLogTag()).define('C', Tags.Items.CHESTS_WOODEN).define('D', StorageTags.Items.DEEPSLATE).pattern("CDC").pattern("L L").pattern("CDC").unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN)).unlockedBy("has_deepslate", has(StorageTags.Items.DEEPSLATE))::save).generateAdvancement().build(consumer, group.QUADRUPLE.getId());
		}
	}
}
