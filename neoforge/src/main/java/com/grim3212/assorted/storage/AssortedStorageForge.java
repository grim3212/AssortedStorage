package com.grim3212.assorted.storage;

import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.storage.client.data.*;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.data.StorageBlockLoot;
import com.grim3212.assorted.storage.data.StorageBlockTagProvider;
import com.grim3212.assorted.storage.data.StorageItemTagProvider;
import com.grim3212.assorted.storage.data.StorageRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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
public class AssortedStorageForge {

    public AssortedStorageForge() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);
        modBus.addListener(this::gatherData);

        StorageCommonMod.init();
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
        ForgeBlockTagProvider blockTagProvider = new ForgeBlockTagProvider(packOutput, lookupProvider, fileHelper, Constants.MOD_ID, new StorageBlockTagProvider(packOutput, lookupProvider));
        datagenerator.addProvider(event.includeServer(), blockTagProvider);
        datagenerator.addProvider(event.includeServer(), new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), fileHelper, Constants.MOD_ID, new StorageItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        datagenerator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(StorageBlockLoot::new, LootContextParamSets.BLOCK))));

        // Client
        LockedModelProvider barrelProvider = new LockedModelProvider(packOutput, fileHelper);
        datagenerator.addProvider(event.includeClient(), new StorageBlockstateProvider(packOutput, fileHelper, barrelProvider));
        datagenerator.addProvider(event.includeClient(), barrelProvider);
        datagenerator.addProvider(event.includeClient(), new StorageItemModelProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new StorageSpriteSourceProvider(packOutput, fileHelper));
        datagenerator.addProvider(event.includeClient(), new StorageLanguageProvider(packOutput));
    }
}
