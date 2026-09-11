package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.storage.common.item.upgrades.AmountUpgradeItem;
import com.grim3212.assorted.storage.common.item.upgrades.RedstoneUpgradeItem;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

/**
 * The mode line a crate upgrade shows in its tooltip. The mode itself stays in the stack's
 * {@code custom_data} under {@code Mode}; this only says which upgrade's wording to show it in.
 *
 * @param kind which upgrade this is
 */
public record UpgradeModeInfo(Kind kind) implements TooltipProvider {

    public static final Codec<UpgradeModeInfo> CODEC = Kind.CODEC.xmap(UpgradeModeInfo::new, UpgradeModeInfo::kind);
    public static final StreamCodec<ByteBuf, UpgradeModeInfo> STREAM_CODEC = Kind.STREAM_CODEC.map(UpgradeModeInfo::new, UpgradeModeInfo::kind);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flag, DataComponentGetter components) {
        int mode = components.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getIntOr("Mode", this.kind.startingMode);
        tooltip.accept(switch (this.kind) {
            case AMOUNT -> AmountUpgradeItem.describeMode(mode);
            case REDSTONE -> RedstoneUpgradeItem.describeMode(mode);
        });
    }

    public enum Kind implements StringRepresentable {
        AMOUNT("amount", 0),
        REDSTONE("redstone", 4);

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);
        public static final StreamCodec<ByteBuf, Kind> STREAM_CODEC = ByteBufCodecs.idMapper(i -> values()[i], Kind::ordinal);

        private final String name;
        // The mode a stack reads as before the upgrade has ever been cycled.
        private final int startingMode;

        Kind(String name, int startingMode) {
            this.name = name;
            this.startingMode = startingMode;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
