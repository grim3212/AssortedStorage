package com.grim3212.assorted.storage.common.item;

import java.util.function.Consumer;

import com.grim3212.assorted.storage.client.blockentity.item.ShulkerBoxBEWLR;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.util.NBTHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
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
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		super.fillItemCategory(tab, items);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains("Color")) {
			return super.getDescriptionId(stack);
		}

		if(NBTHelper.getInt(stack, "Color") == -1) {
			return super.getDescriptionId(stack);
		}
		
		return super.getDescriptionId(stack) + "_" + DyeColor.byId(NBTHelper.getInt(stack, "Color")).getName();
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		return StorageConfig.COMMON.shulkersEnabled.get() ? super.allowedIn(tab) : false;
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}
}
