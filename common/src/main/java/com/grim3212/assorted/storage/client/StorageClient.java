package com.grim3212.assorted.storage.client;

import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.storage.client.blockentity.*;
import com.grim3212.assorted.storage.client.blockentity.item.ItemTowerSpecialRenderer;
import com.grim3212.assorted.storage.client.blockentity.item.LockedChestSpecialRenderer;
import com.grim3212.assorted.storage.client.blockentity.item.LockedShulkerBoxSpecialRenderer;
import com.grim3212.assorted.storage.client.blockentity.item.StorageSpecialRenderer;
import com.grim3212.assorted.storage.client.color.BagTintSource;
import com.grim3212.assorted.storage.client.model.*;
import com.grim3212.assorted.storage.client.model.baked.LockedModel;
import com.grim3212.assorted.storage.client.screen.*;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.config.StorageClientConfig;

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

        ClientServices.CLIENT.registerScreen(StorageContainerTypes.WOOD_CABINET::get, GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.GLASS_CABINET::get, GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.WAREHOUSE_CRATE::get, GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.GOLD_SAFE::get, GoldSafeScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.OBSIDIAN_SAFE::get, GenericStorageScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKER::get, LockerScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.DUAL_LOCKER::get, DualLockerScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.ITEM_TOWER::get, ItemTowerScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKSMITH_WORKBENCH::get, LocksmithWorkbenchScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.KEY_RING::get, KeyRingScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.BAG::get, BagScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.ENDER_BAG::get, EnderBagScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKED_ENDER_CHEST::get, LockedEnderChestScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.CRATE::get, CrateScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.CRATE_COMPACTING::get, CrateCompactingScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKED_CHEST::get, LockedMaterialScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKED_BARREL::get, LockedMaterialScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKED_SHULKER_BOX::get, LockedMaterialScreen::new);
        ClientServices.CLIENT.registerScreen(StorageContainerTypes.LOCKED_HOPPER::get, LockedHopperScreen::new);

        // TODO(26.2): the fourteen locked doors used to be registered as RenderType.cutout() here.
        //  ItemBlockRenderTypes is gone and a block's chunk layer is now derived per quad from the
        //  sprite's transparency or from "render_type" in the block model json, so those calls have
        //  no runtime equivalent - the generated door models must carry
        //  "render_type": "minecraft:cutout" instead. That is a datagen change, outside this package.

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

        // TODO(26.2): registerItemProperty is gone and has no runtime replacement. The bag's "color"
        //  and "locked" properties and the shulker box's "color" property used to select an item model
        //  through ItemProperties; 26.2 chooses item models before baking, from the item's own model
        //  json, using a "minecraft:select" / "minecraft:condition" ItemModel over a codec-registered
        //  property under client.renderer.item.properties.**. Restoring those three needs a property
        //  type registered on each loader plus regenerated item models, both outside this package.

        // An item's tints live in its model json now; all that is registered from code is the source
        // type. Bag models need a "tints" entry naming this id once per dyed layer, with "tag" set to
        // BagItem.TAG_PRIMARY_COLOR / TAG_SECONDARY_COLOR.
        ClientServices.CLIENT.registerItemTintSource(BagTintSource.ID, BagTintSource.MAP_CODEC);

        // BlockEntityWithoutLevelRenderer is gone: a special item renderer is selected by the item's
        // own model json ("minecraft:special" naming one of these ids), so code only registers the id
        // to codec pairs. The generated item models for the cabinets, safes, lockers, warehouse
        // crates, item towers, locked chests and locked shulker boxes have to point at them.
        ClientServices.CLIENT.registerBEWLR((register) -> {
            register.registerSpecialModelRenderer(StorageSpecialRenderer.ID, StorageSpecialRenderer.Unbaked.MAP_CODEC);
            register.registerSpecialModelRenderer(ItemTowerSpecialRenderer.ID, ItemTowerSpecialRenderer.Unbaked.MAP_CODEC);
            register.registerSpecialModelRenderer(LockedChestSpecialRenderer.ID, LockedChestSpecialRenderer.Unbaked.MAP_CODEC);
            register.registerSpecialModelRenderer(LockedShulkerBoxSpecialRenderer.ID, LockedShulkerBoxSpecialRenderer.Unbaked.MAP_CODEC);
        });
    }

}
