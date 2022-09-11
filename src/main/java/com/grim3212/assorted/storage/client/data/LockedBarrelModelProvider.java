package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LockedBarrelModelProvider extends ModelProvider<LockedBarrelModelBuilder> {

	final Map<ResourceLocation, LockedBarrelModelBuilder> previousModels = new HashMap<>();

	public LockedBarrelModelProvider(DataGenerator gen, ExistingFileHelper exHelper) {
		super(gen, AssortedStorage.MODID, "block", LockedBarrelModelBuilder::new, exHelper);
	}

	@Override
	public String getName() {
		return "Storage barrel model provider";
	}

	@Override
	protected void registerModels() {
		super.generatedModels.putAll(previousModels);
	}

	public void previousModels() {
		previousModels.putAll(super.generatedModels);
	}
}