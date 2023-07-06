package com.grim3212.assorted.storage.client.blockentity.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.*;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StorageBEWLR extends BlockEntityWithoutLevelRenderer {

    public static final BlockEntityWithoutLevelRenderer STORAGE_ITEM_RENDERER = new StorageBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels());

    private final Supplier<BlockEntityRenderDispatcher> blockEntityRenderDispatcher;
    private final GlassCabinetBlockEntity glassCabinetBlockEntity;
    private final WoodCabinetBlockEntity woodCabinetBlockEntity;
    private final GoldSafeBlockEntity goldSafeBlockEntity;
    private final ObsidianSafeBlockEntity obsidianSafeBlockEntity;
    private final ItemTowerBlockEntity itemTowerBlockEntity;
    private final LockedEnderChestBlockEntity enderChestBlockEntity;

    public StorageBEWLR(Supplier<BlockEntityRenderDispatcher> dispatcher, Supplier<EntityModelSet> modelSet) {
        super(dispatcher.get(), modelSet.get());
        this.blockEntityRenderDispatcher = dispatcher;

        this.glassCabinetBlockEntity = new GlassCabinetBlockEntity(BlockPos.ZERO, StorageBlocks.GLASS_CABINET.get().defaultBlockState());
        this.woodCabinetBlockEntity = new WoodCabinetBlockEntity(BlockPos.ZERO, StorageBlocks.WOOD_CABINET.get().defaultBlockState());
        this.goldSafeBlockEntity = new GoldSafeBlockEntity(BlockPos.ZERO, StorageBlocks.GOLD_SAFE.get().defaultBlockState());
        this.obsidianSafeBlockEntity = new ObsidianSafeBlockEntity(BlockPos.ZERO, StorageBlocks.OBSIDIAN_SAFE.get().defaultBlockState());
        this.itemTowerBlockEntity = new ItemTowerBlockEntity(BlockPos.ZERO, StorageBlocks.ITEM_TOWER.get().defaultBlockState());
        this.enderChestBlockEntity = new LockedEnderChestBlockEntity(BlockPos.ZERO, StorageBlocks.LOCKED_ENDER_CHEST.get().defaultBlockState());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem) item).getBlock();

            BlockState blockstate = block.defaultBlockState();
            BaseStorageBlockEntity blockentity;
            if (blockstate.is(StorageBlocks.GLASS_CABINET.get())) {
                blockentity = this.glassCabinetBlockEntity;
            } else if (blockstate.is(StorageBlocks.WOOD_CABINET.get())) {
                blockentity = this.woodCabinetBlockEntity;
            } else if (blockstate.is(StorageBlocks.GOLD_SAFE.get())) {
                blockentity = this.goldSafeBlockEntity;
            } else if (blockstate.is(StorageBlocks.OBSIDIAN_SAFE.get())) {
                blockentity = this.obsidianSafeBlockEntity;
            } else if (blockstate.is(StorageBlocks.LOCKED_ENDER_CHEST.get())) {
                blockentity = this.enderChestBlockEntity;
            } else {
                blockentity = this.itemTowerBlockEntity;
            }

            if (StorageUtil.hasCode(stack)) {
                blockentity.setLockCode(StorageUtil.getCode(stack));
            } else {
                blockentity.setLockCode(null);
            }

            this.blockEntityRenderDispatcher.get().renderItem(blockentity, matrix, renderer, light, overlayLight);
        }

    }
}