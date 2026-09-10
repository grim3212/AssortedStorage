package com.grim3212.assorted.storage.client.model.baked;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecification;
import com.grim3212.assorted.lib.client.model.loaders.IModelSpecificationLoader;
import com.grim3212.assorted.lib.client.model.loaders.context.IModelBakingContext;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;

/**
 * A block model that swaps between a locked and an unlocked variant.
 * <p>
 * The two children are model <em>references</em> now rather than inline json objects. 26.2 parses
 * json models as {@code CuboidModel} records through a private Gson whose element and face adapters
 * are package private, so an inline child cannot be deserialised from a foreign context; a model id
 * resolved through {@link ModelBaker#getModel(Identifier)} can. The json shape is therefore
 * {@code {"unlocked": "namespace:block/foo_unlocked", "locked": "namespace:block/foo_locked"}}.
 */
public class LockedModel implements IModelSpecification<LockedModel> {

    public static final Identifier LOADER_NAME = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "locked");

    private final Identifier unlockedModel;
    private final Identifier lockedModel;

    private LockedModel(Identifier unlockedModel, Identifier lockedModel) {
        this.unlockedModel = unlockedModel;
        this.lockedModel = lockedModel;
    }

    // ModelBaker#getModel only resolves ids marked during discovery; discovery does not walk the
    // resource pack, so an unmarked child bakes to the missing model however correct its json is.
    @Override
    public void resolveDependencies(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.unlockedModel);
        resolver.markDependency(this.lockedModel);
    }

    @Override
    public BlockStateModel bake(IModelBakingContext context, ModelBaker baker, ModelState modelState, Identifier modelLocation) {
        BlockStateModelPart unlocked = SimpleModelWrapper.bake(baker, this.unlockedModel, modelState);
        BlockStateModelPart locked = SimpleModelWrapper.bake(baker, this.lockedModel, modelState);
        return new LockedBakedModel(unlocked, locked);
    }

    public enum Loader implements IModelSpecificationLoader<LockedModel> {
        INSTANCE;

        @Override
        public LockedModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            Identifier unlocked = Identifier.parse(GsonHelper.getAsString(modelContents, "unlocked"));
            Identifier locked = Identifier.parse(GsonHelper.getAsString(modelContents, "locked"));
            return new LockedModel(unlocked, locked);
        }
    }
}
