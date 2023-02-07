package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.StorageBlocks.CrateGroup;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.data.LanguageProvider;

public class StorageLanguageProvider extends LanguageProvider {

	public StorageLanguageProvider(PackOutput output) {
		super(output, AssortedStorage.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.add("itemGroup.assortedstorage", "Assorted Storage");

		this.add("assortedstorage.info.combo", "Combo: %s");
		this.add("assortedstorage.info.locked", "Locked");
		this.add("assortedstorage.info.level_upgrade_level", "Storage Level %s");
		this.add("assortedstorage.info.level_upgrade", "Can be used on:");
		this.add("assortedstorage.info.level_upgrade_shift", "§lHold Shift§r to See §bSupported Blocks");
		this.add("assortedstorage.info.amount", "Amount %s");
		this.add("assortedstorage.info.upgrade.mode", "Mode: %s");
		this.add("assortedstorage.info.upgrade_amount.mode.simple", "Simple");
		this.add("assortedstorage.info.upgrade_amount.mode.full", "Full");
		this.add("assortedstorage.info.upgrade_redstone.mode.all", "All Slots");
		this.add("assortedstorage.info.upgrade_redstone.mode.most", "Most Full Slot");
		this.add("assortedstorage.info.upgrade_redstone.mode.least", "Least Full Slot");
		this.add("assortedstorage.info.upgrade_redstone.mode.slot", "Slot %s");
		this.add("assortedstorage.info.storage_multiplier", "Storage Multiplier %s");
		this.add("assortedstorage.info.item_lock", "Toggle item lock for slot %s");
		this.add("assortedstorage.info.compact_item_lock", "Toggle item lock for Compacting Crate");

		this.add("assortedstorage.container.bag", "Bag");
		this.add("assortedstorage.container.ender_bag", "Ender Bag");
		this.add("assortedstorage.container.locked_hopper", "Locked Hopper");
		this.add("assortedstorage.container.locked_barrel", "Locked Barrel");
		this.add("assortedstorage.container.locked_shulker_box", "Locked Shulker Box");
		this.add("assortedstorage.container.locked_chest", "Locked Chest");
		this.add("assortedstorage.container.wood_cabinet", "Wood Cabinet");
		this.add("assortedstorage.container.glass_cabinet", "Glass Cabinet");
		this.add("assortedstorage.container.warehouse_crate", "Warehouse Crate");
		this.add("assortedstorage.container.gold_safe", "Gold Safe");
		this.add("assortedstorage.container.obsidian_safe", "Obsidian Safe");
		this.add("assortedstorage.container.locker", "Locker");
		this.add("assortedstorage.container.item_tower", "Item Tower");
		this.add("assortedstorage.container.item_tower.row", " - Row %s of");
		this.add("assortedstorage.container.locksmith_workbench", "Locksmith Workbench");
		this.add("assortedstorage.container.locked_ender_chest", "Ender Chest");
		this.add("assortedstorage.container.key_ring", "Key Ring");
		this.add("assortedstorage.container.storage_crate.upgrades", "Upgrades");
		this.add("assortedstorage.container.storage_crate", "Storage Crate");
		this.add("assortedstorage.container.compacting_storage_crate", "Compacting Storage Crate");

		for (StorageMaterial material : StorageMaterial.values()) {
			String materialName = material.toString();
			String capitalizedMaterialName = materialName.substring(0, 1).toUpperCase() + materialName.substring(1);

			this.add("assortedstorage.container.chest_" + materialName, capitalizedMaterialName + " Chest");
			this.add("block.assortedstorage.chest_" + materialName, capitalizedMaterialName + " Chest");

			this.add("assortedstorage.container.bag_" + materialName, capitalizedMaterialName + " Bag");
			this.add("item.assortedstorage.bag_" + materialName, capitalizedMaterialName + " Bag");

			this.add("assortedstorage.container.hopper_" + materialName, capitalizedMaterialName + " Hopper");
			this.add("block.assortedstorage.hopper_" + materialName, capitalizedMaterialName + " Hopper");

			this.add("assortedstorage.container.barrel_" + materialName, capitalizedMaterialName + " Barrel");
			this.add("block.assortedstorage.barrel_" + materialName, capitalizedMaterialName + " Barrel");

			this.add("assortedstorage.container.shulker_" + materialName, capitalizedMaterialName + " Shulker Box");
			this.add("block.assortedstorage.shulker_box_" + materialName, capitalizedMaterialName + " Shulker Box");
			
			this.add("item.assortedstorage.level_upgrade_" + materialName, capitalizedMaterialName + " Level Upgrade");
		}

		this.add("block.assortedstorage.locked_hopper", "Locked Hopper");
		this.add("block.assortedstorage.locked_barrel", "Locked Hopper");
		this.add("block.assortedstorage.locked_chest", "Locked Hopper");
		this.add("block.assortedstorage.locked_shulker_box", "Locked Shulker Box");
		this.add("block.assortedstorage.locked_ender_chest", "Locked Ender Chest");
		
		this.add("block.assortedstorage.crate", "Storage Crate");
		for (CrateGroup group : StorageBlocks.CRATES) {
			String woodName = group.getType().toString();
			String capitalizedWoodName = woodName == "dark_oak" ? "Dark Oak" : woodName.substring(0, 1).toUpperCase() + woodName.substring(1);
			
			this.add("block.assortedstorage." + woodName + "_crate", capitalizedWoodName + " Storage Crate");
			this.add("block.assortedstorage." + woodName + "_crate_double", capitalizedWoodName + " Double Storage Crate");
			this.add("block.assortedstorage." + woodName + "_crate_triple", capitalizedWoodName + " Triple Storage Crate");
			this.add("block.assortedstorage." + woodName + "_crate_quadruple", capitalizedWoodName + " Quadruple Storage Crate");
		}
		
		this.add("block.assortedstorage.crate_compacting", "Compacting Storage Crate");
		this.add("block.assortedstorage.crate_controller", "Storage Crate Controller");
		this.add("block.assortedstorage.crate_bridge", "Storage Crate Bridge");

		this.add("block.assortedstorage.wood_cabinet", "Wood Cabinet");
		this.add("block.assortedstorage.glass_cabinet", "Glass Cabinet");
		this.add("block.assortedstorage.oak_warehouse_crate", "Oak Warehouse Crate");
		this.add("block.assortedstorage.birch_warehouse_crate", "Birch Warehouse Crate");
		this.add("block.assortedstorage.spruce_warehouse_crate", "Spruce Warehouse Crate");
		this.add("block.assortedstorage.acacia_warehouse_crate", "Acacia Warehouse Crate");
		this.add("block.assortedstorage.dark_oak_warehouse_crate", "Dark Oak Warehouse Crate");
		this.add("block.assortedstorage.jungle_warehouse_crate", "Jungle Warehouse Crate");
		this.add("block.assortedstorage.warped_warehouse_crate", "Warped Warehouse Crate");
		this.add("block.assortedstorage.crimson_warehouse_crate", "Crimson Warehouse Crate");
		this.add("block.assortedstorage.mangrove_warehouse_crate", "Mangrove Warehouse Crate");
		this.add("block.assortedstorage.gold_safe", "§6Gold Safe");
		this.add("block.assortedstorage.obsidian_safe", "§5Obsidian Safe");
		this.add("block.assortedstorage.locker", "Locker");
		this.add("block.assortedstorage.item_tower", "Item Tower");
		this.add("block.assortedstorage.locksmith_workbench", "Locksmith Workbench");
		this.add("block.assortedstorage.locked_quartz_door", "Locked Quartz Door");
		this.add("block.assortedstorage.locked_glass_door", "Locked Glass Door");
		this.add("block.assortedstorage.locked_steel_door", "Locked Steel Door");
		this.add("block.assortedstorage.locked_chain_link_door", "Locked Chain Link Door");
		this.add("block.assortedstorage.locked_oak_door", "Locked Oak Door");
		this.add("block.assortedstorage.locked_spruce_door", "Locked Spruce Door");
		this.add("block.assortedstorage.locked_birch_door", "Locked Birch Door");
		this.add("block.assortedstorage.locked_acacia_door", "Locked Acacia Door");
		this.add("block.assortedstorage.locked_jungle_door", "Locked Jungle Door");
		this.add("block.assortedstorage.locked_dark_oak_door", "Locked Dark Oak Door");
		this.add("block.assortedstorage.locked_crimson_door", "Locked Crimson Door");
		this.add("block.assortedstorage.locked_warped_door", "Locked Warped Door");
		this.add("block.assortedstorage.locked_mangrove_door", "Locked Mangrove Door");

		this.add("item.assortedstorage.locksmith_key", "Locksmith Key");
		this.add("item.assortedstorage.locksmith_lock", "Locksmith Lock");
		this.add("item.assortedstorage.key_ring", "Key Ring");

		this.add("item.assortedstorage.ender_bag", "Ender Bag");
		this.add("item.assortedstorage.bag", "Bag");
		this.add("item.assortedstorage.blank_upgrade", "Blank Upgrade");
		this.add("item.assortedstorage.void_upgrade", "Void Upgrade");
		this.add("item.assortedstorage.amount_upgrade", "Amount Upgrade");
		this.add("item.assortedstorage.redstone_upgrade", "Redstone Upgrade");
		this.add("item.assortedstorage.glow_upgrade", "Glow Upgrade");
		this.add("item.assortedstorage.rotator_majig", "Rotator Majig");


		for (DyeColor color : DyeColor.values()) {
			String colorName = color.getName();
			String colorPrintName = this.dyeColorToName(color);
			
			this.add("item.assortedstorage.bag_" + colorName, colorPrintName + " Bag");
			this.add("block.assortedstorage.locked_shulker_box_"  + colorName, colorPrintName + " Locked Shulker Box");

			for (StorageMaterial material : StorageMaterial.values()) {
				String materialName = material.toString();
				String capitalizedMaterialName = materialName.substring(0, 1).toUpperCase() + materialName.substring(1);
				
				this.add("item.assortedstorage.bag_" + materialName + "_" + colorName, colorPrintName + " " + capitalizedMaterialName + " Bag");
				this.add("block.assortedstorage.shulker_box_" + materialName + "_" + colorName, colorPrintName + " " + capitalizedMaterialName + " Shulker Box");
			}
		}

	}

	private String dyeColorToName(DyeColor color) {
		switch (color) {
			case BLACK:
				return "Black";
			case BLUE:
				return "Blue";
			case BROWN:
				return "Brown";
			case CYAN:
				return "Cyan";
			case GRAY:
				return "Gray";
			case GREEN:
				return "Green";
			case LIGHT_BLUE:
				return "Light Blue";
			case LIGHT_GRAY:
				return "Light Gray";
			case LIME:
				return "Lime";
			case MAGENTA:
				return "Magenta";
			case ORANGE:
				return "Orange";
			case PINK:
				return "Pink";
			case PURPLE:
				return "Purple";
			case RED:
				return "Red";
			case WHITE:
				return "White";
			case YELLOW:
				return "Yellow";
			default:
				return "unknown";
		}
	}
}
