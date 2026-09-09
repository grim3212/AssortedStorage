package com.grim3212.assorted.storage.client.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.grim3212.assorted.storage.client.model.baked.LockedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.generators.template.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the {@code assortedstorage:locked} loader block into a block model json.
 * <p>
 * Forge's {@code ModelBuilder} / {@code ModelProvider} pair is gone, so this is no longer a whole
 * model builder of its own - and with it goes {@code LockedModelProvider}, whose only job was to own
 * a second output folder for these models. {@link CustomLoaderBuilder} is still the hook, but it
 * plugs into {@link ExtendedModelTemplateBuilder#customLoader} and contributes to the json a
 * {@link net.minecraft.client.data.models.model.ModelTemplate} emits, so it is constructed with the
 * loader id plus whether the loader tolerates inline vanilla elements (it does not - it replaces the
 * geometry outright) and it has to be able to deep copy itself, because a {@code ModelTemplate} is
 * immutable.
 * <p>
 * The children are plain model ids now rather than {@code {"parent": ...}} objects: 26.2 parses json
 * models through a private Gson whose element and face adapters are package private, so an inline
 * child cannot be deserialised from a foreign context and {@link LockedModel} resolves each child
 * through {@code ModelBaker#getModel} instead. The shape it reads is
 * {@code {"unlocked": "ns:block/foo_unlocked", "locked": "ns:block/foo_locked"}}.
 */
public class LockedModelBuilder extends CustomLoaderBuilder {

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
