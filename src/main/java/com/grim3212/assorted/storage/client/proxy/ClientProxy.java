package com.grim3212.assorted.storage.client.proxy;

import com.grim3212.assorted.storage.client.blockentity.GlassCabinetBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.GoldSafeBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.ItemTowerBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.LockerBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.ObsidianSafeBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.WarehouseCrateBlockEntityRenderer;
import com.grim3212.assorted.storage.client.blockentity.WoodCabinetBlockEntityRenderer;
import com.grim3212.assorted.storage.client.model.CabinetModel;
import com.grim3212.assorted.storage.client.model.DualLockerModel;
import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import com.grim3212.assorted.storage.client.model.LockerModel;
import com.grim3212.assorted.storage.client.model.SafeModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.WarehouseCrateModel;
import com.grim3212.assorted.storage.client.screen.DualLockerScreen;
import com.grim3212.assorted.storage.client.screen.GenericStorageScreen;
import com.grim3212.assorted.storage.client.screen.GoldSafeScreen;
import com.grim3212.assorted.storage.client.screen.ItemTowerScreen;
import com.grim3212.assorted.storage.client.screen.LockerScreen;
import com.grim3212.assorted.storage.client.screen.LocksmithWorkbenchScreen;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.proxy.IProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

	@Override
	public void starting() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);
		modBus.addListener(this::registerLayers);
	}

	private void registerLayers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(StorageModelLayers.CABINET, () -> CabinetModel.createBaseMeshDefinition(false));
		event.registerLayerDefinition(StorageModelLayers.GLASS_CABINET, () -> CabinetModel.createBaseMeshDefinition(true));
		event.registerLayerDefinition(StorageModelLayers.LOCKER, LockerModel::createBaseMeshDefinition);
		event.registerLayerDefinition(StorageModelLayers.DUAL_LOCKER, DualLockerModel::createBaseMeshDefinition);
		event.registerLayerDefinition(StorageModelLayers.SAFE, SafeModel::createBaseMeshDefinition);
		event.registerLayerDefinition(StorageModelLayers.WAREHOUSE_CRATE, WarehouseCrateModel::createBaseMeshDefinition);
		event.registerLayerDefinition(StorageModelLayers.ITEM_TOWER, ItemTowerModel::createBaseMeshDefinition);
	}

	private void setupClient(final FMLClientSetupEvent event) {
		MenuScreens.register(StorageContainerTypes.WOOD_CABINET.get(), GenericStorageScreen::new);
		MenuScreens.register(StorageContainerTypes.GLASS_CABINET.get(), GenericStorageScreen::new);
		MenuScreens.register(StorageContainerTypes.WAREHOUSE_CRATE.get(), GenericStorageScreen::new);
		MenuScreens.register(StorageContainerTypes.GOLD_SAFE.get(), GoldSafeScreen::new);
		MenuScreens.register(StorageContainerTypes.OBSIDIAN_SAFE.get(), GenericStorageScreen::new);
		MenuScreens.register(StorageContainerTypes.LOCKER.get(), LockerScreen::new);
		MenuScreens.register(StorageContainerTypes.DUAL_LOCKER.get(), DualLockerScreen::new);
		MenuScreens.register(StorageContainerTypes.ITEM_TOWER.get(), ItemTowerScreen::new);
		MenuScreens.register(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), LocksmithWorkbenchScreen::new);

		BlockEntityRenderers.register(StorageBlockEntityTypes.WOOD_CABINET.get(), WoodCabinetBlockEntityRenderer::new);
		BlockEntityRenderers.register(StorageBlockEntityTypes.GLASS_CABINET.get(), GlassCabinetBlockEntityRenderer::new);
		BlockEntityRenderers.register(StorageBlockEntityTypes.WAREHOUSE_CRATE.get(), WarehouseCrateBlockEntityRenderer::new);
		BlockEntityRenderers.register(StorageBlockEntityTypes.GOLD_SAFE.get(), GoldSafeBlockEntityRenderer::new);
		BlockEntityRenderers.register(StorageBlockEntityTypes.OBSIDIAN_SAFE.get(), ObsidianSafeBlockEntityRenderer::new);
		BlockEntityRenderers.register(StorageBlockEntityTypes.LOCKER.get(), LockerBlockEntityRenderer::new);
		BlockEntityRenderers.register(StorageBlockEntityTypes.ITEM_TOWER.get(), ItemTowerBlockEntityRenderer::new);

		for (Block b : StorageBlocks.lockedDoors()) {
			ItemBlockRenderTypes.setRenderLayer(b, RenderType.cutout());
		}
	}

	@Override
	public Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
