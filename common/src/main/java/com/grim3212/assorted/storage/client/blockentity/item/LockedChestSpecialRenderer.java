package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.client.model.ChestModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.grim3212.assorted.storage.client.model.StorageModelState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * The locked chests as items. Unlike {@link StorageSpecialRenderer} these are textured from the chest
 * atlas, so the unbaked form carries the sprite's path rather than a standalone png.
 */
public class LockedChestSpecialRenderer implements SpecialModelRenderer<Boolean> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "locked_chest");

    private final ChestModel model;
    private final SpriteGetter sprites;
    private final SpriteId sprite;

    public LockedChestSpecialRenderer(ChestModel model, SpriteGetter sprites, SpriteId sprite) {
        this.model = model;
        this.sprites = sprites;
        this.sprite = sprite;
    }

    @Override
    public void submit(@Nullable Boolean locked, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        StorageModelState state = Boolean.TRUE.equals(locked) ? StorageModelState.CLOSED_LOCKED : StorageModelState.CLOSED_UNLOCKED;
        submitNodeCollector.submitModel(this.model, state, poseStack, lightCoords, overlayCoords, -1, this.sprite, this.sprites, outlineColor, null);
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

    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked<Boolean> {
        public static final MapCodec<LockedChestSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(LockedChestSpecialRenderer.Unbaked::texture)
                ).apply(i, LockedChestSpecialRenderer.Unbaked::new)
        );

        @Override
        public MapCodec<LockedChestSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public LockedChestSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            ChestModel model = new ChestModel(context.entityModelSet().bakeLayer(StorageModelLayers.LOCKED_CHEST));
            return new LockedChestSpecialRenderer(model, context.sprites(), new SpriteId(Sheets.CHEST_SHEET, this.texture));
        }
    }
}
