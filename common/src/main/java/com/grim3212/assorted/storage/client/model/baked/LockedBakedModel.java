package com.grim3212.assorted.storage.client.model.baked;

import com.grim3212.assorted.lib.client.model.baked.IDataAwareBakedModel;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.storage.common.properties.StorageModelProperties;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Picks the locked or unlocked geometry from the model data the block entity publishes for its
 * position.
 * <p>
 * {@code BakedModel} became {@code BlockStateModel} in 26.2: geometry is handed out as
 * {@link BlockStateModelPart}s appended to a list rather than returned per {@code Direction}, there is
 * no {@code getQuads(BlockState, ...)} because a model is already baked per block state, and a model
 * no longer chooses a {@code RenderType} - each quad carries its own {@code BakedQuad.MaterialInfo}
 * and the section compiler buckets them by its layer.
 */
public class LockedBakedModel implements IDataAwareBakedModel {

    private final BlockStateModelPart unlockedModel;
    private final BlockStateModelPart lockedModel;

    // An item never reaches the lock check below: it has no block entity, so no model data, and this
    // model would bake to its unlocked child. A barrel item chooses between the locked and unlocked
    // children in its own item json instead, on the assortedstorage:locked property - see
    // StorageBlockstateProvider#barrelState. A hopper item is a flat sprite, as it was in 1.20.1.

    public LockedBakedModel(BlockStateModelPart unlockedModel, BlockStateModelPart lockedModel) {
        this.unlockedModel = unlockedModel;
        this.lockedModel = lockedModel;
    }

    @Override
    public void collectParts(@NotNull RandomSource random, @NotNull IBlockModelData extraData, @NotNull List<BlockStateModelPart> output) {
        Boolean locked = extraData.getData(StorageModelProperties.IS_LOCKED);
        output.add(Boolean.TRUE.equals(locked) ? this.lockedModel : this.unlockedModel);
    }

    // Deprecated by NeoForge in favour of level/pos aware overloads that only exist in its patched
    // jar; vanilla still declares these abstract, so they have to be implemented here.
    @SuppressWarnings("deprecation")
    @Override
    public Material.Baked particleMaterial() {
        return this.unlockedModel.particleMaterial();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return this.unlockedModel.materialFlags() | this.lockedModel.materialFlags();
    }
}
