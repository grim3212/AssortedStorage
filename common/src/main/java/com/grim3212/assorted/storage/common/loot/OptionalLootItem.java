package com.grim3212.assorted.storage.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.Consumer;

public class OptionalLootItem extends LootPoolSingletonContainer {

    private final ResourceLocation lootItem;

    protected OptionalLootItem(ResourceLocation lootItem, int p_79681_, int p_79682_, LootItemCondition[] p_79683_, LootItemFunction[] p_79684_) {
        super(p_79681_, p_79682_, p_79683_, p_79684_);
        this.lootItem = lootItem;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> itemStacks, LootContext context) {
        Services.PLATFORM.getRegistry(Registries.ITEM).getValue(lootItem).ifPresent((item) -> {
            itemStacks.accept(new ItemStack(item));
        });
    }

    @Override
    public LootPoolEntryType getType() {
        return StorageLootEntries.OPTIONAL_ITEM.get();
    }

    public static LootPoolSingletonContainer.Builder<?> optionalLootTableItem(ResourceLocation location) {
        return simpleBuilder((p_79583_, p_79584_, p_79585_, p_79586_) -> {
            return new OptionalLootItem(location, p_79583_, p_79584_, p_79585_, p_79586_);
        });
    }

    public static class Serializer extends LootPoolSingletonContainer.Serializer<OptionalLootItem> {
        public void serializeCustom(JsonObject json, OptionalLootItem lootItem, JsonSerializationContext jsonContext) {
            super.serializeCustom(json, lootItem, jsonContext);
            json.addProperty("name", lootItem.lootItem.toString());
        }

        protected OptionalLootItem deserialize(JsonObject json, JsonDeserializationContext p_79595_, int p_79596_, int p_79597_, LootItemCondition[] p_79598_, LootItemFunction[] p_79599_) {
            return new OptionalLootItem(new ResourceLocation(GsonHelper.getAsString(json, "name")), p_79596_, p_79597_, p_79598_, p_79599_);
        }
    }
}
