package com.grim3212.assorted.storage.client.data;

import com.google.gson.JsonObject;
import com.grim3212.assorted.storage.client.model.baked.LockedModel;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class LockedModelBuilder extends ModelBuilder<LockedModelBuilder> {

    private Identifier unlockedModel;
    private Identifier lockedModel;

    protected LockedModelBuilder(Identifier outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public LockedModelBuilder unlockedModel(Identifier unlockedModel) {
        this.unlockedModel = unlockedModel;
        return this;
    }

    public LockedModelBuilder lockedModel(Identifier lockedModel) {
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