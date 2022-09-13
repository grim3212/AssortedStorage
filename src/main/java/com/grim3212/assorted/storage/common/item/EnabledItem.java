package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.common.handler.EnabledCondition;
import com.grim3212.assorted.storage.common.handler.StorageConfig;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class EnabledItem extends Item {

	private final String enabledCondition;

	public EnabledItem(String enabledCondition, Properties props) {
		super(props);
		this.enabledCondition = enabledCondition;
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		switch (this.enabledCondition) {
			case EnabledCondition.UPGRADES_CONDITION:
				return StorageConfig.COMMON.upgradesEnabled.get() ? super.allowedIn(tab) : false;
			default:
				return super.allowedIn(tab);
		}
	}

}
