package com.grim3212.assorted.storage.common.item;

import java.util.function.Consumer;

import com.grim3212.assorted.storage.client.blockentity.item.ShulkerBoxBEWLR;
import com.grim3212.assorted.storage.common.util.NBTHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ShulkerBoxBlockItem extends BlockItem {

	public ShulkerBoxBlockItem(Block b, Properties props) {
		super(b, props);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new ShulkerBoxBEWLR(() -> Minecraft.getInstance().getBlockEntityRenderDispatcher(), () -> Minecraft.getInstance().getEntityModels(), getBlock().defaultBlockState());
			}
		});
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains("Color")) {
			return super.getDescriptionId(stack);
		}

		if (NBTHelper.getInt(stack, "Color") == -1) {
			return super.getDescriptionId(stack);
		}

		return super.getDescriptionId(stack) + "_" + DyeColor.byId(NBTHelper.getInt(stack, "Color")).getName();
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
}
