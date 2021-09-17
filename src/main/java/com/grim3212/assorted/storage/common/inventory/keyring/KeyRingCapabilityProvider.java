package com.grim3212.assorted.storage.common.inventory.keyring;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeyRingCapabilityProvider implements ICapabilitySerializable<Tag> {
	private final KeyRingItemHandler inventory;
	private final LazyOptional<IItemHandler> optional;

	public KeyRingCapabilityProvider(ItemStack stack, CompoundTag nbtIn) {
		inventory = new KeyRingItemHandler(stack);
		optional = LazyOptional.of(() -> inventory);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return optional.cast();
		} else
			return LazyOptional.empty();
	}

	@Override
	public Tag serializeNBT() {
		inventory.save();
		return new CompoundTag();
	}

	@Override
	public void deserializeNBT(Tag nbt) {
		inventory.load();
	}
}
