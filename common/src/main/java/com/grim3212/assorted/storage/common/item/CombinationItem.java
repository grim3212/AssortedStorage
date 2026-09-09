package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CombinationItem extends Item {

    public CombinationItem(Properties properties) {
        super(properties.stacksTo(16));
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flagIn) {
        // The lock is a CUSTOM_DATA component on the stack now rather than raw stack NBT.
        String code = StorageUtil.getCode(stack);

        if (!code.isEmpty()) {
            tooltip.accept(Component.translatable(Constants.MOD_ID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
        }
    }

}
