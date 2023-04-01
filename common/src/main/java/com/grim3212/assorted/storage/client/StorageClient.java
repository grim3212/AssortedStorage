package com.grim3212.assorted.storage.client;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.client.blockentity.*;
import com.grim3212.assorted.storage.client.blockentity.item.*;
import com.grim3212.assorted.storage.client.model.*;
import com.grim3212.assorted.storage.client.model.baked.LockedModel;
import com.grim3212.assorted.storage.client.screen.*;
import com.grim3212.assorted.storage.common.block.LockedShulkerBoxBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.config.StorageClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StorageClient {

    public static final StorageClientConfig CLIENT_CONFIG = new StorageClientConfig();

    public static void init() {
        ClientServices.CLIENT.registerModelLoader(LockedModel.LOADER_NAME, LockedModel.Loader.INSTANCE);

        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.CABINET, () -> CabinetModel.createBaseMeshDefinition(false));
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.GLASS_CABINET, () -> CabinetModel.createBaseMeshDefinition(true));
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.LOCKER, LockerModel::createBaseMeshDefinition);
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.DUAL_LOCKER, DualLockerModel::createBaseMeshDefinition);
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.SAFE, SafeModel::createBaseMeshDefinition);
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.WAREHOUSE_CRATE, WarehouseCrateModel::createBaseMeshDefinition);
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.ITEM_TOWER, ItemTowerModel::createBaseMeshDefinition);
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.LOCKED_CHEST, ChestModel::createBaseMeshDefinition);
        ClientServices.CLIENT.registerEntityLayer(StorageModelLayers.LOCKED_SHULKER_BOX, ShulkerBoxModel::createBaseMeshDefinition);

        ClientServices.CLIENT.registerScreen(StorageContainerTypes.WOOD_CABINET.get(), GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.GLASS_CABINET.get(), GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.WAREHOUSE_CRATE.get(), GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.GOLD_SAFE.get(), GoldSafeScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.OBSIDIAN_SAFE.get(), GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKER.get(), LockerScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.DUAL_LOCKER.get(), DualLockerScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.ITEM_TOWER.get(), ItemTowerScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKSMITH_WORKBENCH.get(), LocksmithWorkbenchScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.KEY_RING.get(), KeyRingScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.BAG.get(), BagScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.ENDER_BAG.get(), EnderBagScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKED_ENDER_CHEST.get(), LockedEnderChestScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.CRATE.get(), CrateScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.CRATE_COMPACTING.get(), CrateCompactingScreen::new);

        StorageContainerTypes.CHESTS.forEach((material, menu) -> {
            ClientServices.CLIENT.registerScreen(menu.get(), LockedMaterialScreen::new);
        });

        StorageContainerTypes.BARRELS.forEach((material, menu) -> {
            ClientServices.CLIENT.registerScreen(menu.get(), LockedMaterialScreen::new);
        });

        StorageContainerTypes.HOPPERS.forEach((material, menu) -> {
            ClientServices.CLIENT.registerScreen(menu.get(), LockedHopperScreen::new);
        });

        StorageContainerTypes.SHULKERS.forEach((material, menu) -> {
            ClientServices.CLIENT.registerScreen(menu.get(), LockedMaterialScreen::new);
        });

        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.WOOD_CABINET, WoodCabinetBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.GLASS_CABINET, GlassCabinetBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.WAREHOUSE_CRATE, WarehouseCrateBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.GOLD_SAFE, GoldSafeBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.OBSIDIAN_SAFE, ObsidianSafeBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.LOCKER, LockerBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.ITEM_TOWER, ItemTowerBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.LOCKED_ENDER_CHEST, LockedEnderChestBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.LOCKED_CHEST, LockedChestBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.LOCKED_SHULKER_BOX, LockedShulkerBoxBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.CRATE, CrateBlockEntityRenderer::new);
        ClientServices.CLIENT.registerBlockEntityRenderer(StorageBlockEntityTypes.CRATE_COMPACTING, CrateBlockEntityRenderer::new);

        ClampedItemPropertyFunction colorOverride = (stack, world, entity, seed) -> stack.hasTag() && stack.getTag().contains(BagItem.TAG_PRIMARY_COLOR) && stack.getTag().getInt(BagItem.TAG_PRIMARY_COLOR) >= 0 ? 1.0F : 0.0F;
        ClampedItemPropertyFunction lockOverride = (stack, world, entity, seed) -> StorageUtil.getCode(stack).isEmpty() ? 0.0F : 1.0F;

        ClientServices.CLIENT.registerItemProperty(() -> StorageItems.BAG.get(), new ResourceLocation(Constants.MOD_ID, "color"), colorOverride);
        ClientServices.CLIENT.registerItemProperty(() -> StorageItems.BAG.get(), new ResourceLocation(Constants.MOD_ID, "locked"), lockOverride);
        ClientServices.CLIENT.registerItemProperty(() -> StorageItems.ENDER_BAG.get(), new ResourceLocation(Constants.MOD_ID, "locked"), lockOverride);

        for (Map.Entry<StorageMaterial, IRegistryObject<BagItem>> bag : StorageItems.BAGS.entrySet()) {
            ClientServices.CLIENT.registerItemProperty(() -> bag.getValue().get(), new ResourceLocation(Constants.MOD_ID, "color"), colorOverride);
            ClientServices.CLIENT.registerItemProperty(() -> bag.getValue().get(), new ResourceLocation(Constants.MOD_ID, "locked"), lockOverride);
        }

        ClampedItemPropertyFunction shulkerColorOverride = (stack, world, entity, seed) -> Optional.ofNullable(stack.getTag())
                .map(tag -> tag.contains("Color", Tag.TAG_INT) ? tag.getInt("Color") : null)
                .orElse(-1);

        ClientServices.CLIENT.registerItemProperty(() -> StorageBlocks.LOCKED_SHULKER_BOX.get().asItem(), new ResourceLocation(Constants.MOD_ID, "color"), shulkerColorOverride);
        for (Map.Entry<StorageMaterial, IRegistryObject<LockedShulkerBoxBlock>> entry : StorageBlocks.SHULKERS.entrySet()) {
            ClientServices.CLIENT.registerItemProperty(() -> entry.getValue().get().asItem(), new ResourceLocation(Constants.MOD_ID, "color"), shulkerColorOverride);
        }

        ClientServices.CLIENT.registerItemColor((stack, layer) -> {
            if (stack.getItem() instanceof BagItem && stack.hasTag()) {
                if (layer == 0 && stack.getTag().contains(BagItem.TAG_PRIMARY_COLOR)) {
                    int dyeColor = stack.getTag().getInt(BagItem.TAG_PRIMARY_COLOR);
                    return dyeColor == -1 ? 16777215 : DyeColor.byId(dyeColor).getFireworkColor();
                }

                if (layer == 1 && stack.getTag().contains(BagItem.TAG_SECONDARY_COLOR)) {
                    int dyeColor = stack.getTag().getInt(BagItem.TAG_SECONDARY_COLOR);
                    return dyeColor == -1 ? 16777215 : DyeColor.byId(dyeColor).getFireworkColor();
                }
            }
            return 16777215;
        }, () -> {
            List<Item> items = new ArrayList<>();
            items.add(StorageItems.BAG.get());
            items.addAll(StorageItems.BAGS.values().stream().map(x -> x.get()).collect(Collectors.toList()));
            return items;
        });

        ClientServices.CLIENT.registerBEWLR((register) -> {
            for (Block warehouseCrate : StorageBlockEntityTypes.getWarehouseCrates()) {
                register.registerBlockEntityWithoutLevelRenderer(warehouseCrate.asItem(), WarehouseCrateBEWLR.WAREHOUSE_CRATE_ITEM_RENDERER);
            }

            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.WOOD_CABINET.get().asItem(), StorageBEWLR.STORAGE_ITEM_RENDERER);
            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.GLASS_CABINET.get().asItem(), StorageBEWLR.STORAGE_ITEM_RENDERER);
            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.GOLD_SAFE.get().asItem(), StorageBEWLR.STORAGE_ITEM_RENDERER);
            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.OBSIDIAN_SAFE.get().asItem(), StorageBEWLR.STORAGE_ITEM_RENDERER);
            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.ITEM_TOWER.get().asItem(), StorageBEWLR.STORAGE_ITEM_RENDERER);
            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.LOCKED_ENDER_CHEST.get().asItem(), StorageBEWLR.STORAGE_ITEM_RENDERER);
            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.LOCKER.get().asItem(), LockerBEWLR.LOCKER_ITEM_RENDERER);

            for (Block shulker : StorageBlockEntityTypes.getShulkers()) {
                register.registerBlockEntityWithoutLevelRenderer(shulker.asItem(), new ShulkerBoxBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels(), shulker.defaultBlockState()));
            }

            register.registerBlockEntityWithoutLevelRenderer(StorageBlocks.LOCKED_CHEST.get().asItem(), new ChestBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels(), StorageBlocks.LOCKED_CHEST.get().defaultBlockState()));
            StorageBlocks.CHESTS.forEach((mat, r) -> {
                register.registerBlockEntityWithoutLevelRenderer(r.get().asItem(), new ChestBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels(), r.get().defaultBlockState()));
            });
        });
    }

}
