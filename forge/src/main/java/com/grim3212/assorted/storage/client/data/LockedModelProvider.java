package com.grim3212.assorted.storage.client.data;

import java.util.HashMap;
import java.util.Map;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LockedModelProvider extends ModelProvider<LockedModelBuilder> {

	final Map<ResourceLocation, LockedModelBuilder> previousModels = new HashMap<>();

	public LockedModelProvider(PackOutput output, ExistingFileHelper exHelper) {
		super(output, Constants.MOD_ID, "block", LockedModelBuilder::new, exHelper);
	}

	@Override
	public String getName() {
		return "Storage locked model provider";
	}

	@Override
	protected void registerModels() {
		super.generatedModels.putAll(previousModels);
	}

	public void previousModels() {
		previousModels.putAll(super.generatedModels);
	}
}