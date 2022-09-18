package com.grim3212.assorted.storage.common.item;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagCapabilityProvider;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

public class EnderBagItem extends Item {

	public EnderBagItem(Properties props) {
		super(props.stacksTo(1));
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		return StorageConfig.COMMON.bagsEnabled.get() && super.allowedIn(tab);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new EnderBagCapabilityProvider(stack, nbt);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		String lockCode = StorageUtil.getCode(stack);
		if (!lockCode.isEmpty()) {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.locked").withStyle(ChatFormatting.AQUA));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		if (canAccess(playerIn.getItemInHand(handIn), playerIn)) {
			if (!level.isClientSide) {
				playerIn.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
						return new EnderBagContainer(id, player.level, player.blockPosition(), inv, player);
					}

					@Override
					public Component getDisplayName() {
						return playerIn.getItemInHand(handIn).getHoverName();
					}
				});
			}
		}
		return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
	}

	@Override
	public boolean canFitInsideContainerItems() {
		return false;
	}

	private boolean canAccess(ItemStack stack, Player player) {
		String lockCode = StorageUtil.getCode(stack);

		if (!lockCode.isEmpty()) {
			for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
				ItemStack itemstack = player.getInventory().getItem(slot);

				if (!itemstack.isEmpty()) {
					if (itemstack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
						if (StorageUtil.hasCodeWithMatch(itemstack, lockCode)) {
							return true;
						}
					} else if (itemstack.getItem() == StorageItems.KEY_RING.get()) {
						IItemHandler keyRingItemHandler = itemstack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

						if (keyRingItemHandler instanceof KeyRingItemHandler keyItemhandler) {
							keyItemhandler.load();

							for (int keyRingSlot = 0; keyRingSlot < keyItemhandler.getSlots(); keyRingSlot++) {
								ItemStack keyRingStack = keyItemhandler.getStackInSlot(keyRingSlot);

								if (!keyRingStack.isEmpty()) {
									if (keyRingStack.getItem() == StorageItems.LOCKSMITH_KEY.get()) {
										if (StorageUtil.hasCodeWithMatch(keyRingStack, lockCode)) {
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
			return false;
		}

		return true;
	}
}
