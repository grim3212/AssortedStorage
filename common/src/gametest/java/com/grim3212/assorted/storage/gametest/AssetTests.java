package com.grim3212.assorted.storage.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.handlers.StorageCreativeItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.storage.gametest.StorageTestSupport.*;

/**
 * What the mod ships: models and names, barrel item padlocks, loader keys both loaders read, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("every_block_and_item_has_a_model_and_a_name", AssetTests::everyBlockAndItemHasAModelAndAName);
        out.accept("barrel_items_show_their_padlock", AssetTests::barrelItemsShowTheirPadlock);
        out.accept("loader_models_are_read_on_both_loaders", AssetTests::loaderModelsAreReadOnBothLoaders);
        out.accept("every_recipe_loads_or_is_conditioned_off", AssetTests::everyRecipeLoadsOrIsConditionedOff);
    }

    /**
     * Every block and item this mod registers has a model and a name.
     * <p>
     * Both are generated - blockstates and item models by the NeoForge client datagen, the lang
     * file by hand since this mod's language provider did not survive the port - and neither shows
     * up as a compile error, so a missing one is only ever found by looking. Every gap is reported
     * at once, because finding them one run at a time is unbearable.
     */
    private static void everyBlockAndItemHasAModelAndAName(GameTestHelper helper) {
        JsonObject lang = lang(helper);
        List<String> missing = new ArrayList<>();

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }

            if (!resourceExists("/assets/" + Constants.MOD_ID + "/blockstates/" + id.getPath() + ".json")) {
                missing.add("blockstate " + id.getPath());
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("lang key " + entry.getValue().getDescriptionId());
            }
        }

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }

            if (!resourceExists("/assets/" + Constants.MOD_ID + "/items/" + id.getPath() + ".json")) {
                missing.add("item model " + id.getPath());
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("lang key " + entry.getValue().getDescriptionId());
            }
        }

        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(StorageCreativeItems.CREATIVE_TAB_KEY), "the Assorted Storage creative tab is not registered");
        helper.assertTrue(lang.has("itemGroup." + Constants.MOD_ID), "the Assorted Storage creative tab has no name");

        helper.assertTrue(missing.isEmpty(), missing.size() + " missing assets: " + String.join(", ", missing));
        helper.succeed();
    }

    /**
     * A locked barrel item shows its padlock - the plain barrel and every material one. The closed
     * barrel's own model is the {@code assortedstorage:locked} loader, which an item bakes to its
     * unlocked child, so an item json pointing at it silently loses the padlock. It has to choose
     * between the locked and unlocked models itself, on the stack's lock code, the way the bags do.
     * Read off the shipped item jsons, which are on the classpath even on a headless server.
     */
    private static void barrelItemsShowTheirPadlock(GameTestHelper helper) {
        List<Block> barrels = new ArrayList<>();
        barrels.add(StorageBlocks.LOCKED_BARREL.get());
        StorageBlocks.BARRELS.values().forEach(barrel -> barrels.add(barrel.get()));
        helper.assertTrue(barrels.size() > 1, "only " + barrels.size() + " barrels are registered");

        List<String> wrong = new ArrayList<>();
        for (Block barrel : barrels) {
            String path = name(barrel);
            JsonObject item = json("/assets/" + Constants.MOD_ID + "/items/" + path + ".json");
            JsonObject model = item == null ? null : item.getAsJsonObject("model");
            if (model == null || !"minecraft:condition".equals(string(model, "type")) || !(Constants.MOD_ID + ":locked").equals(string(model, "property"))) {
                wrong.add(path + " does not choose its model on " + Constants.MOD_ID + ":locked");
                continue;
            }

            String locked = Constants.MOD_ID + ":block/" + path + "_locked";
            String unlocked = Constants.MOD_ID + ":block/" + path + "_unlocked";
            if (!locked.equals(string(model.getAsJsonObject("on_true"), "model"))) {
                wrong.add(path + " does not draw " + locked + " when locked");
            }
            if (!unlocked.equals(string(model.getAsJsonObject("on_false"), "model"))) {
                wrong.add(path + " does not draw " + unlocked + " when unlocked");
            }

            JsonObject lockedModel = json("/assets/" + Constants.MOD_ID + "/models/block/" + path + "_locked.json");
            String top = lockedModel == null ? null : string(lockedModel.getAsJsonObject("textures"), "top");
            if (top == null || !top.contains("locked_barrel_top")) {
                wrong.add(path + "_locked has no padlock top, got " + top);
            }
        }

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " barrel item model problem(s): " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Every custom blockstate model and every loader model this mod's blocks use is read by both
     * loaders. The jsons are generated once and shared, but NeoForge reads a variant's custom type from
     * {@code "type"} and a model's loader from {@code "loader"}, while Fabric reads both from
     * {@code "fabric:type"} and ignores the others. A json carrying only NeoForge's key loads on Fabric
     * as a plain static model - no locked block ever showing its padlock - and nothing warns.
     */
    private static void loaderModelsAreReadOnBothLoaders(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        List<String> checkedModels = new ArrayList<>();
        int customVariants = 0;

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            JsonObject blockstate = json("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
            if (blockstate == null) {
                continue;
            }

            for (JsonObject variant : blockstateVariants(blockstate)) {
                if (variant.has("type")) {
                    customVariants++;
                    if (!variant.get("type").equals(variant.get("fabric:type"))) {
                        wrong.add("blockstate " + id.getPath() + " has type " + variant.get("type") + " but fabric:type " + variant.get("fabric:type"));
                    }
                }

                String model = variant.has("model") ? variant.get("model").getAsString() : null;
                if (model == null || checkedModels.contains(model) || !model.startsWith(Constants.MOD_ID + ":")) {
                    continue;
                }
                checkedModels.add(model);

                Identifier modelId = Identifier.parse(model);
                JsonObject modelJson = json("/assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json");
                if (modelJson != null && modelJson.has("loader") && !modelJson.get("loader").equals(modelJson.get("fabric:type"))) {
                    wrong.add("model " + model + " has loader " + modelJson.get("loader") + " but fabric:type " + modelJson.get("fabric:type"));
                }
            }
        }

        helper.assertTrue(customVariants > 0, "no custom blockstate variants were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " json(s) Fabric would read as static: " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Every recipe file this mod ships either loaded, or carries this loader's load conditions and was
     * skipped by them. A file with neither failed to parse. On Fabric that was every conditional
     * recipe for a while: Fabric's datagen wrote them without conditions, and the NeoForge copy that
     * shadowed it carries a key Fabric ignores - so only this loader's own key counts.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }
}
