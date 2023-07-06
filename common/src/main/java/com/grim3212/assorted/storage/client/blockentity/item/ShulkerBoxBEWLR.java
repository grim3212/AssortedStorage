package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.common.block.blockentity.LockedShulkerBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ShulkerBoxBEWLR extends BlockEntityWithoutLevelRenderer {

    private final Supplier<BlockEntityRenderDispatcher> blockEntityRenderDispatcher;
    private final LockedShulkerBoxBlockEntity blockEntity;

    public ShulkerBoxBEWLR(Supplier<BlockEntityRenderDispatcher> dispatcher, Supplier<EntityModelSet> modelSet, BlockState blockToUse) {
        super(dispatcher.get(), modelSet.get());

        this.blockEntityRenderDispatcher = dispatcher;
        this.blockEntity = new LockedShulkerBoxBlockEntity(BlockPos.ZERO, blockToUse);
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (this.blockEntity != null) {
            if (NBTHelper.hasTag(stack, "Color")) {
                int savedColor = NBTHelper.getInt(stack, "Color");
                DyeColor color = savedColor == -1 ? null : DyeColor.byId(savedColor);
                this.blockEntity.setColor(color);
            }

            if (StorageUtil.hasCode(stack)) {
                this.blockEntity.setLockCode(StorageUtil.getCode(stack));
            } else {
                this.blockEntity.setLockCode(null);
            }

            this.blockEntityRenderDispatcher.get().renderItem(this.blockEntity, matrix, renderer, light, overlayLight);
        }
    }
}