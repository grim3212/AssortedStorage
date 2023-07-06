package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LockerBEWLR extends BlockEntityWithoutLevelRenderer {

    public static final BlockEntityWithoutLevelRenderer LOCKER_ITEM_RENDERER = new LockerBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels());

    private final Supplier<BlockEntityRenderDispatcher> blockEntityRenderDispatcher;
    private final LockerBlockEntity lockerBlockEntity = new LockerBlockEntity(BlockPos.ZERO, StorageBlocks.LOCKER.get().defaultBlockState());

    public LockerBEWLR(Supplier<BlockEntityRenderDispatcher> dispatcher, Supplier<EntityModelSet> modelSet) {
        super(dispatcher.get(), modelSet.get());
        this.blockEntityRenderDispatcher = dispatcher;
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        this.blockEntityRenderDispatcher.get().renderItem(this.lockerBlockEntity, matrix, renderer, light, overlayLight);
    }
}
