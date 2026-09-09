package com.grim3212.assorted.storage.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

/**
 * A loot entry naming an item that may not exist, so an optional dependency can be dropped without
 * breaking the table.
 * <p>
 * Loot entries are codec driven in 26.x - the registry holds a {@link MapCodec} and the entry
 * exposes it through {@code codec()} - so the Gson {@code Serializer} inner class is gone, and the
 * condition/function arrays became {@code List}s.
 */
public class OptionalLootItem extends LootPoolSingletonContainer {

    public static final MapCodec<OptionalLootItem> MAP_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(Identifier.CODEC.fieldOf("name").forGetter(entry -> entry.lootItem))
                    .and(singletonFields(instance))
                    .apply(instance, OptionalLootItem::new));

    private final Identifier lootItem;

    protected OptionalLootItem(Identifier lootItem, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.lootItem = lootItem;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> itemStacks, LootContext context) {
        Services.PLATFORM.getRegistry(Registries.ITEM).getValue(this.lootItem).ifPresent((item) -> {
            itemStacks.accept(new ItemStack(item));
        });
    }

    @Override
    public MapCodec<OptionalLootItem> codec() {
        return MAP_CODEC;
    }

    public static LootPoolSingletonContainer.Builder<?> optionalLootTableItem(Identifier location) {
        return simpleBuilder((weight, quality, conditions, functions) -> new OptionalLootItem(location, weight, quality, conditions, functions));
    }
}
