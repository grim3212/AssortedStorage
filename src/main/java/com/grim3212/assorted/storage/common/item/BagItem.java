package com.grim3212.assorted.storage.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.inventory.bag.BagCapabilityProvider;
import com.grim3212.assorted.storage.common.inventory.bag.BagContainer;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.util.NBTHelper;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class BagItem extends Item {

	public final static String TAG_PRIMARY_COLOR = "PrimaryColor";
	public final static String TAG_SECONDARY_COLOR = "SecondaryColor";

	private final StorageMaterial material;

	public BagItem(Properties props, @Nullable StorageMaterial material) {
		super(props.stacksTo(1));
		this.material = material;
	}

	public StorageMaterial getStorageMaterial() {
		return material;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
		if (this.allowedIn(tab)) {

			items.add(new ItemStack(this));

			if (material == null) {
				for (DyeColor color : DyeColor.values()) {
					items.add(NBTHelper.putIntItemStack(new ItemStack(this), TAG_PRIMARY_COLOR, color.getId()));
				}
			}
		}
	}

	@Override
	protected boolean allowedIn(CreativeModeTab tab) {
		if (!StorageConfig.COMMON.bagsEnabled.get()) {
			return false;
		}

		if (this.material != null && StorageConfig.COMMON.hideUncraftableItems.get() && ForgeRegistries.ITEMS.tags().getTag(this.getStorageMaterial().getMaterial()).size() <= 0) {
			return false;
		}

		return super.allowedIn(tab);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new BagCapabilityProvider(stack, this.material, nbt);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		String lockCode = StorageUtil.getCode(stack);
		if (!lockCode.isEmpty()) {
			tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.locked").withStyle(ChatFormatting.AQUA));
		}
		
		tooltip.add(Component.translatable(AssortedStorage.MODID + ".info.level_upgrade_level", Component.literal("" + (material == null ? 0 : material.getStorageLevel())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
		if (canAccess(playerIn.getItemInHand(handIn), playerIn)) {
			if (!level.isClientSide) {
				playerIn.openMenu(new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
						return new BagContainer(id, player.level, player.blockPosition(), inv, player);
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
	public String getDescriptionId(ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag().contains(TAG_PRIMARY_COLOR)) {
			return super.getDescriptionId(stack);
		}

		if (NBTHelper.getInt(stack, TAG_PRIMARY_COLOR) == -1) {
			return super.getDescriptionId(stack);
		}

		return super.getDescriptionId(stack) + "_" + DyeColor.byId(NBTHelper.getInt(stack, TAG_PRIMARY_COLOR)).getName();
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
