package com.grim3212.assorted.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.storage.client.data.StorageBlockstateProvider;
import com.grim3212.assorted.storage.client.data.StorageItemModelProvider;
import com.grim3212.assorted.storage.client.proxy.ClientProxy;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.crafting.StorageRecipeSerializers;
import com.grim3212.assorted.storage.common.data.StorageBlockTagProvider;
import com.grim3212.assorted.storage.common.data.StorageItemTagProvider;
import com.grim3212.assorted.storage.common.data.StorageLootProvider;
import com.grim3212.assorted.storage.common.data.StorageRecipes;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.network.PacketHandler;
import com.grim3212.assorted.storage.common.proxy.IProxy;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AssortedStorage.MODID)
public class AssortedStorage {
	public static final String MODID = "assortedstorage";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static IProxy proxy = new IProxy() {
	};

	public static final CreativeModeTab ASSORTED_STORAGE_ITEM_GROUP = (new CreativeModeTab("assortedstorage") {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(StorageBlocks.WOOD_CABINET.get());
		}
	});

	public AssortedStorage() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
		proxy.starting();

		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::gatherData);

		StorageBlocks.BLOCKS.register(modBus);
		StorageBlocks.ITEMS.register(modBus);
		StorageBlockEntityTypes.BLOCK_ENTITIES.register(modBus);
		StorageContainerTypes.CONTAINERS.register(modBus);
		StorageRecipeSerializers.RECIPES.register(modBus);
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();

		// Server
		datagenerator.addProvider(event.includeServer(), new StorageRecipes(datagenerator));
		StorageBlockTagProvider blockTagProvider = new StorageBlockTagProvider(datagenerator, fileHelper);
		datagenerator.addProvider(event.includeServer(), blockTagProvider);
		datagenerator.addProvider(event.includeServer(), new StorageItemTagProvider(datagenerator, blockTagProvider, fileHelper));
		datagenerator.addProvider(event.includeServer(), new StorageLootProvider(datagenerator));

		// Client
		datagenerator.addProvider(event.includeClient(), new StorageBlockstateProvider(datagenerator, fileHelper));
		datagenerator.addProvider(event.includeClient(), new StorageItemModelProvider(datagenerator, fileHelper));
	}
}
