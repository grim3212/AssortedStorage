package com.grim3212.assorted.storage.common.item;


import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ShulkerBoxBlockItem extends BlockItem {

    public ShulkerBoxBlockItem(Block b, Properties props) {
        super(b, props);
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
