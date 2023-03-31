package com.grim3212.assorted.storage.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ModLoadedLootCondition implements LootItemCondition {
    private final String modId;

    private ModLoadedLootCondition(String modId) {
        this.modId = modId;
    }

    @Override
    public LootItemConditionType getType() {
        return StorageLootConditions.MOD_LOADED.get();
    }

    @Override
    public boolean test(LootContext context) {
        return Services.PLATFORM.isModLoaded(modId);
    }

    public static ModLoadedLootCondition.Builder isModLoaded(String modId) {
        return new ModLoadedLootCondition.Builder(modId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final String modId;

        public Builder(String modId) {
            this.modId = modId;
        }

        @Override
        public LootItemCondition build() {
            return new ModLoadedLootCondition(this.modId);
        }
    }

    public static class ModLoadedLootConditionSerializer implements Serializer<ModLoadedLootCondition> {
        @Override
        public void serialize(JsonObject json, ModLoadedLootCondition condition, JsonSerializationContext context) {
            json.addProperty("modId", condition.modId);
        }

        @Override
        public ModLoadedLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return new ModLoadedLootCondition(GsonHelper.getAsString(json, "modId"));
        }
    }

}
