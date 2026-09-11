package com.grim3212.assorted.storage.client.color;

import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Tints a bag layer from a dye colour on the stack. The item model lists the tint layers, and
 * {@link #tag()} says which of the bag's two colours a layer takes.
 */
public record BagTintSource(String tag) implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "bag");
    // Colours carry an alpha channel now, so an opaque white is -1 rather than 0xFFFFFF.
    private static final int NO_TINT = -1;

    public static final MapCodec<BagTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Codec.STRING.fieldOf("tag").forGetter(BagTintSource::tag)
            ).apply(i, BagTintSource::new)
    );

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        if (!NBTHelper.hasTag(itemStack, this.tag)) {
            return NO_TINT;
        }

        int dyeColor = NBTHelper.getInt(itemStack, this.tag);
        // getFireworkColor is an unpacked RGB, so it has to be made opaque explicitly.
        return dyeColor == -1 ? NO_TINT : ARGB.opaque(DyeColor.byId(dyeColor).getFireworkColor());
    }

    @Override
    public MapCodec<BagTintSource> type() {
        return MAP_CODEC;
    }
}
