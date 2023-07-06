package com.grim3212.assorted.storage.common.handlers;

import com.grim3212.assorted.lib.core.creative.CreativeTabItems;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.StorageCommonMod;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class StorageCreativeItems {

    public static final RegistryProvider<CreativeModeTab> CREATIVE_TABS = RegistryProvider.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final IRegistryObject CREATIVE_TAB = CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MOD_ID))
            .icon(() -> new ItemStack(StorageBlocks.WOOD_CABINET.get()))
            .displayItems((props, output) -> output.acceptAll(StorageCreativeItems.getCreativeItems())).build());

    private static List<ItemStack> getCreativeItems() {
        CreativeTabItems items = new CreativeTabItems();

        items.add(StorageBlocks.LOCKSMITH_WORKBENCH.get());
        items.add(StorageItems.LOCKSMITH_KEY.get());
        items.add(StorageItems.LOCKSMITH_LOCK.get());
        items.add(StorageItems.KEY_RING.get());
        items.add(StorageItems.ROTATOR_MAJIG.get());

        if (StorageCommonMod.COMMON_CONFIG.cratesEnabled.get()) {
            items.add(StorageBlocks.CRATE_COMPACTING.get());
            items.add(StorageBlocks.CRATE_CONTROLLER.get());
            items.add(StorageBlocks.CRATE_BRIDGE.get());

            for (StorageBlocks.CrateGroup group : StorageBlocks.CRATES) {
                items.add(group.SINGLE.get());
                items.add(group.DOUBLE.get());
                items.add(group.TRIPLE.get());
                items.add(group.QUADRUPLE.get());
            }

            if (StorageCommonMod.COMMON_CONFIG.upgradesEnabled.get()) {
                items.add(StorageItems.VOID_UPGRADE.get());
                items.add(StorageItems.REDSTONE_UPGRADE.get());
                items.add(StorageItems.AMOUNT_UPGRADE.get());
                items.add(StorageItems.GLOW_UPGRADE.get());
            }
        }

        if (StorageCommonMod.COMMON_CONFIG.bagsEnabled.get()) {
            items.add(StorageItems.ENDER_BAG.get());
            items.add(StorageItems.BAG.get());

            for (DyeColor color : DyeColor.values()) {
                items.add(NBTHelper.putIntItemStack(new ItemStack(StorageItems.BAG.get()), BagItem.TAG_PRIMARY_COLOR, color.getId()));
            }
        }

        if (StorageCommonMod.COMMON_CONFIG.bagsEnabled.get()) {
            StorageItems.BAGS.forEach((mat, bag) -> {
                if (canNotCraft(mat)) {
                    return;
                }

                items.add(bag.get());
            });
        }

        if (StorageCommonMod.COMMON_CONFIG.upgradesEnabled.get()) {
            items.add(StorageItems.BLANK_UPGRADE.get());
            StorageItems.LEVEL_UPGRADES.forEach((mat, upgrade) -> {
                if (canNotCraft(mat)) {
                    return;
                }

                items.add(upgrade.get());
            });
        }

        // Blocks
        items.add(StorageBlocks.WOOD_CABINET.get());
        items.add(StorageBlocks.GLASS_CABINET.get());
        items.add(StorageBlocks.GOLD_SAFE.get());
        items.add(StorageBlocks.OBSIDIAN_SAFE.get());
        items.add(StorageBlocks.LOCKER.get());
        items.add(StorageBlocks.ITEM_TOWER.get());
        items.add(StorageBlocks.OAK_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.WARPED_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get());
        items.add(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get());

        items.add(defaultLock(StorageBlocks.LOCKED_ENDER_CHEST.get()));

        if (StorageCommonMod.COMMON_CONFIG.chestsEnabled.get()) {
            items.add(defaultLock(StorageBlocks.LOCKED_CHEST.get()));
        }

        if (StorageCommonMod.COMMON_CONFIG.shulkersEnabled.get()) {
            items.add(defaultLock(StorageBlocks.LOCKED_SHULKER_BOX.get()));
        }

        if (StorageCommonMod.COMMON_CONFIG.barrelsEnabled.get()) {
            items.add(defaultLock(StorageBlocks.LOCKED_BARREL.get()));
        }

        if (StorageCommonMod.COMMON_CONFIG.hoppersEnabled.get()) {
            items.add(defaultLock(StorageBlocks.LOCKED_HOPPER.get()));
        }

        if (StorageCommonMod.COMMON_CONFIG.chestsEnabled.get()) {
            StorageBlocks.CHESTS.forEach((mat, chest) -> {

                if (canNotCraft(mat)) {
                    return;
                }

                items.add(chest.get());
            });
        }

        if (StorageCommonMod.COMMON_CONFIG.shulkersEnabled.get()) {
            StorageBlocks.SHULKERS.forEach((mat, shulker) -> {

                if (canNotCraft(mat)) {
                    return;
                }

                items.add(shulker.get());
            });
        }

        if (StorageCommonMod.COMMON_CONFIG.barrelsEnabled.get()) {
            StorageBlocks.BARRELS.forEach((mat, barrel) -> {

                if (canNotCraft(mat)) {
                    return;
                }

                items.add(barrel.get());
            });
        }

        if (StorageCommonMod.COMMON_CONFIG.hoppersEnabled.get()) {
            StorageBlocks.HOPPERS.forEach((mat, hopper) -> {

                if (canNotCraft(mat)) {
                    return;
                }

                items.add(hopper.get());
            });
        }

        return items.getItems();
    }

    private static ItemStack defaultLock(ItemLike item) {
        return StorageUtil.setCodeOnStack("default", new ItemStack(item));
    }

    private static boolean canNotCraft(StorageMaterial type) {
        return StorageCommonMod.COMMON_CONFIG.hideUncraftableItems.get() && BuiltInRegistries.ITEM.getTag(type.getMaterial()).isPresent() && BuiltInRegistries.ITEM.getTag(type.getMaterial()).get().stream().count() < 1;
    }

    public static void init() {
    }
}
