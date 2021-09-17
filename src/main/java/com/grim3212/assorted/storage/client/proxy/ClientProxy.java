package com.grim3212.assorted.storage.client.proxy;

import com.grim3212.assorted.storage.client.screen.DualLockerScreen;
import com.grim3212.assorted.storage.client.screen.GenericStorageScreen;
import com.grim3212.assorted.storage.client.screen.GoldSafeScreen;
import com.grim3212.assorted.storage.client.screen.ItemTowerScreen;
import com.grim3212.assorted.storage.client.screen.KeyRingScreen;
import com.grim3212.assorted.storage.client.screen.LockedEnderChestScreen;
import com.grim3212.assorted.storage.client.screen.LockerScreen;
import com.grim3212.assorted.storage.client.screen.LocksmithWorkbenchScreen;
import com.grim3212.assorted.storage.client.tileentity.GlassCabinetTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.GoldSafeTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.ItemTowerTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.LockedEnderChestTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.LockerTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.ObsidianSafeTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.WarehouseCrateTileEntityRenderer;
import com.grim3212.assorted.storage.client.tileentity.WoodCabinetTileEntityRenderer;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.tileentity.StorageTileEntityTypes;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.proxy.IProxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy implements IProxy {

	@Override
	public void starting() {
		final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setupClient);
	}

	public void setupClient(final FMLClientSetupEvent event) {
		ScreenManager.register(StorageContainerTypes.WOOD_CABINET.get(), GenericStorageScreen::new);
		ScreenManager.register(StorageContainerTypes.GLASS_CABINET.get(), GenericStorageScreen::new);
		ScreenManager.register(StorageContainerTypes.WAREHOUSE_CRATE.get(), GenericStorageScreen::new);
		ScreenManager.register(StorageContainerTypes.GOLD_SAFE.get(), GoldSafeScreen::new);
		ScreenManager.register(StorageContainerTypes.OBSIDIAN_SAFE.get(), GenericStorageScreen::new);
		ScreenManager.register(StorageContainerTypes.LOCKER.get(), LockerScreen::new);
		ScreenManager.register(StorageContainerTypes.DUAL_LOCKER.get(), DualLockerScreen::new);
		ScreenManager.register(StorageContainerTypes.ITEM_TOWER.get(), ItemTowerScreen::new);
		ScreenManager.register(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), LocksmithWorkbenchScreen::new);
		ScreenManager.register(StorageContainerTypes.KEY_RING.get(), KeyRingScreen::new);
		ScreenManager.register(StorageContainerTypes.LOCKED_ENDER_CHEST.get(), LockedEnderChestScreen::new);

		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.WOOD_CABINET.get(), WoodCabinetTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.GLASS_CABINET.get(), GlassCabinetTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.WAREHOUSE_CRATE.get(), WarehouseCrateTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.GOLD_SAFE.get(), GoldSafeTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.OBSIDIAN_SAFE.get(), ObsidianSafeTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.LOCKER.get(), LockerTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.ITEM_TOWER.get(), ItemTowerTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(StorageTileEntityTypes.LOCKED_ENDER_CHEST.get(), LockedEnderChestTileEntityRenderer::new);

		for (Block b : StorageBlocks.lockedDoors()) {
			RenderTypeLookup.setRenderLayer(b, RenderType.cutout());
		}
	}

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
