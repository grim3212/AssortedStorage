package com.grim3212.assorted.storage.client.data;

import java.util.ArrayList;
import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.LockedChestBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.LevelUpgradeItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StorageItemModelProvider extends ItemModelProvider {

	private final List<Item> items;

	public StorageItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, AssortedStorage.MODID, existingFileHelper);

		this.items = new ArrayList<>();

		items.add(StorageBlocks.WOOD_CABINET.get().asItem().asItem());
		items.add(StorageBlocks.GLASS_CABINET.get().asItem());
		items.add(StorageBlocks.GOLD_SAFE.get().asItem());
		items.add(StorageBlocks.LOCKED_ENDER_CHEST.get().asItem());
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
		items.add(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get().asItem());
		items.add(StorageBlocks.LOCKED_CHEST.get().asItem());
		items.add(StorageBlocks.LOCKED_SHULKER_BOX.get().asItem());

		for (RegistryObject<LockedChestBlock> b : StorageBlocks.CHESTS.values()) {
			items.add(b.get().asItem());
		}

		for (RegistryObject<LockedShulkerBoxBlock> b : StorageBlocks.SHULKERS.values()) {
			items.add(b.get().asItem());
		}
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
		generatedItem(StorageItems.KEY_RING.get());
		generatedItem(StorageBlocks.LOCKED_HOPPER.get().asItem());
		generateBag(StorageItems.BAG.get());
		generateEnderBag(StorageItems.ENDER_BAG.get());

		for (RegistryObject<BagItem> bag : StorageItems.BAGS.values()) {
			generateMaterialBag(bag.get());
		}

		for (RegistryObject<LockedHopperBlock> b : StorageBlocks.HOPPERS.values()) {
			generatedItem(b.get().asItem());
		}

		generatedItem(StorageItems.BLANK_UPGRADE.get());
		for (RegistryObject<LevelUpgradeItem> levelUpgrade : StorageItems.LEVEL_UPGRADES.values()) {
			generatedItem(levelUpgrade.get());
		}
	}

	private static final ResourceLocation COLOR_OVERRIDE = new ResourceLocation(AssortedStorage.MODID, "color");
	private static final ResourceLocation LOCK_OVERRIDE = new ResourceLocation(AssortedStorage.MODID, "locked");

	private ItemModelBuilder generateBag(String name) {
		ModelFile coloredBag = withExistingParent(name + "_colored", "item/generated").texture("layer0", resource("item/" + name + "_colored")).texture("layer1", resource("item/" + "bag_strap"));
		ModelFile lockedBag = withExistingParent(name + "_locked", "item/generated").texture("layer0", resource("item/" + name + "_lock_cutout")).texture("layer1", resource("item/" + "bag_strap_lock_cutout")).texture("layer2", resource("item/" + "bag_lock"));
		ModelFile lockedColoredBag = withExistingParent(name + "_locked_colored", "item/generated").texture("layer0", resource("item/" + name + "_colored_lock_cutout")).texture("layer1", resource("item/" + "bag_strap_lock_cutout")).texture("layer2", resource("item/" + "bag_lock"));
		return withExistingParent(name, "item/generated").texture("layer0", resource("item/" + name)).texture("layer1", resource("item/" + "bag_strap")).override().predicate(COLOR_OVERRIDE, 1.0F).predicate(LOCK_OVERRIDE, 0.0F).model(coloredBag).end().override().predicate(COLOR_OVERRIDE, 0.0F).predicate(LOCK_OVERRIDE, 1.0F).model(lockedBag).end().override().predicate(COLOR_OVERRIDE, 1.0F).predicate(LOCK_OVERRIDE, 1.0F).model(lockedColoredBag).end();
	}

	private ItemModelBuilder generateEnderBag(Item i) {
		String name = name(i);
		ModelFile lockedBag = withExistingParent(name + "_locked", "item/generated").texture("layer0", resource("item/" + name + "_locked"));
		return withExistingParent(name, "item/generated").texture("layer0", resource("item/" + name)).override().predicate(LOCK_OVERRIDE, 1.0F).model(lockedBag).end();
	}

	private ItemModelBuilder generateMaterialBag(String name, StorageMaterial type) {
		ModelFile coloredBag = withExistingParent(name + "_material_colored", "item/generated").texture("layer0", resource("item/bag_material_colored")).texture("layer1", resource("item/" + "bag_strap")).texture("layer2", resource("item/" + name));
		ModelFile lockedBag = withExistingParent(name + "_material_locked", "item/generated").texture("layer0", resource("item/bag_material_lock_cutout")).texture("layer1", resource("item/" + "bag_strap_lock_cutout")).texture("layer2", resource("item/" + name)).texture("layer3", resource("item/" + "bag_lock"));
		ModelFile lockedColoredBag = withExistingParent(name + "_material_locked_colored", "item/generated").texture("layer0", resource("item/bag_material_colored_lock_cutout")).texture("layer1", resource("item/" + "bag_strap_lock_cutout")).texture("layer2", resource("item/" + name)).texture("layer3", resource("item/" + "bag_lock"));
		return withExistingParent(name, "item/generated").texture("layer0", resource("item/bag_material")).texture("layer1", resource("item/" + "bag_strap")).texture("layer2", resource("item/" + name)).override().predicate(COLOR_OVERRIDE, 1.0F).predicate(LOCK_OVERRIDE, 0.0F).model(coloredBag).end().override().predicate(COLOR_OVERRIDE, 0.0F).predicate(LOCK_OVERRIDE, 1.0F).model(lockedBag).end().override().predicate(COLOR_OVERRIDE, 1.0F).predicate(LOCK_OVERRIDE, 1.0F).model(lockedColoredBag)
				.end();
	}

	private ItemModelBuilder generateBag(Item i) {
		return generateBag(name(i));
	}

	private ItemModelBuilder generateMaterialBag(BagItem i) {
		return generateMaterialBag(name(i), i.getStorageMaterial());
	}

	private ItemModelBuilder generatedItem(String name) {
		return withExistingParent(name, "item/generated").texture("layer0", resource("item/" + name));
	}

	private ItemModelBuilder generatedItem(Item i) {
		return generatedItem(name(i));
	}

	private static String name(Item i) {
		return ForgeRegistries.ITEMS.getKey(i).getPath();
	}

	private ResourceLocation resource(String name) {
		return new ResourceLocation(AssortedStorage.MODID, name);
	}

	private void builtinEntity(Item i) {
		String name = name(i);
		getBuilder(name).parent(new ModelFile.UncheckedModelFile("builtin/entity")).transforms().transform(TransformType.GUI).rotation(30, 45, 0).scale(0.625F).end().transform(TransformType.GROUND).translation(0, 3, 0).scale(0.25F).end().transform(TransformType.HEAD).rotation(0, 180, 0).end().transform(TransformType.FIXED).rotation(0, 180, 0).scale(0.5F).end().transform(TransformType.THIRD_PERSON_RIGHT_HAND).rotation(75, 315, 0).translation(0, 2.5F, 0).scale(0.375F).end()
				.transform(TransformType.FIRST_PERSON_RIGHT_HAND).rotation(0, 315, 0).scale(0.4F).end().end();
	}
}
