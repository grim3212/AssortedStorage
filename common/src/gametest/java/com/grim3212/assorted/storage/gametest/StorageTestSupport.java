package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponentType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.LockedEnderChestBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.inventory.LocksmithWorkbenchContainer;
import com.grim3212.assorted.storage.common.network.SetLockPacket;
import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helpers, constants and fixtures shared by AssortedStorage's gametest classes, which import them statically.
 */
final class StorageTestSupport {

    private StorageTestSupport() {
    }

    static final BlockPos BLOCK = new BlockPos(4, 1, 4);

    static final String CODE = "3212";

    static void assertLockReachesClients(GameTestHelper helper, BlockPos rel, Block block) {
        helper.setBlock(rel, block);
        BlockEntity placed = helper.getLevel().getBlockEntity(helper.absolutePos(rel));
        String name = BuiltInRegistries.BLOCK.getKey(block).toString();
        helper.assertTrue(placed instanceof ILockable, name + " has no lockable block entity");
        ((ILockable) placed).setLockCode(CODE);
        helper.assertTrue(placed.getUpdatePacket() != null, name + " sends clients no update packet");

        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        BlockEntity onClient = placed.getType().create(placed.getBlockPos(), placed.getBlockState());
        helper.assertTrue(onClient instanceof ILockable, name + " did not recreate as a lockable block entity");
        onClient.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, registries, placed.getUpdateTag(registries)));
        helper.assertValueEqual(((ILockable) onClient).getLockCode(), CODE, name + " lock code as a client receives it");
    }

    /**
     * Saves a block entity through a collecting problem reporter and fails the test if anything was
     * refused. A codec that rejects the value it is handed - ItemStack.CODEC on an empty stack, say -
     * only logs "Serialization errors" in game and writes nothing, so a test has to read the report.
     */
    static CompoundTag saveWithoutProblems(GameTestHelper helper, BlockEntity blockEntity, String what) {
        ProblemReporter.Collector problems = new ProblemReporter.Collector();
        TagValueOutput output = TagValueOutput.createWithContext(problems, helper.getLevel().registryAccess());
        blockEntity.saveWithFullMetadata(output);

        helper.assertTrue(problems.isEmpty(), what + " reported serialization errors: " + problems.getReport());
        return output.buildResult();
    }

    /** Every variant object in a blockstate json: each "variants" entry and each multipart "apply". */
    static List<JsonObject> blockstateVariants(JsonObject blockstate) {
        List<JsonObject> out = new ArrayList<>();
        if (blockstate.has("variants")) {
            for (Map.Entry<String, com.google.gson.JsonElement> entry : blockstate.getAsJsonObject("variants").entrySet()) {
                addVariants(entry.getValue(), out);
            }
        }
        if (blockstate.has("multipart")) {
            for (com.google.gson.JsonElement part : blockstate.getAsJsonArray("multipart")) {
                addVariants(part.getAsJsonObject().get("apply"), out);
            }
        }
        return out;
    }

    static void addVariants(com.google.gson.JsonElement element, List<JsonObject> out) {
        if (element == null) {
            return;
        }
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(variant -> out.add(variant.getAsJsonObject()));
        } else {
            out.add(element.getAsJsonObject());
        }
    }

    /** A json off the mod's own classpath, or null if it is not there. */
    static JsonObject json(String path) {
        try (InputStream in = StorageTestSupport.class.getResourceAsStream(path)) {
            return in == null ? null : JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    static String string(JsonObject object, String key) {
        return object != null && object.has(key) ? object.get(key).getAsString() : null;
    }

    /** Every storage block whose block entity keeps an inventory of its own. */
    static List<Block> storageBlocks() {
        List<Block> blocks = new ArrayList<>();
        blocks.add(StorageBlocks.LOCKER.get());
        blocks.add(StorageBlocks.WOOD_CABINET.get());
        blocks.add(StorageBlocks.GLASS_CABINET.get());
        blocks.add(StorageBlocks.GOLD_SAFE.get());
        blocks.add(StorageBlocks.OBSIDIAN_SAFE.get());
        blocks.add(StorageBlocks.ITEM_TOWER.get());
        blocks.add(StorageBlocks.LOCKED_CHEST.get());
        blocks.add(StorageBlocks.LOCKED_SHULKER_BOX.get());
        Collections.addAll(blocks, StorageBlockEntityTypes.getWarehouseCrates());
        return blocks;
    }

    /**
     * One marker item per block under test, so a dropped stack can only have come from one of
     * them. Long enough for every position {@link #spread(int)} hands out.
     */
    static final List<Item> MARKERS = List.of(
            Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT, Items.COPPER_INGOT,
            Items.COAL, Items.REDSTONE, Items.LAPIS_LAZULI, Items.QUARTZ, Items.AMETHYST_SHARD,
            Items.ECHO_SHARD, Items.FLINT, Items.BONE, Items.STRING, Items.PAPER,
            Items.BRICK, Items.CLAY_BALL, Items.SUGAR, Items.APPLE, Items.FEATHER,
            Items.WHEAT, Items.LEATHER, Items.SLIME_BALL, Items.GLOWSTONE_DUST, Items.BLAZE_ROD);

    /** Positions two apart on the test box floor, so nothing a block drops lands on a neighbour. */
    static BlockPos spread(int index) {
        return new BlockPos((index % 5) * 2, 1, (index / 5) * 2);
    }

    static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    /** Right clicks the north face of a door with whatever the player is carrying. */
    static InteractionResult knock(GameTestHelper helper, Player player, BlockPos door) {
        BlockPos pos = helper.absolutePos(door);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false);
        BlockState state = helper.getLevel().getBlockState(pos);
        return state.useItemOn(player.getItemInHand(InteractionHand.MAIN_HAND), helper.getLevel(), player, InteractionHand.MAIN_HAND, hit);
    }

    /** The result of the one crafting recipe that matches {@code input}. */
    static ItemStack crafted(GameTestHelper helper, CraftingInput input) {
        var found = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
        helper.assertTrue(found.isPresent(), "no crafting recipe matched the ingredients laid out");
        return found.get().value().assemble(input);
    }

    /** Puts {@code blank} in the workbench, types {@link #CODE} at it, and takes the result. */
    static ItemStack typeCode(GameTestHelper helper, ServerPlayer player, ItemStack blank) {
        LocksmithWorkbenchContainer menu = (LocksmithWorkbenchContainer) player.containerMenu;
        menu.getSlot(0).set(blank);

        SetLockPacket.handle(new SetLockPacket(CODE), player);
        ItemStack result = menu.getSlot(1).getItem();
        helper.assertFalse(result.isEmpty(), "the workbench made nothing from a typed code");

        menu.getSlot(0).set(ItemStack.EMPTY);
        return result;
    }

    /** The stack, through the codec it goes to disk with. */
    static ItemStack saveAndLoad(GameTestHelper helper, ItemStack stack) {
        RegistryOps<Tag> ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Tag saved = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
        return ItemStack.CODEC.parse(ops, saved).getOrThrow();
    }

    /** Whether a dropped {@code blockItem} is carrying {@code content} in its CONTAINER component. */
    static boolean droppedItemCarries(GameTestHelper helper, Item blockItem, Item content) {
        return helper.getEntities(EntityTypes.ITEM).stream()
                .map(entity -> entity.getItem())
                .filter(stack -> stack.is(blockItem))
                .anyMatch(stack -> {
                    NonNullList<ItemStack> contents = NonNullList.withSize(27, ItemStack.EMPTY);
                    stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(contents);
                    return contents.stream().anyMatch(held -> held.is(content));
                });
    }

    /** The mod's own en_us.json, off the classpath - it is a resource even on a headless server. */
    static JsonObject lang(GameTestHelper helper) {
        try (InputStream in = StorageTestSupport.class.getResourceAsStream("/assets/" + Constants.MOD_ID + "/lang/en_us.json")) {
            helper.assertTrue(in != null, "this mod ships no en_us.json");
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (Exception e) {
            throw helper.assertionException("could not read en_us.json: " + e);
        }
    }

    static boolean resourceExists(String path) {
        try (InputStream in = StorageTestSupport.class.getResourceAsStream(path)) {
            return in != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * A player really in the level, in survival. {@code makeMockServerPlayerInLevel} is deprecated
     * for removal and hard codes creative; {@code makeMockServerPlayer} is never placed, so it has
     * no connection and anything sent to it throws.
     */
    static ServerPlayer survivalPlayer(GameTestHelper helper, ItemStack held) {
        ServerLevel level = helper.getLevel();
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "assortedstorage-test"), false);
        ServerPlayer player = new ServerPlayer(level.getServer(), level, cookie.gameProfile(), cookie.clientInformation());

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        level.getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        helper.runBeforeTestEnd(() -> level.getServer().getPlayerList().remove(player));

        player.setGameMode(GameType.SURVIVAL);
        helper.assertFalse(player.isCreative(), "the test player is in creative, which changes every path under test");
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        return player;
    }

    static LockedEnderChestBlockEntity enderChest(GameTestHelper helper, BlockPos pos, String code) {
        helper.setBlock(pos, StorageBlocks.LOCKED_ENDER_CHEST.get());
        LockedEnderChestBlockEntity chest = helper.getBlockEntity(pos, LockedEnderChestBlockEntity.class);
        chest.setLockCode(code);
        // The saved-data inventory is only bound when the platform handler is first asked for.
        chest.getStorageHandler();
        return chest;
    }

    static Block oakCrate() {
        return StorageBlocks.CRATES.stream().filter(group -> group.getType() == Wood.OAK).findFirst().orElseThrow().SINGLE.get();
    }

    static Block oakCrateQuadruple() {
        return StorageBlocks.CRATES.stream().filter(group -> group.getType() == Wood.OAK).findFirst().orElseThrow().QUADRUPLE.get();
    }

    /** The translation keys of the lines one component adds to a stack's tooltip, in order. */
    static <T extends TooltipProvider> List<String> tooltipKeys(GameTestHelper helper, ItemStack stack, DataComponentType<T> type) {
        List<String> keys = new ArrayList<>();
        stack.addToTooltip(type, Item.TooltipContext.of(helper.getLevel()), TooltipDisplay.DEFAULT, line -> keys.add(tooltipKey(line)), TooltipFlag.NORMAL);
        return keys;
    }

    /** The translation keys of a stack's whole tooltip, as the loader builds it. */
    static List<String> fullTooltipKeys(GameTestHelper helper, ItemStack stack) {
        return stack.getTooltipLines(Item.TooltipContext.of(helper.getLevel()), null, TooltipFlag.NORMAL).stream().map(StorageTestSupport::tooltipKey).toList();
    }

    /** A line's translation key, or its text when it is not translatable. */
    static String tooltipKey(Component line) {
        return line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString();
    }

    /** NeoForge adds mod component tooltip lines on the server too; Fabric only on the client. */
    static boolean onNeoForge() {
        return "Forge".equals(Services.PLATFORM.getPlatformName());
    }
}
