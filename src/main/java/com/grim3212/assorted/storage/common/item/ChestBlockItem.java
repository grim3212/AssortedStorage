package com.grim3212.assorted.storage.common.item;

import java.util.function.Consumer;

import com.grim3212.assorted.storage.client.blockentity.item.ChestBEWLR;
import com.grim3212.assorted.storage.common.handler.StorageConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ChestBlockItem extends BlockItem {

	public ChestBlockItem(Block b, Properties props) {
		super(b, props);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new ChestBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels(), getBlock().defaultBlockState());
			}
		});
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		return StorageConfig.COMMON.chestsEnabled.get() ? super.allowedIn(tab) : false;
	}
}
