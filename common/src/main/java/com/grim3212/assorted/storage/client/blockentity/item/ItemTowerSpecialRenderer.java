package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import com.grim3212.assorted.storage.client.model.StorageModelLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3fc;

import java.util.function.Consumer;

/**
 * The item tower as an item: a lone, capped section with empty shelves. See
 * {@link StorageSpecialRenderer} for why item renderers are data driven now.
 */
public class ItemTowerSpecialRenderer implements NoDataSpecialModelRenderer {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "item_tower");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/model/item_tower.png");

    private final ItemTowerModel model;

    public ItemTowerSpecialRenderer(ItemTowerModel model) {
        this.model = model;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        submitNodeCollector.submitModel(this.model, ItemTowerModel.State.INVENTORY, poseStack, TEXTURE, lightCoords, overlayCoords, outlineColor, null);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        this.model.setupAnim(ItemTowerModel.State.INVENTORY);
        this.model.root().getExtentsForGui(poseStack, output);
    }

    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<ItemTowerSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new ItemTowerSpecialRenderer.Unbaked());

        @Override
        public MapCodec<ItemTowerSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemTowerSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new ItemTowerSpecialRenderer(new ItemTowerModel(context.entityModelSet().bakeLayer(StorageModelLayers.ITEM_TOWER)));
        }
    }
}
