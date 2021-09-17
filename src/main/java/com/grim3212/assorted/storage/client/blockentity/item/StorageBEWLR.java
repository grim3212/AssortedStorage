package com.grim3212.assorted.storage.client.blockentity.item;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.GlassCabinetBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.GoldSafeBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.ItemTowerBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.ObsidianSafeBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.WoodCabinetBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StorageBEWLR extends BlockEntityWithoutLevelRenderer {

	public static final BlockEntityWithoutLevelRenderer STORAGE_ITEM_RENDERER = new StorageBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels());

	private final Supplier<BlockEntityRenderDispatcher> blockEntityRenderDispatcher;
	private final GlassCabinetBlockEntity glassCabinetBlockEntity = new GlassCabinetBlockEntity(BlockPos.ZERO, StorageBlocks.GLASS_CABINET.get().defaultBlockState());
	private final WoodCabinetBlockEntity woodCabinetBlockEntity = new WoodCabinetBlockEntity(BlockPos.ZERO, StorageBlocks.WOOD_CABINET.get().defaultBlockState());
	private final GoldSafeBlockEntity goldSafeBlockEntity = new GoldSafeBlockEntity(BlockPos.ZERO, StorageBlocks.GOLD_SAFE.get().defaultBlockState());
	private final ObsidianSafeBlockEntity obsidianSafeBlockEntity = new ObsidianSafeBlockEntity(BlockPos.ZERO, StorageBlocks.OBSIDIAN_SAFE.get().defaultBlockState());
	private final ItemTowerBlockEntity itemTowerBlockEntity = new ItemTowerBlockEntity(BlockPos.ZERO, StorageBlocks.ITEM_TOWER.get().defaultBlockState());
	private final LockedEnderChestBlockEntity enderChestBlockEntity = new LockedEnderChestBlockEntity(BlockPos.ZERO, StorageBlocks.LOCKED_ENDER_CHEST.get().defaultBlockState());

	public StorageBEWLR(Supplier<BlockEntityRenderDispatcher> dispatcher, Supplier<EntityModelSet> modelSet) {
		super(dispatcher.get(), modelSet.get());
		this.blockEntityRenderDispatcher = dispatcher;
	}

	@Override
	public void renderByItem(@Nonnull ItemStack stack, @Nonnull TransformType transformType, @Nonnull PoseStack matrix, @Nonnull MultiBufferSource renderer, int light, int overlayLight) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem) item).getBlock();

			BlockState blockstate = block.defaultBlockState();
			BlockEntity blockentity;
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

			this.blockEntityRenderDispatcher.get().renderItem(blockentity, matrix, renderer, light, overlayLight);
		}

	}
}