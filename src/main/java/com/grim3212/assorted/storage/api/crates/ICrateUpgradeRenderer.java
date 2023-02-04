package com.grim3212.assorted.storage.api.crates;

import com.grim3212.assorted.storage.common.block.blockentity.StorageCrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ICrateUpgradeRenderer {

	public void render(StorageCrateBlockEntity tileEntityIn, ItemStack selfStack, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn);
}
