package com.grim3212.assorted.storage.client.model.baked;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public class LockedModel implements IUnbakedGeometry<LockedModel> {

	private final BlockModel unbakedUnlockedModel;
	private final BlockModel unbakedLockedModel;

	private LockedModel(BlockModel unbakedUnlockedModel, BlockModel unbakedLockedModel) {
		this.unbakedUnlockedModel = unbakedUnlockedModel;
		this.unbakedLockedModel = unbakedLockedModel;
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		this.unbakedUnlockedModel.resolveParents(modelGetter);
		this.unbakedLockedModel.resolveParents(modelGetter);
	}

	@Nullable
	@Override
	public BakedModel bake(IGeometryBakingContext owner, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation name) {
		BakedModel bakedUnlockedBarrel = unbakedUnlockedModel.bake(bakery, spriteGetter, transform, name);
		BakedModel bakedLockedBarrel = unbakedLockedModel.bake(bakery, spriteGetter, transform, name);
		return new LockedBakedModel(bakedUnlockedBarrel, bakedLockedBarrel, owner, spriteGetter.apply(owner.getMaterial("particle")), bakery, spriteGetter, transform, overrides, name);
	}

	public enum Loader implements IGeometryLoader<LockedModel> {
		INSTANCE;

		@Nonnull
		@Override
		public LockedModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			BlockModel unlockedModel = deserializationContext.deserialize(jsonObject.getAsJsonObject("unlocked"), BlockModel.class);
			BlockModel lockedModel = deserializationContext.deserialize(jsonObject.getAsJsonObject("locked"), BlockModel.class);
			return new LockedModel(unlockedModel, lockedModel);
		}
	}
}
