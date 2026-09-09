package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class EnderBagItem extends Item implements IInventoryItem {

    public EnderBagItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler(ItemStack stack) {
        return Services.INVENTORY.createStorageInventoryHandler(new EnderBagItemHandler(stack));
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FORGE, value = "IForgeItem")
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FABRIC, value = "FabricItem")
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
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
        String lockCode = StorageUtil.getCode(stack);
        if (!lockCode.isEmpty()) {
            tooltip.accept(Component.translatable(Constants.MOD_ID + ".info.locked").withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public InteractionResult use(Level level, Player playerIn, InteractionHand handIn) {
        if (StorageAccessUtil.canAccess(playerIn.getItemInHand(handIn), playerIn)) {
            level.playSound(playerIn, playerIn.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS);
            if (!level.isClientSide()) {
                Services.PLATFORM.openMenu((ServerPlayer) playerIn, new MenuProvider() {
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                        return new EnderBagContainer(id, player.level(), player.blockPosition(), inv, player);
                    }

                    @Override
                    public Component getDisplayName() {
                        return playerIn.getItemInHand(handIn).getHoverName();
                    }
                }, buf -> buf.writeBlockPos(playerIn.blockPosition()));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
