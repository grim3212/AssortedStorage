package com.grim3212.assorted.storage.mixin.item;

import com.grim3212.assorted.lib.inventory.ForgeItemStackStorageWrapper;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.inventory.bag.BagItemHandler;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagItemHandler;
import com.grim3212.assorted.storage.common.inventory.keyring.KeyRingItemHandler;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.EnderBagItem;
import com.grim3212.assorted.storage.common.item.KeyRingItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class AddItemHandlerCapabilityItems {

    @Mixin(BagItem.class)
    public static class BagItemCapability extends Item {
        @Shadow
        private StorageMaterial material;

        public BagItemCapability(Properties pProperties) {
            super(pProperties);
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new ForgeItemStackStorageWrapper(new BagItemHandler(stack, this.material));
        }
    }

    @Mixin(EnderBagItem.class)
    public static class EnderBagItemCapability extends Item {

        public EnderBagItemCapability(Properties pProperties) {
            super(pProperties);
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new ForgeItemStackStorageWrapper(new EnderBagItemHandler(stack));
        }
    }

    @Mixin(KeyRingItem.class)
    public static class KeyRingItemCapability extends Item {

        public KeyRingItemCapability(Properties pProperties) {
            super(pProperties);
        }

        @Override
        public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
            return new ForgeItemStackStorageWrapper(new KeyRingItemHandler(stack));
        }
    }
}
