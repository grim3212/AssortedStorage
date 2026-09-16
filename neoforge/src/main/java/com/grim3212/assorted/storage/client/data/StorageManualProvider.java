package com.grim3212.assorted.storage.client.data;

import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.crafting.StorageConditions;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This mod's section of the instruction manual. The material and wood families are read from the
 * same maps the blocks are registered from, so a new material lands on its page on its own.
 */
public class StorageManualProvider extends LibManualProvider {

    public StorageManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addChapters() {
        this.section(20, StorageBlocks.CRATES.getFirst().SINGLE.get());

        this.addContainers();
        this.addCrates();
        this.addBags();
        this.addLocking();
        this.addFurniture();
    }

    private void addContainers() {
        ChapterBuilder containers = this.chapter("containers");

        containers.text("materials");
        containers.recipes("chests", blocks(StorageBlocks.CHESTS)).whenPartEnabled(StorageConditions.Parts.CHESTS).opens(blocks(StorageBlocks.CHESTS));
        containers.recipes("barrels", blocks(StorageBlocks.BARRELS)).whenPartEnabled(StorageConditions.Parts.BARRELS).opens(blocks(StorageBlocks.BARRELS));
        containers.recipes("hoppers", blocks(StorageBlocks.HOPPERS)).whenPartEnabled(StorageConditions.Parts.HOPPERS).opens(blocks(StorageBlocks.HOPPERS));
        containers.recipes("shulker_boxes", blocks(StorageBlocks.SHULKERS)).whenPartEnabled(StorageConditions.Parts.SHULKERS).opens(blocks(StorageBlocks.SHULKERS));
        containers.recipes("level_upgrades", items(StorageItems.LEVEL_UPGRADES)).whenPartEnabled(StorageConditions.Parts.UPGRADES)
                .opens(items(StorageItems.LEVEL_UPGRADES));
    }

    private void addCrates() {
        Block[] singles = StorageBlocks.CRATES.stream().map(group -> group.SINGLE.get()).toArray(Block[]::new);
        List<Block> bigger = new ArrayList<>();
        for (StorageBlocks.CrateGroup group : StorageBlocks.CRATES) {
            bigger.add(group.DOUBLE.get());
            bigger.add(group.TRIPLE.get());
            bigger.add(group.QUADRUPLE.get());
        }
        StorageBlocks.CrateGroup oak = StorageBlocks.CRATES.getFirst();

        ChapterBuilder crates = this.chapter("crates").whenPartEnabled(StorageConditions.Parts.CRATES);
        crates.recipes("crates", singles).opens(singles);
        crates.recipes("sizes", oak.SINGLE.get(), oak.DOUBLE.get(), oak.TRIPLE.get(), oak.QUADRUPLE.get()).whenPartEnabled(StorageConditions.Parts.CRATES).every(50)
                .opens(bigger.toArray(Block[]::new));
        crates.recipes("controller", StorageBlocks.CRATE_CONTROLLER.get()).whenPartEnabled(StorageConditions.Parts.CRATES).opens(StorageBlocks.CRATE_CONTROLLER.get());
        crates.recipesById("bridge", recipeId("crate_bridge_copper"), recipeId("crate_bridge_bronze"), recipeId("crate_bridge_gold")).whenPartEnabled(StorageConditions.Parts.CRATES, StorageConditions.Parts.UPGRADES).every(60)
                .opens(StorageBlocks.CRATE_BRIDGE.get());
        crates.recipesById("compacting", recipeId("crate_compacting_iron"), recipeId("crate_compacting_aluminum"), recipeId("crate_compacting_steel")).whenPartEnabled(StorageConditions.Parts.CRATES, StorageConditions.Parts.UPGRADES)
                .every(60).opens(StorageBlocks.CRATE_COMPACTING.get());
        crates.recipes("upgrades", StorageItems.BLANK_UPGRADE.get(), StorageItems.GLOW_UPGRADE.get(),
                        StorageItems.VOID_UPGRADE.get(), StorageItems.AMOUNT_UPGRADE.get(),
                        StorageItems.REDSTONE_UPGRADE.get()).whenPartEnabled(StorageConditions.Parts.UPGRADES).every(50)
                .opens(StorageItems.BLANK_UPGRADE.get(), StorageItems.GLOW_UPGRADE.get(),
                        StorageItems.VOID_UPGRADE.get(), StorageItems.AMOUNT_UPGRADE.get(),
                        StorageItems.REDSTONE_UPGRADE.get());
    }

