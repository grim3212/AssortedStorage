package com.grim3212.assorted.storage.common.item;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingCapabilityProvider;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class KeyRingItem extends Item {

	public KeyRingItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new KeyRingCapabilityProvider(stack, nbt);
	}

	@Override
	public ActionResult<ItemStack> use(World level, PlayerEntity playerIn, Hand handIn) {
		if (!level.isClientSide) {
			playerIn.openMenu(new INamedContainerProvider() {
				@Override
				public ITextComponent getDisplayName() {
					return playerIn.getItemInHand(handIn).getHoverName();

				}

				@Nullable
				@Override
				public Container createMenu(int p_createMenu_1_, PlayerInventory playerInv, PlayerEntity player) {
					return new KeyRingContainer(p_createMenu_1_, player.level, player.blockPosition(), playerInv, player);
				}
			});
		}
		return ActionResult.success(playerIn.getItemInHand(handIn));
	}
}
