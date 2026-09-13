package com.grim3212.assorted.storage.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.handlers.StorageCreativeItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.grim3212.assorted.lib.test.TestSupport.*;
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
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
        out.accept("every_vanilla_wood_has_a_family", AssetTests::everyVanillaWoodHasAFamily);
        out.accept("every_locked_door_has_its_textures", AssetTests::everyLockedDoorHasItsTextures);
    }

    /**
     * Every vanilla {@link WoodType} is a {@link Wood}, and every {@code Wood} ships the two crate
     * textures that are drawn by hand rather than generated. Vanilla adding a wood is otherwise
     * silent: the missing crates simply never exist, which is how cherry, pale oak and bamboo went
     * unnoticed. The blocks themselves are covered by {@code everyBlockAndItemHasAModelAndAName},
     * which sees whatever {@code Wood} lists.
     */
    private static void everyVanillaWoodHasAFamily(GameTestHelper helper) {
        List<String> missing = new ArrayList<>();

        WoodType.values().filter(type -> Stream.of(Wood.values()).noneMatch(wood -> wood.getType() == type))
                .forEach(type -> missing.add("vanilla wood " + type.name() + " has no Wood entry"));

        for (Wood wood : Wood.values()) {
            if (!resourceExists("/assets/" + Constants.MOD_ID + "/textures/block/crates/" + wood + "_facing.png")) {
                missing.add("crate facing texture for " + wood);
            }
            if (!resourceExists("/assets/" + Constants.MOD_ID + "/textures/model/warehouse_crate/" + wood + ".png")) {
                missing.add("warehouse crate texture for " + wood);
            }
        }

        helper.assertTrue(missing.isEmpty(), missing.size() + " incomplete wood familie(s): " + String.join(", ", missing));
        helper.succeed();
    }

    /**
     * Every locked door's generated model names two textures that are actually in the jar. A texture
     * a model asks for and does not get draws as the missing-texture checkerboard, which is the only
     * warning there is; this reads the model rather than guessing the path, so the waxed copper doors
     * pointing at the unwaxed pngs is checked rather than assumed.
     */
    private static void everyLockedDoorHasItsTextures(GameTestHelper helper) {
        List<String> missing = new ArrayList<>();

        for (Block door : StorageBlocks.lockedDoors()) {
            JsonObject model = json("/assets/" + Constants.MOD_ID + "/models/block/" + name(door) + "_bottom_left.json");
            if (model == null) {
                missing.add(name(door) + " has no bottom model");
                continue;
            }

            JsonObject textures = model.getAsJsonObject("textures");
            for (String slot : new String[]{"bottom", "top"}) {
                String texture = string(textures, slot);
                if (texture == null) {
                    missing.add(name(door) + " names no " + slot + " texture");
                    continue;
                }

                Identifier id = Identifier.parse(texture);
                if (!resourceExists("/assets/" + id.getNamespace() + "/textures/" + id.getPath() + ".png")) {
                    missing.add(name(door) + " " + slot + " points at " + texture + ", which is not in the jar");
                }
            }
        }

        helper.assertTrue(missing.isEmpty(), missing.size() + " door texture problem(s): " + String.join(", ", missing));
        helper.succeed();
    }

    /** Every block and item has a model and a name. Every gap is reported at once. */
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
     * A locked barrel item shows its padlock, for every material. The closed barrel model is the
     * {@code assortedstorage:locked} loader, which an item bakes to its unlocked child, so the item
     * json has to choose between the two models itself, on the stack's lock.
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
     * Every custom blockstate model and loader model carries both loaders' keys: NeoForge reads
     * {@code "type"} and {@code "loader"}, Fabric only {@code "fabric:type"}. With only NeoForge's
     * key, Fabric loads a plain static model and nothing warns.
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
     * Every recipe file either loaded or was skipped by this loader's own load conditions; anything
     * else failed to parse.
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

    /**
     * Every non-vanilla item tag has a {@code tag.item.<namespace>.<path>} name, the check Fabric
     * API warns about at dev startup. Both loaders name the standard c: tags, so anything missing
     * is ours.
     */
    private static void everyItemTagHasAName(GameTestHelper helper) {
        Language language = Language.getInstance();
        List<String> missing = helper.getLevel().registryAccess().lookupOrThrow(Registries.ITEM).getTags()
                .map(tag -> tag.key().location())
                .filter(id -> !"minecraft".equals(id.getNamespace()))
                .map(id -> "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.'))
                .filter(key -> !language.has(key))
                .sorted()
                .toList();
        helper.assertTrue(missing.isEmpty(), "item tags with no name in any lang file: " + missing);
        helper.succeed();
    }
}
