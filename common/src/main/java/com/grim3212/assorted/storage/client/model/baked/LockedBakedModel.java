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

    // TODO(26.2): the item side of this model is gone. It used to carry an ItemOverrides list whose
    //  resolve() swapped in the locked model for a stack with a lock code, which is how a locked
    //  barrel/hopper item showed its padlock in inventories. ItemOverrides was deleted outright:
    //  item variation is chosen before baking, by an ItemModel named in the item's own model json -
    //  here that would be a "minecraft:condition" ItemModel over a registered ConditionalItemModel
    //  property that reports whether the stack has a lock code, with the locked and unlocked models as
    //  its branches. That needs a client-side property registration plus a change to the generated
    //  item models, so it is deliberately left undone rather than faked: the block still swaps
    //  correctly in the world, only the item form does not.

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
