package com.grim3212.assorted.storage.client.model;

import com.grim3212.assorted.storage.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class StorageModelLayers {
    public static final ModelLayerLocation CABINET = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "cabinet"), "main");
    public static final ModelLayerLocation GLASS_CABINET = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "glass_cabinet"), "main");
    public static final ModelLayerLocation SAFE = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "safe"), "main");
    public static final ModelLayerLocation LOCKER = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "locker"), "main");
    public static final ModelLayerLocation DUAL_LOCKER = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "dual_locker"), "main");
    public static final ModelLayerLocation WAREHOUSE_CRATE = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "warehouse_crate"), "main");
    public static final ModelLayerLocation ITEM_TOWER = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "item_tower"), "main");
    // Used by both locked chests and locked ender chests
    public static final ModelLayerLocation LOCKED_CHEST = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "locked_chest"), "main");

    public static final ModelLayerLocation LOCKED_SHULKER_BOX = new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "locked_shulker_box"), "main");
}