    private void addBags() {
        List<Item> bags = new ArrayList<>();
        bags.add(StorageItems.BAG.get());
        bags.addAll(List.of(items(StorageItems.BAGS)));

        ChapterBuilder chapter = this.chapter("bags").whenPartEnabled(StorageConditions.Parts.BAGS);
        chapter.recipes("bags", bags.toArray(Item[]::new)).opens(bags.toArray(Item[]::new));
        chapter.recipes("ender_bag", StorageItems.ENDER_BAG.get()).opens(StorageItems.ENDER_BAG.get());
    }

    private void addLocking() {
        Block[] containers = {StorageBlocks.LOCKED_CHEST.get(), StorageBlocks.LOCKED_BARREL.get(),
                StorageBlocks.LOCKED_HOPPER.get(), StorageBlocks.LOCKED_SHULKER_BOX.get(),
                StorageBlocks.LOCKED_ENDER_CHEST.get()};

        ChapterBuilder locking = this.chapter("locking");
        locking.recipes("workbench", StorageBlocks.LOCKSMITH_WORKBENCH.get()).opens(StorageBlocks.LOCKSMITH_WORKBENCH.get());
        locking.recipes("locks", StorageItems.LOCKSMITH_LOCK.get(), StorageItems.LOCKSMITH_KEY.get()).every(50)
                .opens(StorageItems.LOCKSMITH_LOCK.get(), StorageItems.LOCKSMITH_KEY.get());
        locking.recipes("key_ring", StorageItems.KEY_RING.get()).opens(StorageItems.KEY_RING.get());
        locking.items("containers", containers).opens(containers);
        // A locked door is made by locking one that is already hung, so there is no item to draw.
        locking.text("doors").opens(lockedDoors());
    }

    private void addFurniture() {
        Block[] warehouse = blocks(StorageBlocks.WAREHOUSE_CRATES);
        ChapterBuilder furniture = this.chapter("furniture");

        furniture.recipes("locker", StorageBlocks.LOCKER.get()).opens(StorageBlocks.LOCKER.get());
        furniture.recipes("cabinets", StorageBlocks.WOOD_CABINET.get(), StorageBlocks.GLASS_CABINET.get()).every(50)
                .opens(StorageBlocks.WOOD_CABINET.get(), StorageBlocks.GLASS_CABINET.get());
        furniture.recipes("safes", StorageBlocks.GOLD_SAFE.get(), StorageBlocks.OBSIDIAN_SAFE.get()).every(50)
                .opens(StorageBlocks.GOLD_SAFE.get(), StorageBlocks.OBSIDIAN_SAFE.get());
        furniture.recipes("item_tower", StorageBlocks.ITEM_TOWER.get()).opens(StorageBlocks.ITEM_TOWER.get());
        furniture.recipes("warehouse", warehouse).opens(warehouse);
        furniture.recipesById("rotator_majig", recipeId("rotator_majig_iron"), recipeId("rotator_majig_aluminum"), recipeId("rotator_majig_steel"))
                .every(60).opens(StorageItems.ROTATOR_MAJIG.get());
    }

    /** Every locked door: the vanilla ones this mod mirrors, plus the four of its own. */
    private static Block[] lockedDoors() {
        List<Block> doors = new ArrayList<>(StorageBlocks.VANILLA_DOORS.values().stream()
                .map(IRegistryObject::get).map(Block.class::cast).toList());
        doors.add(StorageBlocks.LOCKED_IRON_DOOR.get());
        doors.add(StorageBlocks.LOCKED_QUARTZ_DOOR.get());
        doors.add(StorageBlocks.LOCKED_GLASS_DOOR.get());
        doors.add(StorageBlocks.LOCKED_STEEL_DOOR.get());
        doors.add(StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get());
        return doors.stream().distinct().toArray(Block[]::new);
    }

    private static Block[] blocks(Map<?, ? extends IRegistryObject<? extends Block>> registered) {
        return values(registered.values()).toArray(Block[]::new);
    }

    private static Item[] items(Map<StorageMaterial, ? extends IRegistryObject<? extends Item>> registered) {
        return values(registered.values()).toArray(Item[]::new);
    }

    private static <T> List<T> values(Collection<? extends IRegistryObject<? extends T>> registered) {
        return registered.stream().map(IRegistryObject::get).map(t -> (T) t).toList();
    }
}
