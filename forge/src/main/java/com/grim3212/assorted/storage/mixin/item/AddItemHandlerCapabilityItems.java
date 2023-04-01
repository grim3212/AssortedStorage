package com.grim3212.assorted.storage.mixin.item;

import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.inventory.ForgePlatformInventoryStorageHandler;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.EnderBagItem;
import com.grim3212.assorted.storage.common.item.KeyRingItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({BagItem.class, EnderBagItem.class, KeyRingItem.class})
public class AddItemHandlerCapabilityItems extends Item {

    public AddItemHandlerCapabilityItems(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof IInventoryItem inv) {
                return new ICapabilityProvider() {
                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                        if (cap == ForgeCapabilities.ITEM_HANDLER) {
                            return ((ForgePlatformInventoryStorageHandler) inv.getStorageHandler(stack)).getCapability(side).cast();
                        }
                        return LazyOptional.empty();
                    }
                };
            }
        }

        return null;
    }
}
