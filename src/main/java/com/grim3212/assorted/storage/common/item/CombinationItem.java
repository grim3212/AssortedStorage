package com.grim3212.assorted.storage.common.item;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CombinationItem extends Item {

	public CombinationItem(Properties properties) {
		super(properties.stacksTo(16));
	}

	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.hasTag()) {
			String code = stack.getTag().contains("Storage_Lock", 8) ? stack.getTag().getString("Storage_Lock") : "";

			if (!code.isEmpty()) {
				tooltip.add(new TranslationTextComponent(AssortedStorage.MODID + ".info.combo", new StringTextComponent(code).withStyle(TextFormatting.AQUA)));
			}
		}
	}

}
