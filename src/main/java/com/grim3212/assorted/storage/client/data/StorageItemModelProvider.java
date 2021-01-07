package com.grim3212.assorted.storage.client.data;

import java.util.ArrayList;
import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class StorageItemModelProvider extends ItemModelProvider {

	private final List<Item> items;

	public StorageItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, AssortedStorage.MODID, existingFileHelper);

		this.items = new ArrayList<>();

		items.add(StorageBlocks.WOOD_CABINET.get().asItem().asItem());
		items.add(StorageBlocks.GLASS_CABINET.get().asItem());
		items.add(StorageBlocks.GOLD_SAFE.get().asItem());
		items.add(StorageBlocks.OBSIDIAN_SAFE.get().asItem());
		items.add(StorageBlocks.LOCKER.get().asItem());
		items.add(StorageBlocks.ITEM_TOWER.get().asItem());
		items.add(StorageBlocks.OAK_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.WARPED_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get().asItem());
	}

	@Override
	public String getName() {
		return "Assorted Storage item models";
	}

	@Override
	protected void registerModels() {
		items.forEach((item) -> builtinEntity(item));

		generatedItem(StorageItems.LOCKSMITH_KEY.get());
		generatedItem(StorageItems.LOCKSMITH_LOCK.get());

		generatedItem("quartz_locked_door");
	}

	private ItemModelBuilder generatedItem(String name) {
		return withExistingParent(name, "item/generated").texture("layer0", resource("item/" + name));
	}

	private ItemModelBuilder generatedItem(Item i) {
		return generatedItem(name(i));
	}

	private static String name(Item i) {
		return Registry.ITEM.getKey(i).getPath();
	}

	private String prefix(String name) {
		return resource(name).toString();
	}

	private ResourceLocation resource(String name) {
		return new ResourceLocation(AssortedStorage.MODID, name);
	}

	private void builtinEntity(Item i) {
		String name = name(i);
		getBuilder(name).parent(new ModelFile.UncheckedModelFile("builtin/entity")).transforms().transform(ModelBuilder.Perspective.GUI).rotation(30, 45, 0).scale(0.625F).end().transform(ModelBuilder.Perspective.GROUND).translation(0, 3, 0).scale(0.25F).end().transform(ModelBuilder.Perspective.HEAD).rotation(0, 180, 0).end().transform(ModelBuilder.Perspective.FIXED).rotation(0, 180, 0).scale(0.5F).end().transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT).rotation(75, 315, 0)
				.translation(0, 2.5F, 0).scale(0.375F).end().transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT).rotation(0, 315, 0).scale(0.4F).end().end();
	}
}
