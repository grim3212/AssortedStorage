package com.grim3212.assorted.storage.common.item;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CombinationItem extends Item {

	public CombinationItem(Properties properties) {
		super(properties.stacksTo(16));
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.hasTag()) {
			String code = stack.getTag().contains("Storage_Lock", 8) ? stack.getTag().getString("Storage_Lock") : "";

			if (!code.isEmpty()) {
				tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
			}
		}
	}

}
