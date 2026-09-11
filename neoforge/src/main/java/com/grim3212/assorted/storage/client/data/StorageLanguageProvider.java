package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.lib.data.LibLanguageProvider;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.StorageBlocks.CrateGroup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case
 * needs no line here (see {@link LibLanguageProvider}); these are the names that read differently,
 * and every key that is not a name.
 */
public class StorageLanguageProvider extends LibLanguageProvider {

    public StorageLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assortedstorage", "Assorted Storage");

        this.add("assortedstorage.info.combo", "Combo: %s");
        this.add("assortedstorage.info.locked", "Locked");
        this.add("assortedstorage.info.level_upgrade_level", "Storage Level %s");
        this.add("assortedstorage.info.level_upgrade", "Can be used on:");
        this.add("assortedstorage.info.level_upgrade_shift", "\u00A7lHold Shift\u00A7r to See \u00A7bSupported Blocks");
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

        // The material comes first in each name, where the id puts it last.
        for (StorageMaterial material : StorageMaterial.values()) {
            String id = material.toString();
            String name = titleCase(id);

            this.add("assortedstorage.container.chest_" + id, name + " Chest");
            this.add("block.assortedstorage.chest_" + id, name + " Chest");
            this.add("assortedstorage.container.bag_" + id, name + " Bag");
            this.add("item.assortedstorage.bag_" + id, name + " Bag");
            this.add("assortedstorage.container.hopper_" + id, name + " Hopper");
            this.add("block.assortedstorage.hopper_" + id, name + " Hopper");
            this.add("assortedstorage.container.barrel_" + id, name + " Barrel");
            this.add("block.assortedstorage.barrel_" + id, name + " Barrel");
            this.add("assortedstorage.container.shulker_" + id, name + " Shulker Box");
            this.add("block.assortedstorage.shulker_box_" + id, name + " Shulker Box");
            this.add("item.assortedstorage.level_upgrade_" + id, name + " Level Upgrade");
        }

        this.add("block.assortedstorage.crate", "Storage Crate");
        for (CrateGroup group : StorageBlocks.CRATES) {
            String id = group.getType().toString();
            String name = titleCase(id);

            this.add("block.assortedstorage." + id + "_crate", name + " Storage Crate");
            this.add("block.assortedstorage." + id + "_crate_double", name + " Double Storage Crate");
            this.add("block.assortedstorage." + id + "_crate_triple", name + " Triple Storage Crate");
            this.add("block.assortedstorage." + id + "_crate_quadruple", name + " Quadruple Storage Crate");
        }
        this.add("block.assortedstorage.crate_compacting", "Compacting Storage Crate");
        this.add("block.assortedstorage.crate_controller", "Storage Crate Controller");
        this.add("block.assortedstorage.crate_bridge", "Storage Crate Bridge");

        this.add("block.assortedstorage.gold_safe", "\u00A76Gold Safe");
        this.add("block.assortedstorage.obsidian_safe", "\u00A75Obsidian Safe");

        for (DyeColor color : DyeColor.values()) {
            String id = color.getName();
            String name = titleCase(id);

            this.add("item.assortedstorage.bag_" + id, name + " Bag");
            this.add("block.assortedstorage.locked_shulker_box_" + id, name + " Locked Shulker Box");

            for (StorageMaterial material : StorageMaterial.values()) {
                String materialId = material.toString();
                this.add("item.assortedstorage.bag_" + materialId + "_" + id, name + " " + titleCase(materialId) + " Bag");
                this.add("block.assortedstorage.shulker_box_" + materialId + "_" + id, name + " " + titleCase(materialId) + " Shulker Box");
            }
        }

        // Item tag names, which recipe viewers show in place of the raw tag id.
        this.add("tag.item.assortedstorage.bags", "Bags");
        this.add("tag.item.assortedstorage.crafting_override", "Crafting Overrides");
        this.add("tag.item.assortedstorage.one_to_one_crafting_override", "One-to-One Crafting Overrides");
        this.add("tag.item.assortedstorage.crates", "Storage Crates");
        for (String size : new String[]{"single", "double", "triple", "quadruple"}) {
            this.add("tag.item.assortedstorage.crates." + size, titleCase(size) + " Storage Crates");
        }
        this.add("tag.item.assortedstorage.shulkers.normal", "Vanilla Shulker Boxes");
        this.add("tag.item.assortedstorage.upgrades", "Storage Upgrades");
        this.add("tag.item.c.deepslate", "Deepslate");
        this.add("tag.item.c.hoppers", "Hoppers");
        this.add("tag.item.c.paper", "Paper");
        this.add("tag.item.c.pistons", "Pistons");
        this.add("tag.item.c.storage.level_upgrades", "Storage Level Upgrades");
        for (int level = 0; level <= 5; level++) {
            this.add("tag.item.assortedstorage.bags.level_" + level, "Level " + level + " Bags");
            this.add("tag.item.assortedstorage.barrels.level_" + level, "Level " + level + " Barrels");
            this.add("tag.item.assortedstorage.chests.level_" + level, "Level " + level + " Chests");
            this.add("tag.item.assortedstorage.hoppers.level_" + level, "Level " + level + " Hoppers");
            this.add("tag.item.assortedstorage.shulkers.level_" + level, "Level " + level + " Shulker Boxes");
            this.add("tag.item.assortedstorage.can_upgrade.level_" + level, "Level " + level + " Upgradable Storage");
            this.add("tag.item.c.storage.level_" + level + "_upgrades", "Level " + level + " Storage Upgrades");
        }
    }
}
