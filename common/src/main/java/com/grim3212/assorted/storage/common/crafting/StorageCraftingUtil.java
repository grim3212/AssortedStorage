package com.grim3212.assorted.storage.common.crafting;

import com.google.gson.*;
import com.grim3212.assorted.lib.platform.Services;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public class StorageCraftingUtil {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static Item getItem(String itemName, boolean disallowsAirInRecipe) {
        ResourceLocation itemKey = new ResourceLocation(itemName);
        if (!Services.PLATFORM.getRegistry(Registries.ITEM).containsKey(itemKey))
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");

        Item item = Services.PLATFORM.getRegistry(Registries.ITEM).getValue(itemKey).orElse(Items.AIR);
        if (disallowsAirInRecipe && item == Items.AIR)
            throw new JsonSyntaxException("Invalid item: " + itemName);
        return Objects.requireNonNull(item);
    }

    public static CompoundTag getNBT(JsonElement element) {
        try {
            if (element.isJsonObject())
                return TagParser.parseTag(GSON.toJson(element));
            else
                return TagParser.parseTag(GsonHelper.convertToString(element, "nbt"));
        } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException("Invalid NBT Entry: " + e);
        }
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT, boolean disallowsAirInRecipe) {
        String itemName = GsonHelper.getAsString(json, "item");
        Item item = getItem(itemName, disallowsAirInRecipe);
        if (readNBT && json.has("nbt")) {
            CompoundTag nbt = getNBT(json.get("nbt"));
            CompoundTag tmp = new CompoundTag();
            if (nbt.contains("ForgeCaps")) {
                tmp.put("ForgeCaps", nbt.get("ForgeCaps"));
                nbt.remove("ForgeCaps");
            }

            tmp.put("tag", nbt);
            tmp.putString("id", itemName);
            tmp.putInt("Count", GsonHelper.getAsInt(json, "count", 1));

            return ItemStack.of(tmp);
        }

        return new ItemStack(item, GsonHelper.getAsInt(json, "count", 1));
    }

    public static boolean hasNoElements(Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return true;
        if (items.length == 1) {
            //If we potentially added a barrier due to the ingredient being an empty tag, try and check if it is the stack we added
            ItemStack item = items[0];
            return item.getItem() == Items.BARRIER && item.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
        }
        return false;
    }
}
