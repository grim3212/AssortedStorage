package com.grim3212.assorted.storage.gametest;

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.grim3212.assorted.lib.test.TestSupport.craft;

/**
 * Helpers, constants and fixtures shared by AssortedStorage's gametest classes, which import them
 * statically, alongside AssortedLib's {@code TestSupport}.
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
        return craft(helper, input, "the ingredients laid out");
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
}
