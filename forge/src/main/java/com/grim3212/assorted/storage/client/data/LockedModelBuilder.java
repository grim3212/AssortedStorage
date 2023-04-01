package com.grim3212.assorted.storage.client.data;

import com.google.gson.JsonObject;
import com.grim3212.assorted.storage.client.model.baked.LockedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LockedModelBuilder extends ModelBuilder<LockedModelBuilder> {

    private ResourceLocation unlockedModel;
    private ResourceLocation lockedModel;

    protected LockedModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public LockedModelBuilder unlockedModel(ResourceLocation unlockedModel) {
        this.unlockedModel = unlockedModel;
        return this;
    }

    public LockedModelBuilder lockedModel(ResourceLocation lockedModel) {
        this.lockedModel = lockedModel;
        return this;
    }

    @Override
    public JsonObject toJson() {
        JsonObject ret = super.toJson();
        ret.addProperty("loader", LockedModel.LOADER_NAME.toString());

        JsonObject unlockedObj = new JsonObject();
        unlockedObj.addProperty("parent", unlockedModel.toString());
        ret.add("unlocked", unlockedObj);

        JsonObject lockedObj = new JsonObject();
        lockedObj.addProperty("parent", lockedModel.toString());
        ret.add("locked", lockedObj);
        return ret;
    }
}