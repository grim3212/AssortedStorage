package com.grim3212.assorted.storage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.grim3212.assorted.storage.client.data.StorageBlockstateProvider;
import com.grim3212.assorted.storage.client.data.StorageItemModelProvider;
import com.grim3212.assorted.storage.client.screen.DualLockerScreen;
import com.grim3212.assorted.storage.client.screen.GoldSafeScreen;
import com.grim3212.assorted.storage.client.screen.ItemTowerScreen;
import com.grim3212.assorted.storage.client.screen.LockerScreen;
import com.grim3212.assorted.storage.client.screen.LocksmithWorkbenchScreen;
import com.grim3212.assorted.storage.client.screen.StorageScreen;
import com.grim3212.assorted.storage.client.tileentity.GlassCabinetTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.GoldSafeTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.ItemTowerTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.LockerTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.ObsidianSafeTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.WarehouseCrateTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.WoodCabinetTileEntityRenderer;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.tileentity.StorageTileEntityTypes;
import com.grim3212.assorted.storage.common.data.StorageLootProvider;
import com.grim3212.assorted.storage.common.data.StorageRecipes;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.network.PacketHandler;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AssortedStorage.MODID)
public class AssortedStorage {
	public static final String MODID = "assortedstorage";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final ItemGroup ASSORTED_STORAGE_ITEM_GROUP = (new ItemGroup("assortedstorage") {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(StorageBlocks.WOOD_CABINET.get());
		}
	});

	public AssortedStorage() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::setupClient);
		modBus.addListener(this::gatherData);

		StorageBlocks.BLOCKS.register(modBus);
		StorageBlocks.ITEMS.register(modBus);
		StorageTileEntityTypes.TILE_ENTITIES.register(modBus);
		StorageContainerTypes.CONTAINERS.register(modBus);
	}

	private void setup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	private void setupClient(final FMLClientSetupEvent event) {
		ScreenManager.registerFactory(StorageContainerTypes.WOOD_CABINET.get(), StorageScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.GLASS_CABINET.get(), StorageScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.WAREHOUSE_CRATE.get(), StorageScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.GOLD_SAFE.get(), GoldSafeScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.OBSIDIAN_SAFE.get(), StorageScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.LOCKER.get(), LockerScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.DUAL_LOCKER.get(), DualLockerScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.ITEM_TOWER.get(), ItemTowerScreen::new);
		ScreenManager.registerFactory(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), LocksmithWorkbenchScreen::new);

		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.WOOD_CABINET.get(), WoodCabinetTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.GLASS_CABINET.get(), GlassCabinetTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.WAREHOUSE_CRATE.get(), WarehouseCrateTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.GOLD_SAFE.get(), GoldSafeTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.OBSIDIAN_SAFE.get(), ObsidianSafeTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.LOCKER.get(), LockerTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.ITEM_TOWER.get(), ItemTowerTileEntityRenderer::new);
	}

	private void gatherData(GatherDataEvent event) {
		DataGenerator datagenerator = event.getGenerator();

		if (event.includeServer()) {
			datagenerator.addProvider(new StorageRecipes(datagenerator));
			datagenerator.addProvider(new StorageLootProvider(datagenerator));
		}

		if (event.includeClient()) {
			datagenerator.addProvider(new StorageBlockstateProvider(datagenerator, event.getExistingFileHelper()));
			datagenerator.addProvider(new StorageItemModelProvider(datagenerator, event.getExistingFileHelper()));
		}
	}
}
