package com.grim3212.assorted.storage.client.model;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.util.StorageMaterial;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AssortedStorage.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StorageModels {

	public static final Map<StorageMaterial, ResourceLocation> CHEST_LOCATIONS = Maps.newHashMap();
	public static final Map<StorageMaterial, ResourceLocation> SHULKER_LOCATIONS = Maps.newHashMap();
	static {
		CHEST_LOCATIONS.put(null, new ResourceLocation(AssortedStorage.MODID, "model/chests/normal"));
		SHULKER_LOCATIONS.put(null, new ResourceLocation(AssortedStorage.MODID, "model/shulkers/normal"));
		Stream.of(StorageMaterial.values()).forEach((type) -> {
			CHEST_LOCATIONS.put(type, new ResourceLocation(AssortedStorage.MODID, "model/chests/" + type.toString()));
			SHULKER_LOCATIONS.put(type, new ResourceLocation(AssortedStorage.MODID, "model/shulkers/" + type.toString()));
		});
	}

	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre event) {
		if (event.getAtlas().location().equals(Sheets.CHEST_SHEET)) {
			for (ResourceLocation tex : CHEST_LOCATIONS.values()) {
				event.addSprite(tex);
			}
		}
		if (event.getAtlas().location().equals(Sheets.SHULKER_SHEET)) {
			for (ResourceLocation tex : SHULKER_LOCATIONS.values()) {
				event.addSprite(tex);
			}
		}
	}

}
