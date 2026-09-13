package com.grim3212.assorted.storage;

import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.inventory.FabricPlatformInventoryStorageHandlerUnsided;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class AssortedStorageFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StorageCommonMod.init();

        StorageBlocks.initDispenserHandlers();
        registerLockedCopperDoorOxidation();

        ItemStorage.SIDED.registerForBlockEntities((be, direction) ->
                {
                    if (be instanceof IInventoryBlockEntity inv)
                        return ((FabricPlatformInventoryStorageHandlerUnsided) inv.getStorageHandler()).getFabricInventory();
                    return null;
                },
                StorageBlockEntityTypes.WOOD_CABINET.get(),
                StorageBlockEntityTypes.GLASS_CABINET.get(),
                StorageBlockEntityTypes.GOLD_SAFE.get(),
                StorageBlockEntityTypes.OBSIDIAN_SAFE.get(),
                StorageBlockEntityTypes.LOCKER.get(),
                StorageBlockEntityTypes.ITEM_TOWER.get(),
                StorageBlockEntityTypes.WAREHOUSE_CRATE.get(),
                StorageBlockEntityTypes.LOCKED_CHEST.get(),
                StorageBlockEntityTypes.LOCKED_ENDER_CHEST.get(),
                StorageBlockEntityTypes.LOCKED_HOPPER.get(),
                StorageBlockEntityTypes.LOCKED_BARREL.get(),
                StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(),
                StorageBlockEntityTypes.CRATE.get(),
                StorageBlockEntityTypes.CRATE_CONTROLLER.get(),
                StorageBlockEntityTypes.CRATE_COMPACTING.get()
        );
    }

    /**
     * Tells Fabric how the locked copper doors scrape back with an axe and wax with a honeycomb.
     * Oxidising over time is {@code LockedCopperDoorBlock}'s own random tick and does not read this;
     * scraping and waxing are vanilla's item code, which does. NeoForge's half is the
     * {@code neoforge:oxidizables} and {@code neoforge:waxables} data maps in
     * {@code StorageDataMapProvider}, so a change here needs the same change there.
     */
    private static void registerLockedCopperDoorOxidation() {
        Blocks.COPPER_DOOR.weathering().progressMapping((from, to) -> OxidizableBlocksRegistry.registerNextStage(locked(from), locked(to)));
        Blocks.COPPER_DOOR.zipUnwaxedWaxed((unwaxed, waxed) -> OxidizableBlocksRegistry.registerWaxable(locked(unwaxed), locked(waxed)));
    }

    private static Block locked(Block vanillaDoor) {
        return StorageBlocks.VANILLA_DOORS.get(vanillaDoor).get();
    }
}
