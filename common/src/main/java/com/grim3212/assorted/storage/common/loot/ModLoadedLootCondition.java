package com.grim3212.assorted.storage.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/**
 * Loot conditions are codec driven in 26.x: the registry holds a {@link MapCodec} directly and a
 * condition exposes it through {@code codec()}, so the old Gson {@code Serializer} inner class and
 * the {@code getType()} lookup are both gone.
 */
public record ModLoadedLootCondition(String modId) implements LootItemCondition {

    public static final MapCodec<ModLoadedLootCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.STRING.fieldOf("modId").forGetter(ModLoadedLootCondition::modId)
            ).apply(instance, ModLoadedLootCondition::new));

    @Override
    public MapCodec<ModLoadedLootCondition> codec() {
        return MAP_CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        return Services.PLATFORM.isModLoaded(this.modId);
    }

    public static LootItemCondition.Builder isModLoaded(String modId) {
        return () -> new ModLoadedLootCondition(modId);
    }
}
