package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.client.model.BaseStorageModel;
import com.grim3212.assorted.storage.client.model.StorageModelState;
import com.grim3212.assorted.storage.client.model.StorageModelType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * Draws a cabinet, safe, locker, warehouse crate or ender chest as an item.
 * <p>
 * This replaces the {@code StorageBEWLR} / {@code LockerBEWLR} / {@code WarehouseCrateBEWLR} family.
 * {@code BlockEntityWithoutLevelRenderer} is gone in 26.2, and with it the ability to attach an item
 * renderer to an {@code Item} from code: an item opts in from its own model json, which names a
 * {@code minecraft:special} renderer by id, and the only thing registered in code is the id to
 * {@link MapCodec} pair. The dummy block entities the old renderers kept around are gone too - the
 * model is submitted directly, and the one thing that varied per stack, whether the padlock is shown,
 * comes back through {@link #extractArgument(ItemStack)}.
 */
public class StorageSpecialRenderer implements SpecialModelRenderer<Boolean> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "storage");

    private final BaseStorageModel model;
    private final Identifier texture;

    public StorageSpecialRenderer(BaseStorageModel model, Identifier texture) {
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void submit(@Nullable Boolean locked, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        StorageModelState state = Boolean.TRUE.equals(locked) ? StorageModelState.CLOSED_LOCKED : StorageModelState.CLOSED_UNLOCKED;
        submitNodeCollector.submitModel(this.model, state, poseStack, this.texture, lightCoords, overlayCoords, outlineColor, null);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.model.setupAnim(StorageModelState.CLOSED_UNLOCKED);
        this.model.root().getExtentsForGui(poseStack, output);
    }

    @Override
    public @Nullable Boolean extractArgument(ItemStack stack) {
        return StorageUtil.hasCode(stack);
    }

    public record Unbaked(StorageModelType model, Identifier texture) implements SpecialModelRenderer.Unbaked<Boolean> {
        public static final MapCodec<StorageSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        StorageModelType.CODEC.fieldOf("model").forGetter(StorageSpecialRenderer.Unbaked::model),
                        Identifier.CODEC.fieldOf("texture").forGetter(StorageSpecialRenderer.Unbaked::texture)
                ).apply(i, StorageSpecialRenderer.Unbaked::new)
        );

        @Override
        public MapCodec<StorageSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public StorageSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new StorageSpecialRenderer(this.model.bake(context.entityModelSet()), this.texture);
        }
    }
}
