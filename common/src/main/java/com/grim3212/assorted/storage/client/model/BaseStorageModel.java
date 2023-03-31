package com.grim3212.assorted.storage.client.model;

import java.util.function.Function;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseStorageModel extends Model {

	public boolean renderHandle = false;
	public float doorAngle = 0.0F;

	public BaseStorageModel(Function<ResourceLocation, RenderType> renderTypeIn) {
		super(renderTypeIn);
	}

	public abstract void handleRotations();
}
