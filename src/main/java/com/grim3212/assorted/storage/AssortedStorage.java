package com.grim3212.assorted.storage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.storage.client.data.LockedModelProvider;
import com.grim3212.assorted.storage.client.data.StorageBlockstateProvider;
import com.grim3212.assorted.storage.client.data.StorageItemModelProvider;
import com.grim3212.assorted.storage.client.data.StorageSpriteSourceProvider;
import com.grim3212.assorted.storage.client.proxy.ClientProxy;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.crafting.StorageRecipeSerializers;
import com.grim3212.assorted.storage.common.creative.StorageCreativeTab;
import com.grim3212.assorted.storage.common.data.StorageBlockTagProvider;
import com.grim3212.assorted.storage.common.data.StorageItemTagProvider;
import com.grim3212.assorted.storage.common.data.StorageLootProvider;
import com.grim3212.assorted.storage.common.data.StorageLootProvider.BlockTables;
import com.grim3212.assorted.storage.common.data.StorageRecipes;
import com.grim3212.assorted.storage.common.handler.EnabledCondition;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.loot.StorageLootConditions;
import com.grim3212.assorted.storage.common.loot.StorageLootEntries;
import com.grim3212.assorted.storage.common.network.PacketHandler;
import com.grim3212.assorted.storage.common.proxy.IProxy;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AssortedStorage.MODID)
public class AssortedStorage {
	public static final String MODID = "assortedstorage";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static IProxy proxy = new IProxy() {
	};

	public AssortedStorage() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		proxy.starting();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::gatherData);
		modBus.addListener(StorageCreativeTab::registerTabs);

		StorageBlocks.BLOCKS.register(modBus);
		StorageBlocks.ITEMS.register(modBus);
		StorageBlockEntityTypes.BLOCK_ENTITIES.register(modBus);
		StorageContainerTypes.CONTAINERS.register(modBus);
		StorageRecipeSerializers.RECIPES.register(modBus);
		StorageLootConditions.LOOT_ITEM_CONDITIONS.register(modBus);
		StorageLootEntries.LOOT_POOL_ENTRY_TYPES.register(modBus);

		ModLoadingContext.get().registerConfig(Type.CLIENT, StorageConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(Type.COMMON, StorageConfig.COMMON_SPEC);

		CraftingHelper.register(EnabledCondition.Serializer.INSTANCE);
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();

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
	}
}
