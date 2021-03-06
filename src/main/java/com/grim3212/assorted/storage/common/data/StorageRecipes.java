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
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(StorageBlocks.WOOD_CABINET.get()).define('X', ItemTags.PLANKS).define('C', Tags.Items.CHESTS_WOODEN).pattern(" X ").pattern("XCX").pattern(" X ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.GLASS_CABINET.get()).define('X', ItemTags.PLANKS).define('G', Tags.Items.GLASS).define('C', Tags.Items.CHESTS_WOODEN).pattern(" X ").pattern("GCG").pattern(" X ").unlockedBy("has_glass", has(Tags.Items.GLASS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.GOLD_SAFE.get()).define('G', Tags.Items.INGOTS_GOLD).define('C', StorageBlocks.OBSIDIAN_SAFE.get()).pattern(" G ").pattern("GCG").pattern(" G ").unlockedBy("has_obsidian_chest", has(StorageBlocks.OBSIDIAN_SAFE.get())).unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.OBSIDIAN_SAFE.get()).define('G', Tags.Items.CHESTS_WOODEN).define('X', Tags.Items.OBSIDIAN).pattern(" X ").pattern("XGX").pattern(" X ").unlockedBy("has_obsidian", has(Tags.Items.OBSIDIAN)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.LOCKER.get()).define('X', Tags.Items.INGOTS_IRON).define('C', Tags.Items.CHESTS_WOODEN).pattern(" X ").pattern("XCX").pattern(" X ").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.ITEM_TOWER.get(), 2).define('I', Tags.Items.INGOTS_IRON).define('C', Tags.Items.CHESTS_WOODEN).pattern("I I").pattern("ICI").pattern("I I").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.OAK_WAREHOUSE_CRATE.get()).define('P', Items.OAK_PLANKS).define('L', ItemTags.OAK_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_oak_planks", has(Items.OAK_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get()).define('P', Items.BIRCH_PLANKS).define('L', ItemTags.BIRCH_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_birch_planks", has(Items.BIRCH_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get()).define('P', Items.SPRUCE_PLANKS).define('L', ItemTags.SPRUCE_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_spruce_planks", has(Items.SPRUCE_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get()).define('P', Items.ACACIA_PLANKS).define('L', ItemTags.ACACIA_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_acacia_planks", has(Items.ACACIA_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get()).define('P', Items.DARK_OAK_PLANKS).define('L', ItemTags.DARK_OAK_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_dark_oak_planks", has(Items.DARK_OAK_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get()).define('P', Items.JUNGLE_PLANKS).define('L', ItemTags.JUNGLE_LOGS).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_jungle_planks", has(Items.JUNGLE_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.WARPED_WAREHOUSE_CRATE.get()).define('P', Items.WARPED_PLANKS).define('L', Items.WARPED_STEM).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_warped_planks", has(Items.WARPED_PLANKS)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get()).define('P', Items.CRIMSON_PLANKS).define('L', Items.CRIMSON_STEM).pattern("LLL").pattern("P P").pattern("PPP").unlockedBy("has_crimson_planks", has(Items.CRIMSON_PLANKS)).save(consumer);

		ShapedRecipeBuilder.shaped(StorageItems.LOCKSMITH_LOCK.get(), 3).define('X', Tags.Items.INGOTS_IRON).pattern(" X ").pattern("X X").pattern("XXX").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageItems.LOCKSMITH_KEY.get(), 3).define('X', Tags.Items.INGOTS_IRON).pattern("XX").pattern("XX").pattern("X ").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).define('L', StorageItems.LOCKSMITH_LOCK.get()).define('K', StorageItems.LOCKSMITH_KEY.get()).define('W', Blocks.CRAFTING_TABLE).pattern("L").pattern("K").pattern("W").unlockedBy("has_lock", has(StorageItems.LOCKSMITH_LOCK.get())).save(consumer);
		ShapedRecipeBuilder.shaped(StorageBlocks.LOCKSMITH_WORKBENCH.get(), 1).define('L', StorageItems.LOCKSMITH_LOCK.get()).define('K', StorageItems.LOCKSMITH_KEY.get()).define('W', Blocks.CRAFTING_TABLE).pattern("K").pattern("L").pattern("W").unlockedBy("has_lock", has(StorageItems.LOCKSMITH_LOCK.get())).save(consumer, new ResourceLocation(AssortedStorage.MODID, "locksmith_workbench_alt"));
	}

	@Override
	public String getName() {
		return "Assorted Storage recipes";
	}
}
