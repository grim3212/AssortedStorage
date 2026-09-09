package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * Shared block item for the storage blocks.
 * <p>
 * The lock code and storage level lines used to be appended by the blocks themselves, but
 * {@code Block.appendHoverText} no longer exists - tooltips are an item concern now - so they moved
 * here. The stored contents lines are gone from the mod's own code entirely: the vanilla
 * {@code CONTAINER} data component the block entity hands to the stack renders them itself.
 */
public class StorageBlockItem extends BlockItem {

    public StorageBlockItem(Block b, Properties props) {
        super(b, props);
    }

    /**
     * {@code Item.appendHoverText} is marked deprecated in 26.x - tooltips are meant to come from
     * data components implementing {@code TooltipProvider} - but it is still the only per item
     * hook, and vanilla's own items (DiscFragmentItem, HangingEntityItem, SmithingTemplateItem)
     * still override it.
     * <p>
     * TODO(26.2): moving this text onto a component would mean giving the lock its own
     * DataComponentType instead of the CUSTOM_DATA tag AssortedLib's StorageUtil writes.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        String code = StorageUtil.getCode(stack);
        if (!code.isEmpty()) {
            tooltip.accept(Component.translatable(Constants.MOD_ID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
        }

        if (this.getBlock() instanceof IStorageMaterial storageBlock) {
            StorageMaterial material = storageBlock.getStorageMaterial();
            tooltip.accept(Component.translatable(Constants.MOD_ID + ".info.level_upgrade_level", Component.literal("" + (material == null ? 0 : material.getStorageLevel())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
        }
    }
}
