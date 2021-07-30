package com.grim3212.assorted.storage.common.item;

import java.util.function.Consumer;

import com.grim3212.assorted.storage.client.blockentity.item.WarehouseCrateBEWLR;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

public class WarehouseCrateBlockItem extends BlockItem {

	public WarehouseCrateBlockItem(Block b, Properties props) {
		super(b, props);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return WarehouseCrateBEWLR.WAREHOUSE_CRATE_ITEM_RENDERER;
			}
		});
	}
}
