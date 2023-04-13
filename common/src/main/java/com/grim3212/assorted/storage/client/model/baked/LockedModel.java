package com.grim3212.assorted.storage.client.model.baked;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class LockedModel implements IModelSpecification<LockedModel> {

    public static final ResourceLocation LOADER_NAME = new ResourceLocation(Constants.MOD_ID, "locked");

    private final BlockModel unbakedUnlockedModel;
    private final BlockModel unbakedLockedModel;

    private LockedModel(BlockModel unbakedUnlockedModel, BlockModel unbakedLockedModel) {
        this.unbakedUnlockedModel = unbakedUnlockedModel;
        this.unbakedLockedModel = unbakedLockedModel;
    }

    @Override
    public BakedModel bake(IModelBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        this.unbakedUnlockedModel.resolveParents(baker::getModel);
        this.unbakedLockedModel.resolveParents(baker::getModel);

        BakedModel bakedUnlockedBarrel = unbakedUnlockedModel.bake(baker, spriteGetter, modelState, modelLocation);
        BakedModel bakedLockedBarrel = unbakedLockedModel.bake(baker, spriteGetter, modelState, modelLocation);
        return new LockedBakedModel(context, bakedUnlockedBarrel, bakedLockedBarrel, baker, spriteGetter, modelState, modelLocation);
    }

    public enum Loader implements IModelSpecificationLoader<LockedModel> {
        INSTANCE;

        @Override
        public LockedModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            BlockModel unlockedModel = deserializationContext.deserialize(modelContents.getAsJsonObject("unlocked"), BlockModel.class);
            BlockModel lockedModel = deserializationContext.deserialize(modelContents.getAsJsonObject("locked"), BlockModel.class);
            return new LockedModel(unlockedModel, lockedModel);
        }
    }
}
