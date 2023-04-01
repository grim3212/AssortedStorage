package com.grim3212.assorted.storage;

import com.grim3212.assorted.lib.data.FabricBlockTagProvider;
import com.grim3212.assorted.lib.data.FabricItemTagProvider;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.data.StorageBlockLoot;
import com.grim3212.assorted.storage.data.StorageBlockTagProvider;
import com.grim3212.assorted.storage.data.StorageItemTagProvider;
import com.grim3212.assorted.storage.data.StorageRecipes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AssortedStorageFabricDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider((output, registriesFuture) -> new StorageRecipes(output));
        FabricBlockTagProvider provider = pack.addProvider((output, registriesFuture) -> new FabricBlockTagProvider(output, registriesFuture, new StorageBlockTagProvider(output, registriesFuture)));
        pack.addProvider((output, registriesFuture) -> new FabricItemTagProvider(output, registriesFuture, provider, new StorageItemTagProvider(output, registriesFuture, provider)));
        pack.addProvider((output, registriesFuture) -> new LootTableProvider(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(() -> new StorageBlockLoot(() -> StorageBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList())), LootContextParamSets.BLOCK))));
    }
}
