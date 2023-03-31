package com.grim3212.assorted.storage;

import com.grim3212.assorted.storage.client.data.*;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.data.StorageBlockTagProvider;
import com.grim3212.assorted.storage.common.data.StorageItemTagProvider;
import com.grim3212.assorted.storage.common.data.StorageLootProvider;
import com.grim3212.assorted.storage.common.data.StorageLootProvider.BlockTables;
import com.grim3212.assorted.storage.common.data.StorageRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedStorage {

    public AssortedStorage() {
        //TODO: Use mixins for capability inialization

        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);
        modBus.addListener(this::gatherData);

        StorageCommonMod.init();

        Block
    }

    private void setup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() -> {
            StorageBlocks.initDispenserHandlers();
        });
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator datagenerator = event.getGenerator();
        PackOutput packOutput = datagenerator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Server
        datagenerator.addProvider(event.includeServer(), new StorageRecipes(packOutput));
        StorageBlockTagProvider blockTagProvider = new StorageBlockTagProvider(packOutput, lookupProvider, fileHelper);
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new StorageItemTagProvider(packOutput, lookupProvider, blockTagProvider, fileHelper));
        datagenerator.addProvider(event.includeServer(), new StorageLootProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(BlockTables::new, LootContextParamSets.BLOCK))));

        // Client
        LockedModelProvider barrelProvider = new LockedModelProvider(packOutput, fileHelper);
        datagenerator.addProvider(event.includeClient(), new StorageBlockstateProvider(packOutput, fileHelper, barrelProvider));
        datagenerator.addProvider(event.includeClient(), barrelProvider);
        datagenerator.addProvider(event.includeClient(), new StorageItemModelProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new StorageSpriteSourceProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new StorageLanguageProvider(packOutput));
    }
}
