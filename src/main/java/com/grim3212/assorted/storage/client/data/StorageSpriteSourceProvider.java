package com.grim3212.assorted.storage.client.data;

import java.util.Optional;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.StorageModels;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class StorageSpriteSourceProvider extends SpriteSourceProvider {

	public StorageSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, fileHelper, AssortedStorage.MODID);
	}

	@Override
	protected void addSources() {
		for (ResourceLocation tex : StorageModels.CHEST_LOCATIONS.values()) {
			atlas(SpriteSourceProvider.CHESTS_ATLAS).addSource(new SingleFile(tex, Optional.empty()));
		}

		for (ResourceLocation tex : StorageModels.SHULKER_LOCATIONS.values()) {
			atlas(SpriteSourceProvider.SHULKER_BOXES_ATLAS).addSource(new SingleFile(tex, Optional.empty()));
		}
	}

}
