package com.grim3212.assorted.storage.common.data;

import java.util.function.Consumer;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class StorageRecipes extends RecipeProvider {

	public StorageRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.WOOD_CABINET.get()).key('X', ItemTags.PLANKS).key('C', Tags.Items.CHESTS_WOODEN).patternLine(" X ").patternLine("XCX").patternLine(" X ").addCriterion("has_planks", hasItem(ItemTags.PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.GLASS_CABINET.get()).key('X', ItemTags.PLANKS).key('G', Tags.Items.GLASS).key('C', Tags.Items.CHESTS_WOODEN).patternLine(" X ").patternLine("GCG").patternLine(" X ").addCriterion("has_glass", hasItem(Tags.Items.GLASS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.GOLD_SAFE.get()).key('G', Tags.Items.INGOTS_GOLD).key('C', StorageBlocks.OBSIDIAN_SAFE.get()).patternLine(" G ").patternLine("GCG").patternLine(" G ").addCriterion("has_obsidian_chest", hasItem(StorageBlocks.OBSIDIAN_SAFE.get())).addCriterion("has_gold", hasItem(Tags.Items.INGOTS_GOLD)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.OBSIDIAN_SAFE.get()).key('G', Tags.Items.CHESTS_WOODEN).key('X', Tags.Items.OBSIDIAN).patternLine(" X ").patternLine("XGX").patternLine(" X ").addCriterion("has_obsidian", hasItem(Tags.Items.OBSIDIAN)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.LOCKER.get()).key('X', Tags.Items.INGOTS_IRON).key('C', Tags.Items.CHESTS_WOODEN).patternLine(" X ").patternLine("XCX").patternLine(" X ").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.ITEM_TOWER.get(), 2).key('I', Tags.Items.INGOTS_IRON).key('C', Tags.Items.CHESTS_WOODEN).patternLine("I I").patternLine("ICI").patternLine("I I").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.OAK_WAREHOUSE_CRATE.get()).key('P', Items.OAK_PLANKS).key('L', ItemTags.OAK_LOGS).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_oak_planks", hasItem(Items.OAK_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get()).key('P', Items.BIRCH_PLANKS).key('L', ItemTags.BIRCH_LOGS).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_birch_planks", hasItem(Items.BIRCH_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get()).key('P', Items.SPRUCE_PLANKS).key('L', ItemTags.SPRUCE_LOGS).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_spruce_planks", hasItem(Items.SPRUCE_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get()).key('P', Items.ACACIA_PLANKS).key('L', ItemTags.ACACIA_LOGS).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_acacia_planks", hasItem(Items.ACACIA_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get()).key('P', Items.DARK_OAK_PLANKS).key('L', ItemTags.DARK_OAK_LOGS).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_dark_oak_planks", hasItem(Items.DARK_OAK_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get()).key('P', Items.JUNGLE_PLANKS).key('L', ItemTags.JUNGLE_LOGS).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_jungle_planks", hasItem(Items.JUNGLE_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.WARPED_WAREHOUSE_CRATE.get()).key('P', Items.WARPED_PLANKS).key('L', Items.WARPED_STEM).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_warped_planks", hasItem(Items.WARPED_PLANKS)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get()).key('P', Items.CRIMSON_PLANKS).key('L', Items.CRIMSON_STEM).patternLine("LLL").patternLine("P P").patternLine("PPP").addCriterion("has_crimson_planks", hasItem(Items.CRIMSON_PLANKS)).build(consumer);

		ShapedRecipeBuilder.shapedRecipe(StorageItems.LOCKSMITH_LOCK.get(), 3).key('X', Tags.Items.INGOTS_IRON).patternLine(" X ").patternLine("X X").patternLine("XXX").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageItems.LOCKSMITH_KEY.get(), 3).key('X', Tags.Items.INGOTS_IRON).patternLine("XX").patternLine("XX").patternLine("X ").addCriterion("has_iron", hasItem(Tags.Items.INGOTS_IRON)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).key('L', StorageItems.LOCKSMITH_LOCK.get()).key('K', StorageItems.LOCKSMITH_KEY.get()).key('W', Blocks.CRAFTING_TABLE).patternLine("L").patternLine("K").patternLine("W").addCriterion("has_lock", hasItem(StorageItems.LOCKSMITH_LOCK.get())).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).key('L', StorageItems.LOCKSMITH_LOCK.get()).key('K', StorageItems.LOCKSMITH_KEY.get()).key('W', Blocks.CRAFTING_TABLE).patternLine("K").patternLine("L").patternLine("W").addCriterion("has_lock", hasItem(StorageItems.LOCKSMITH_LOCK.get())).build(consumer, new ResourceLocation(AssortedStorage.MODID, "locksmith_workbench_alt"));

		ShapedRecipeBuilder.shapedRecipe(StorageBlocks.QUARTZ_DOOR.get(), 3).key('X', Items.QUARTZ).patternLine("XX").patternLine("XX").patternLine("XX").addCriterion("has_quartz", hasItem(Items.QUARTZ)).build(consumer);
	}

	@Override
	public String getName() {
		return "Assorted Storage recipes";
	}
}
