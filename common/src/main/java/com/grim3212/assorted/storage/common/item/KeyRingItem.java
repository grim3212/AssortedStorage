package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KeyRingItem extends Item implements IInventoryItem {

    public KeyRingItem(Properties props) {
        super(props.stacksTo(1));
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler(ItemStack stack) {
        return Services.INVENTORY.createStorageInventoryHandler(new KeyRingItemHandler(stack));
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FORGE, value = "IForgeItem")
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FABRIC, value = "FabricItem")
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        if (!level.isClientSide) {
            Services.PLATFORM.openMenu((ServerPlayer) playerIn, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    return new KeyRingContainer(id, player.level, player.blockPosition(), inv, player);
                }

                @Override
                public Component getDisplayName() {
                    return playerIn.getItemInHand(handIn).getHoverName();
                }
            }, buf -> buf.writeBlockPos(playerIn.blockPosition()));
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }
}
