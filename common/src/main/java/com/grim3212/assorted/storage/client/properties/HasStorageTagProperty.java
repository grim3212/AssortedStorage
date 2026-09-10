package com.grim3212.assorted.storage.client.properties;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * The two branches a bag's item model picks between: whether the stack is dyed, and whether it is
 * locked. Both replace an {@code ItemProperties} function that a model {@code overrides} list keyed
 * on in 1.20.1.
 * <p>
 * Neither is expressible with a vanilla conditional. Both values live in
 * {@code DataComponents.CUSTOM_DATA}, and the only vanilla property that reads a component predicate,
 * {@code minecraft:component_matches}, needs an exact {@code NbtPredicate} value - which neither an
 * arbitrary dye id nor an arbitrary lock code can supply.
 */
public enum HasStorageTagProperty implements ConditionalItemModelProperty {
    DYED(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "dyed")) {
        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext context) {
            // A stored -1 means "no dye", same as BagTintSource treats it, so it must not select the
            // greyscale body texture.
            return NBTHelper.hasTag(stack, BagItem.TAG_PRIMARY_COLOR) && NBTHelper.getInt(stack, BagItem.TAG_PRIMARY_COLOR) >= 0;
        }
    },
    LOCKED(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "locked")) {
        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext context) {
            return StorageUtil.hasCode(stack);
        }
    };

    private final Identifier id;
    private final MapCodec<HasStorageTagProperty> codec;

    HasStorageTagProperty(Identifier id) {
        this.id = id;
        this.codec = MapCodec.unit(this);
    }

    public Identifier id() {
        return this.id;
    }

    @Override
    public MapCodec<HasStorageTagProperty> type() {
        return this.codec;
    }
}
