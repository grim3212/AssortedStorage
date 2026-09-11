package com.grim3212.assorted.storage.client.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.grim3212.assorted.storage.client.model.baked.LockedModel;
import net.minecraft.resources.Identifier;
import com.grim3212.assorted.lib.client.data.LibCustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the {@code assortedstorage:locked} loader block into a block model json. The loader
 * replaces the geometry, so it takes no inline elements. The children are model ids:
 * {@code {"unlocked": "ns:block/foo_unlocked", "locked": "ns:block/foo_locked"}}.
 */
public class LockedModelBuilder extends LibCustomLoaderBuilder {

    public static LockedModelBuilder begin() {
        return new LockedModelBuilder();
    }

    private @Nullable Identifier unlockedModel;
    private @Nullable Identifier lockedModel;

    protected LockedModelBuilder() {
        super(LockedModel.LOADER_NAME, false);
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
    protected CustomLoaderBuilder copyInternal() {
        LockedModelBuilder copy = new LockedModelBuilder();
        copy.unlockedModel = this.unlockedModel;
        copy.lockedModel = this.lockedModel;
        return copy;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);

        Preconditions.checkNotNull(unlockedModel, "unlocked model must not be null");
        Preconditions.checkNotNull(lockedModel, "locked model must not be null");

        json.addProperty("unlocked", unlockedModel.toString());
        json.addProperty("locked", lockedModel.toString());

        return json;
    }
}
