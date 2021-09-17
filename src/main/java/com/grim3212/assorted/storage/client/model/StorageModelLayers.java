package com.grim3212.assorted.storage.client.model;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class StorageModelLayers {
	public static final ModelLayerLocation CABINET = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "cabinet"), "main");
	public static final ModelLayerLocation GLASS_CABINET = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "glass_cabinet"), "main");
	public static final ModelLayerLocation SAFE = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "safe"), "main");
	public static final ModelLayerLocation LOCKER = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "locker"), "main");
	public static final ModelLayerLocation DUAL_LOCKER = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "dual_locker"), "main");
	public static final ModelLayerLocation WAREHOUSE_CRATE = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "warehouse_crate"), "main");
	public static final ModelLayerLocation ITEM_TOWER = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "item_tower"), "main");
	public static final ModelLayerLocation LOCKED_ENDER_CHEST = new ModelLayerLocation(new ResourceLocation(AssortedStorage.MODID, "locked_ender_chest"), "main");
}
