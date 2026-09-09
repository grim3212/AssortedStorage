package com.grim3212.assorted.storage.api.crates;

import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.item.ItemStack;


public interface ICrateUpgradeRenderer {

    /**
     * {@code MultiBufferSource} is gone - rendering is push based now, so an upgrade renderer is
     * handed a {@link SubmitNodeCollector} and calls its {@code submitX} methods rather than
     * writing into a {@code VertexConsumer} it pulled out of a buffer source.
     */
    void render(CrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStackIn, SubmitNodeCollector collectorIn, int combinedLightIn, int combinedOverlayIn);
}
