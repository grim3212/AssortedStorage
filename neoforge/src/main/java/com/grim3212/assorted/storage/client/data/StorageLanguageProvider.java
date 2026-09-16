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

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

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

        this.addManual();
    }

    /** The chapters in {@code assets/assortedstorage/manual} name these keys. */
    private void addManual() {
        this.add("manual.assortedstorage.title", "Assorted Storage");
        this.add("manual.assortedstorage.description",
                "Bigger containers, crates, portable storage, and locks for all of it.");

        this.addContainersChapter();
        this.addCratesChapter();
        this.addBagsChapter();
        this.addLockingChapter();
        this.addFurnitureChapter();
    }

    private void addContainersChapter() {
        this.add("manual.assortedstorage.chapter.containers", "Containers");

        this.add("manual.assortedstorage.chapter.containers.materials.title", "Materials and Levels");
        this.add("manual.assortedstorage.chapter.containers.materials",
                "Every container in this chapter comes in the same set of materials, and the material decides "
                        + "how much it holds. Stone is the smallest and netherite the largest." + BREAK
                        + "Materials are also grouped into storage levels, from 0 to 5. A level is what the upgrades later in this chapter step through." + BREAK
                        + "Everything Assorted Core adds has a place in that order too, which gives tin, "
                        + "silver, ruby and the rest somewhere useful to go.");

        this.add("manual.assortedstorage.chapter.containers.chests.title", "Chests");
        this.add("manual.assortedstorage.chapter.containers.chests",
                "A material chest works exactly like a vanilla one and holds a great deal more. They do not "
                        + "pair into doubles.");

        this.add("manual.assortedstorage.chapter.containers.barrels.title", "Barrels");
        this.add("manual.assortedstorage.chapter.containers.barrels",
                "Barrels hold the same as their chest and open without needing space above them.");

        this.add("manual.assortedstorage.chapter.containers.hoppers.title", "Hoppers");
        this.add("manual.assortedstorage.chapter.containers.hoppers",
                "A material hopper moves items the way a vanilla one does but has far more room, and can vary in speed depending on the material.");

        this.add("manual.assortedstorage.chapter.containers.shulker_boxes.title", "Shulker Boxes");
        this.add("manual.assortedstorage.chapter.containers.shulker_boxes",
                "Material shulker boxes keep their contents when broken, the same as the vanilla, and can end up being much larger.");

        this.add("manual.assortedstorage.chapter.containers.level_upgrades.title", "Level Upgrades");
        this.add("manual.assortedstorage.chapter.containers.level_upgrades",
                "A level upgrade raises a container one material at a time, in place, without unpacking it." + BREAK
                        + "Each upgrade names the material it upgrades to, and its tooltip lists what level it will become. An upgrade will not skip a level, so working "
                        + "up from stone means working through the order.");
    }

    private void addCratesChapter() {
        this.add("manual.assortedstorage.chapter.crates", "Crates");

        this.add("manual.assortedstorage.chapter.crates.crates.title", "Crates");
        this.add("manual.assortedstorage.chapter.crates.crates",
                "A crate holds one kind of item and a lot of it. By default 32 stacks." + BREAK
                        + "There is a crate for every wood, and they are all the same size.");

        this.add("manual.assortedstorage.chapter.crates.sizes.title", "Compartments");
        this.add("manual.assortedstorage.chapter.crates.sizes",
                "Double, triple and quadruple crates split the same block into more compartments, each holding "
                        + "its own item." + BREAK
                        + "A double gives two compartments of sixteen stacks. A triple gives sixteen, eight and "
                        + "eight. A quadruple gives four of eight. More kinds of item, less of each.");

        this.add("manual.assortedstorage.chapter.crates.controller.title", "Crate Controller");
        this.add("manual.assortedstorage.chapter.crates.controller",
                "A controller gathers every crate it is connected to into one inventory, so a wall of crates "
                        + "can befilled from a single block." + BREAK
                        + "Crates connect through each other, so the controller only needs to touch the "
                        + "network once.");

        this.add("manual.assortedstorage.chapter.crates.bridge.title", "Crate Bridge");
        this.add("manual.assortedstorage.chapter.crates.bridge",
                "A bridge carries a crate network past a gap without storing anything itself. Use it to reach "
                        + "around a doorway, or to join two walls of crates to one controller.");

        this.add("manual.assortedstorage.chapter.crates.compacting.title", "Compacting Crate");
        this.add("manual.assortedstorage.chapter.crates.compacting",
                "A compacting crate can display the different levels of an item group. For exmaple if you place nine iron "
                        + "ingots, you will see the option of them as a iron block as well as iron nuggets.");

        this.add("manual.assortedstorage.chapter.crates.upgrades.title", "Crate Upgrades");
        this.add("manual.assortedstorage.chapter.crates.upgrades",
                "Upgrades slot into a crate and change how it behaves. The blank upgrade is the base the "
                        + "others are made from." + BREAK
                        + "Glow lights the crate to make it easier to see at night. Void throws away anything that will not fit. Amount shows how full it is, simply or exactly. "
                        + "Redstone gives out a signal, from all slots or from whichever one you pick.");
    }

    private void addBagsChapter() {
        this.add("manual.assortedstorage.chapter.bags", "Bags");

        this.add("manual.assortedstorage.chapter.bags.bags.title", "Bags");
        this.add("manual.assortedstorage.chapter.bags.bags",
                "A bag is a container you carry. Right click to open it anywhere, and it holds as much as its "
                        + "material allows." + BREAK
                        + "Bags can be dyed, you can adjust both Primary and Secondary Colors to really distinguish them.");

        this.add("manual.assortedstorage.chapter.bags.ender_bag.title", "Ender Bag");
        this.add("manual.assortedstorage.chapter.bags.ender_bag",
                "An ender bag opens your ender chest from wherever you are standing. If you lock it it will open the ender storage tied to that combination.");
    }

    private void addLockingChapter() {
        this.add("manual.assortedstorage.chapter.locking", "Locks and Keys");

        this.add("manual.assortedstorage.chapter.locking.workbench.title", "Locksmith Workbench");
        this.add("manual.assortedstorage.chapter.locking.workbench",
                "The locksmith workbench is where a lock or a key is given its combination. Two locks with the "
                        + "same combination take the same key.");

        this.add("manual.assortedstorage.chapter.locking.locks.title", "Padlocks and Keys");
        this.add("manual.assortedstorage.chapter.locking.locks",
                "Use a padlock on a chest, barrel, hopper, shulker box, ender chest or door and it becomes the "
                        + "locked version of itself, keeping whatever was inside." + BREAK
                        + "After that only a key with the matching combination opens it.");

        this.add("manual.assortedstorage.chapter.locking.key_ring.title", "Key Ring");
        this.add("manual.assortedstorage.chapter.locking.key_ring",
                "A key ring carries several keys in one slot and tries all of them.");

        this.add("manual.assortedstorage.chapter.locking.containers.title", "Locked Containers");
        this.add("manual.assortedstorage.chapter.locking.containers",
                "These are what a padlock turns a vanilla container into.");

        this.add("manual.assortedstorage.chapter.locking.doors.title", "Locked Doors");
        this.add("manual.assortedstorage.chapter.locking.doors",
                "Every vanilla door has a locked form, and so do the doors Assorted Decor adds. There is no "
                        + "item to craft. Simply put a padlock on a door that is already placed and it becomes one." + BREAK
                        + "A locked door will not open to a hand, a key of the wrong combination, or redstone.");
    }

    private void addFurnitureChapter() {
        this.add("manual.assortedstorage.chapter.furniture", "Other Storage");

        this.add("manual.assortedstorage.chapter.furniture.locker.title", "Locker");
        this.add("manual.assortedstorage.chapter.furniture.locker",
                "A locker is two blocks tall and opens as one container.");

        this.add("manual.assortedstorage.chapter.furniture.cabinets.title", "Cabinets");
        this.add("manual.assortedstorage.chapter.furniture.cabinets",
                "Cabinets are a visually pleasing addition to any room.");

        this.add("manual.assortedstorage.chapter.furniture.safes.title", "Safes");
        this.add("manual.assortedstorage.chapter.furniture.safes",
                "The gold safe works just like Shulker boxes where when you break it, it will keeps its inventory." + BREAK + "The obsidian safe is resitant to explosions");

        this.add("manual.assortedstorage.chapter.furniture.item_tower.title", "Item Tower");
        this.add("manual.assortedstorage.chapter.furniture.item_tower",
                "Item towers stack on top of each other and share one inventory, two rows per block. Build it as "
                        + "tall as the room allows and it is still opened from any part of it.");

        this.add("manual.assortedstorage.chapter.furniture.warehouse.title", "Warehouse Crates");
        this.add("manual.assortedstorage.chapter.furniture.warehouse",
                "A warehouse crate is not a crate from the other chapter but more of a box you would see in a Warehouse.");

        this.add("manual.assortedstorage.chapter.furniture.rotator_majig.title", "Rotator Majig");
        this.add("manual.assortedstorage.chapter.furniture.rotator_majig",
                "The rotator majig turns whatever you right click into its next facing. Handy for pointing a "
                        + "crate, a hopper or anything else the right way round without breaking it first.");
    }
}
