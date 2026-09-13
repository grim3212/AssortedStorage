package com.grim3212.assorted.storage.data;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.concurrent.CompletableFuture;

/**
 * Tells NeoForge how the locked copper doors scrape back with an axe and wax with a honeycomb, the
 * same pairs Fabric is given in {@code AssortedStorageFabric}, so a change here needs the same
 * change there. Oxidising over time is {@code LockedCopperDoorBlock}'s own random tick and does not
 * read these maps; scraping and waxing are vanilla's item code, which does.
 */
public class StorageDataMapProvider extends DataMapProvider {

    public StorageDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        Builder<Oxidizable, Block> oxidizables = builder(NeoForgeDataMaps.OXIDIZABLES);
        Blocks.COPPER_DOOR.weathering().progressMapping((from, to) -> oxidizables.add(key(locked(from)), new Oxidizable(locked(to)), false));

        Builder<Waxable, Block> waxables = builder(NeoForgeDataMaps.WAXABLES);
        Blocks.COPPER_DOOR.zipUnwaxedWaxed((unwaxed, waxed) -> waxables.add(key(locked(unwaxed)), new Waxable(locked(waxed)), false));
    }

    private static Block locked(Block vanillaDoor) {
        return StorageBlocks.VANILLA_DOORS.get(vanillaDoor).get();
    }

    private static ResourceKey<Block> key(Block block) {
        return ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block));
    }
}
