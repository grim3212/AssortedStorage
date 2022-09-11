package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.common.handler.EnabledCondition;
import com.grim3212.assorted.storage.common.handler.StorageConfig;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public class EnabledBlockItem extends BlockItem {

	private final String enabledCondition;

	public EnabledBlockItem(String enabledCondition, Block b, Properties props) {
		super(b, props);
		this.enabledCondition = enabledCondition;
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		switch (this.enabledCondition) {
			case EnabledCondition.BARRELS_CONDITION:
				return StorageConfig.COMMON.barrelsEnabled.get() ? super.allowedIn(tab) : false;
			default:
				return super.allowedIn(tab);
		}
	}
}