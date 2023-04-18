package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.WarehouseCrateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class WarehouseCrateBEWLR extends BlockEntityWithoutLevelRenderer {

    public static final BlockEntityWithoutLevelRenderer WAREHOUSE_CRATE_ITEM_RENDERER = new WarehouseCrateBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels());

    private final Supplier<BlockEntityRenderDispatcher> blockEntityRenderDispatcher;
    private final WarehouseCrateBlockEntity oakCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.OAK_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity spruceCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity birchCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.BIRCH_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity acaciaCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.ACACIA_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity darkOakCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity jungleCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity crimsonCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity warpedCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.WARPED_WAREHOUSE_CRATE.get().defaultBlockState());
    private final WarehouseCrateBlockEntity mangroveCrateBlockEntity = new WarehouseCrateBlockEntity(BlockPos.ZERO, StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get().defaultBlockState());

    public WarehouseCrateBEWLR(Supplier<BlockEntityRenderDispatcher> dispatcher, Supplier<EntityModelSet> modelSet) {
        super(dispatcher.get(), modelSet.get());
        this.blockEntityRenderDispatcher = dispatcher;
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext transformType, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light, int overlayLight) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();

            BlockState blockstate = block.defaultBlockState();
            BlockEntity blockentity;
            if (blockstate.is(StorageBlocks.SPRUCE_WAREHOUSE_CRATE.get())) {
                blockentity = this.spruceCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.BIRCH_WAREHOUSE_CRATE.get())) {
                blockentity = this.birchCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.ACACIA_WAREHOUSE_CRATE.get())) {
                blockentity = this.acaciaCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.DARK_OAK_WAREHOUSE_CRATE.get())) {
                blockentity = this.darkOakCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.JUNGLE_WAREHOUSE_CRATE.get())) {
                blockentity = this.jungleCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.CRIMSON_WAREHOUSE_CRATE.get())) {
                blockentity = this.crimsonCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.WARPED_WAREHOUSE_CRATE.get())) {
                blockentity = this.warpedCrateBlockEntity;
            } else if (blockstate.is(StorageBlocks.MANGROVE_WAREHOUSE_CRATE.get())) {
                blockentity = this.mangroveCrateBlockEntity;
            } else {
                blockentity = this.oakCrateBlockEntity;
            }

            this.blockEntityRenderDispatcher.get().renderItem(blockentity, matrix, renderer, light, overlayLight);
        }

    }
}