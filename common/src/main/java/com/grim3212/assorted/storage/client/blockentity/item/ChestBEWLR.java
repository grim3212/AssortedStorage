package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.common.block.blockentity.LockedChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ChestBEWLR extends BlockEntityWithoutLevelRenderer {

    private final Supplier<BlockEntityRenderDispatcher> blockEntityRenderDispatcher;
    private final LockedChestBlockEntity blockEntity;

    public ChestBEWLR(Supplier<BlockEntityRenderDispatcher> dispatcher, Supplier<EntityModelSet> modelSet, BlockState blockToUse) {
        super(dispatcher.get(), modelSet.get());

        this.blockEntityRenderDispatcher = dispatcher;
        this.blockEntity = new LockedChestBlockEntity(BlockPos.ZERO, blockToUse);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light, int overlayLight) {
        if (this.blockEntity != null) {
            if (StorageUtil.hasCode(stack)) {
                this.blockEntity.setLockCode(StorageUtil.getCode(stack));
            } else {
                this.blockEntity.setLockCode(null);
            }

            this.blockEntityRenderDispatcher.get().renderItem(this.blockEntity, matrix, renderer, light, overlayLight);
        }
    }
}