package com.grim3212.assorted.storage.common.item;


import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ShulkerBoxBlockItem extends StorageBlockItem {

    public ShulkerBoxBlockItem(Block b, Properties props) {
        super(b, props);
    }

    /**
     * {@code getDescriptionId(ItemStack)} is gone - {@code Item.getDescriptionId()} is final and a
     * per stack name is now a {@link Component} handed back by {@code getName}. The colour itself
     * lives in the stack's CUSTOM_DATA component rather than raw stack NBT.
     */
    @Override
    public Component getName(ItemStack stack) {
        int color = NBTHelper.getInt(stack, "Color", -1);
        if (color == -1) {
            return super.getName(stack);
        }

        return Component.translatable(this.getDescriptionId() + "_" + DyeColor.byId(color).getName());
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
