package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class KeyRingItem extends Item {

    public KeyRingItem(Properties props) {
        super(props.stacksTo(1));
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
            playerIn.openMenu(new MenuProvider() {

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    return new KeyRingContainer(id, player.level, player.blockPosition(), inv, player);
                }

                @Override
                public Component getDisplayName() {
                    return playerIn.getItemInHand(handIn).getHoverName();
                }
            });
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }
}
