package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.LargeItemStack;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.client.blockentity.state.CrateRenderState;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.CrateBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import com.grim3212.assorted.storage.common.inventory.crates.CompactingCrateInventory;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.lib.client.screen.LibGuiItemRenderer;
import com.grim3212.assorted.storage.client.screen.CrateScreen;
import com.grim3212.assorted.storage.client.screen.buttons.ImageToggleButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * What only a client can check: tooltips as Fabric builds them, menus arriving through Fabric's
 * packet path, and the crate renderer. Run with {@code ./gradlew :fabric:runClientGameTest}; it
 * exits non-zero on a failure.
 */
public class StorageClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        // Inside a world: an ItemStack cannot be made on the title screen, because an item's default
        // components are only bound once a world's registries have loaded.
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            tooltipsShowStorageInfo(context);
            menusOpenOnTheClient(context, world);
            crateRendersFromItsState(context, world);
            crateScreenShowsItsFace(context, world);
            compactingCrateTiersReachTheClient(context, world);
        }
    }

    private static void tooltipsShowStorageInfo(ClientGameTestContext context) {
        context.runOnClient(client -> {
            List<String> bag = tooltipKeys(client, StorageUtil.setCodeOnStack("1234", new ItemStack(StorageItems.BAG.get())));
            check(bag.contains(Constants.MOD_ID + ".info.locked") && bag.contains(Constants.MOD_ID + ".info.level_upgrade_level"), "a locked bag's tooltip is " + bag);

            List<String> upgrade = tooltipKeys(client, new ItemStack(StorageItems.AMOUNT_UPGRADE.get()));
            check(upgrade.contains(Constants.MOD_ID + ".info.upgrade.mode"), "an amount upgrade's tooltip is " + upgrade);
        });
    }

    /**
     * The server opens each menu for the client's own player, and the client builds the same menu
     * from what arrives: menus with data (a material, a position) and a plain vanilla one.
     */
    private static void menusOpenOnTheClient(ClientGameTestContext context, TestSingleplayerContext world) {
        List<Block> blocks = List.of(StorageBlocks.CHESTS.get(StorageMaterial.GOLD).get(), StorageBlocks.LOCKED_CHEST.get(),
                oakCrate(), StorageBlocks.ITEM_TOWER.get(), StorageBlocks.WOOD_CABINET.get());

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            int offset = i * 2;
            BlockPos pos = world.getServer().computeOnServer(server -> {
                ServerPlayer player = player(server);
                BlockPos at = player.blockPosition().offset(2, 0, offset - 4);
                player.level().setBlockAndUpdate(at, block.defaultBlockState());
                return at;
            });
            context.waitFor(client -> client.level != null && client.level.getBlockState(pos).is(block));

            Opened opened = world.getServer().computeOnServer(server -> {
                ServerPlayer player = player(server);
                ServerLevel level = player.level();
                MenuProvider provider = level.getBlockState(pos).getMenuProvider(level, pos);
                Services.PLATFORM.openMenu(player, provider);
                AbstractContainerMenu menu = player.containerMenu;
                return new Opened(menu.containerId, menu.getClass(), menu.slots.size());
            });
            context.waitFor(client -> client.player.containerMenu.containerId == opened.containerId());
            context.runOnClient(client -> {
                AbstractContainerMenu menu = client.player.containerMenu;
                String name = block.getName().getString();
                check(menu.getClass() == opened.type(), name + " opened a " + menu.getClass().getSimpleName() + " on the client, not a " + opened.type().getSimpleName());
                check(menu.slots.size() == opened.slots(), name + " has " + menu.slots.size() + " slots on the client and " + opened.slots() + " on the server");
            });

            world.getServer().runOnServer(server -> player(server).closeContainer());
            context.waitFor(client -> client.player.containerMenu == client.player.inventoryMenu);
        }
    }

    /**
     * The crate renderer extracts everything its upgrade renderers draw, and the submit pass reads
     * only that state: after extraction the amount is changed on the state alone, and the amount
     * upgrade draws the changed number.
     */
    private static void crateRendersFromItsState(ClientGameTestContext context, TestSingleplayerContext world) {
        BlockPos pos = world.getServer().computeOnServer(server -> {
            ServerPlayer player = player(server);
            ServerLevel level = player.level();
            BlockPos at = player.blockPosition().offset(-2, 0, 0);
            level.setBlockAndUpdate(at, oakCrate().defaultBlockState());
            CrateBlockEntity crate = (CrateBlockEntity) level.getBlockEntity(at);
            CrateSidedInv inventory = crate.getItemStackStorageHandler();
            // Capacity comes from the item already in the slot, so the item goes in before the amount.
            inventory.setItem(0, new LargeItemStack(new ItemStack(Items.COBBLESTONE), 1));
            inventory.setItem(0, new LargeItemStack(new ItemStack(Items.COBBLESTONE), 100));
            // Enhancement slot 0 is the lock.
            inventory.getEnhancements().set(2, new ItemStack(StorageItems.AMOUNT_UPGRADE.get()));
            inventory.getEnhancements().set(3, new ItemStack(StorageItems.GLOW_UPGRADE.get()));
            crate.setChanged();
            level.sendBlockUpdated(at, crate.getBlockState(), crate.getBlockState(), Block.UPDATE_ALL);
            return at;
        });
        try {
            context.waitFor(client -> client.level != null && client.level.getBlockEntity(pos) instanceof CrateBlockEntity crate
                    && crate.getItemStackStorageHandler().getLargeItemStack(0).getAmount() == 100
                    && crate.getItemStackStorageHandler().hasGlowUpgrade(), 100);
        } catch (AssertionError timedOut) {
            String client = context.computeOnClient(mc -> describeCrate(mc.level.getBlockEntity(pos)));
            String server = world.getServer().computeOnServer(s -> describeCrate(player(s).level().getBlockEntity(pos)));
            throw new AssertionError("the client never saw the filled crate: client " + client + ", server " + server, timedOut);
        }

        context.runOnClient(client -> {
            CrateBlockEntity crate = (CrateBlockEntity) client.level.getBlockEntity(pos);
            BlockEntityRenderer<CrateBlockEntity, CrateRenderState> renderer = client.getBlockEntityRenderDispatcher().getRenderer(crate);
            CrateRenderState state = renderer.createRenderState();
            renderer.extractRenderState(crate, state, 0.0F, Vec3.ZERO, null);

            check(state.slotAmounts.length > 0 && state.slotAmounts[0] == 100, "the extracted amount in slot 0 is not 100");
            int capacity = crate.getItemStackStorageHandler().getMaxStackSizeForSlot(0);
            check(state.slotCapacities[0] == capacity, "the extracted capacity of slot 0 is " + state.slotCapacities[0] + ", not " + capacity);
            check(state.itemLightCoords == LightCoordsUtil.FULL_BRIGHT, "a crate with a glow upgrade extracted item light " + state.itemLightCoords);
            check(state.upgrades.stream().anyMatch(stack -> stack.is(StorageItems.AMOUNT_UPGRADE.get())), "the amount upgrade was not extracted: " + state.upgrades);

            state.slotAmounts[0] = 7;
            List<String> texts = new ArrayList<>();
            renderer.submit(state, new PoseStack(), recordingText(texts), new CameraRenderState());
            check(texts.equals(List.of("7")), "the amount upgrade drew " + texts + " from a state holding 7");
        });
    }

    /**
     * The crate screen draws the crate's face, not the three-quarter view an item icon gets: the
     * slots it draws on top belong to that face. Only the model identity says which view was asked
     * for - a layer's transform can be set but never read back - and it is also what keeps this
     * drawing apart from the crate's inventory icon in the GUI item atlas. The lock buttons are
     * checked here too, since a sprite missing from the atlas draws as the missing texture silently.
     */
    private static void crateScreenShowsItsFace(ClientGameTestContext context, TestSingleplayerContext world) {
        BlockPos pos = world.getServer().computeOnServer(server -> {
            ServerPlayer player = player(server);
            ServerLevel level = player.level();
            BlockPos at = player.blockPosition().offset(0, 0, -3);
            level.setBlockAndUpdate(at, oakCrate().defaultBlockState());
            CrateBlockEntity crate = (CrateBlockEntity) level.getBlockEntity(at);
            // Capacity comes from the item already in the slot, so the item goes in before the amount.
            crate.getItemStackStorageHandler().setItem(0, new LargeItemStack(new ItemStack(Items.COBBLESTONE), 1));
            crate.getItemStackStorageHandler().setItem(0, new LargeItemStack(new ItemStack(Items.COBBLESTONE), 191));
            crate.setChanged();
            level.sendBlockUpdated(at, crate.getBlockState(), crate.getBlockState(), Block.UPDATE_ALL);
            return at;
        });
        // The screen builds its lock buttons in init(), so the client has to have the contents first.
        context.waitFor(client -> client.level != null && client.level.getBlockEntity(pos) instanceof CrateBlockEntity crate
                && crate.getItemStackStorageHandler().getLargeItemStack(0).getAmount() == 191, 100);

        world.getServer().runOnServer(server -> {
            ServerPlayer player = player(server);
            ServerLevel level = player.level();
            Services.PLATFORM.openMenu(player, level.getBlockState(pos).getMenuProvider(level, pos));
        });
        context.waitForScreen(CrateScreen.class);

        context.runOnClient(client -> {
            CrateScreen screen = (CrateScreen) client.gui.screen();

            long lockButtons = screen.children().stream().filter(ImageToggleButton.class::isInstance).count();
            check(lockButtons == 1, "a filled single crate's screen has " + lockButtons + " lock buttons");

            GuiRenderState renderState = new GuiRenderState();
            GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(client, renderState, 0, 0);

            TextureAtlas guiAtlas = client.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);
            for (Identifier sprite : ImageToggleButton.SPRITES) {
                TextureAtlasSprite inAtlas = guiAtlas.getSprite(sprite);
                check(!inAtlas.contents().name().equals(MissingTextureAtlasSprite.getLocation()), "the lock button's " + sprite + " is not in the GUI atlas");
            }

            screen.extractBackground(graphics, 0, 0, 0.0F);
            List<GuiItemRenderState> items = new ArrayList<>();
            renderState.forEachItem(items::add);
            check(items.size() == 1, "the crate screen's background drew " + items.size() + " items, not just the crate preview");

            Object identity = items.getFirst().itemStackRenderState().getModelIdentity();
            check(identity instanceof List<?> elements
                            && elements.contains(ItemDisplayContext.NONE)
                            && elements.contains(LibGuiItemRenderer.facingViewer(180.0F)),
                    "the crate preview was not drawn facing the viewer: " + identity);
        });

        world.getServer().runOnServer(server -> player(server).closeContainer());
        context.waitFor(client -> client.gui.screen() == null);
    }

    /**
     * A compactor works its tiers out of the recipe manager, which is server side only, and every
     * capacity it reports comes from them. A client that watched the crate being filled has to end
     * up with the same three tiers as the server, not just the ones it could cap for itself.
     */
    private static void compactingCrateTiersReachTheClient(ClientGameTestContext context, TestSingleplayerContext world) {
        BlockPos pos = world.getServer().computeOnServer(server -> {
            ServerPlayer player = player(server);
            BlockPos at = player.blockPosition().offset(-4, 0, 0);
            player.level().setBlockAndUpdate(at, StorageBlocks.CRATE_COMPACTING.get().defaultBlockState());
            return at;
        });
        // The client has to see the crate placed and empty first: filling it afterwards is what the
        // player does, and what leaves the client with the empty tier list it was placed with.
        context.waitFor(client -> client.level != null && client.level.getBlockEntity(pos) instanceof CrateCompactingBlockEntity, 100);

        world.getServer().runOnServer(server -> {
            CrateCompactingBlockEntity crate = (CrateCompactingBlockEntity) player(server).level().getBlockEntity(pos);
            crate.getItemStackStorageHandler().addItem(0, new ItemStack(Items.IRON_INGOT, 9));
        });
        try {
            context.waitFor(client -> client.level.getBlockEntity(pos) instanceof CrateCompactingBlockEntity crate
                    && crate.getItemStackStorageHandler().getLargeItemStack(2).getAmount() == 81, 100);
        } catch (AssertionError timedOut) {
            String seen = context.computeOnClient(client -> describeCrate(client.level.getBlockEntity(pos)));
            throw new AssertionError("the client never saw the compactor's lowest tier: " + seen, timedOut);
        }

        context.runOnClient(client -> {
            CompactingCrateInventory compactor = (CompactingCrateInventory) ((CrateCompactingBlockEntity) client.level.getBlockEntity(pos)).getItemStackStorageHandler();
            check(compactor.getLargeItemStack(0).getStack().is(Items.IRON_BLOCK), "the client's block tier is " + compactor.getLargeItemStack(0).getStack());
            check(compactor.getLargeItemStack(1).getStack().is(Items.IRON_INGOT), "the client's ingot tier is " + compactor.getLargeItemStack(1).getStack());
            check(compactor.getLargeItemStack(2).getStack().is(Items.IRON_NUGGET), "the client's nugget tier is " + compactor.getLargeItemStack(2).getStack());
            // Capacity is read for the slot tooltip and by the amount upgrade, and is worked out
            // from the tiers, so a client without them reports nothing for the lower ones.
            check(compactor.getMaxStackSizeForSlot(2) > 0, "the client's compactor reports no capacity for the nugget tier");
            check(compactor.getMaxStackSizeForSlot(1) > 0, "the client's compactor reports no capacity for the ingot tier");
        });
    }

    private record Opened(int containerId, Class<?> type, int slots) {
    }

    private static String describeCrate(Object blockEntity) {
        if (!(blockEntity instanceof CrateBlockEntity crate)) {
            return "no crate (" + blockEntity + ")";
        }
        CrateSidedInv inventory = crate.getItemStackStorageHandler();
        return "slot 0 " + inventory.getLargeItemStack(0).getStack() + " x" + inventory.getLargeItemStack(0).getAmount() + ", enhancements " + inventory.getEnhancements();
    }

    private static ServerPlayer player(MinecraftServer server) {
        return server.getPlayerList().getPlayers().getFirst();
    }

    private static Block oakCrate() {
        return StorageBlocks.CRATES.stream().filter(group -> group.getType() == Wood.OAK).findFirst().orElseThrow().SINGLE.get();
    }

    /** A collector that keeps the text of every {@code submitText} and drops everything else. */
    private static SubmitNodeCollector recordingText(List<String> texts) {
        return (SubmitNodeCollector) Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "submitText" -> {
                    StringBuilder text = new StringBuilder();
                    ((FormattedCharSequence) args[3]).accept((index, style, codePoint) -> {
                        text.appendCodePoint(codePoint);
                        return true;
                    });
                    texts.add(text.toString());
                }
                case "order" -> {
                    return proxy;
                }
                case "hashCode" -> {
                    return System.identityHashCode(proxy);
                }
                case "equals" -> {
                    return proxy == args[0];
                }
                case "toString" -> {
                    return "RecordingSubmitNodeCollector";
                }
                default -> {
                }
            }
            Class<?> returned = method.getReturnType();
            return returned == boolean.class ? false : returned.isPrimitive() && returned != void.class ? 0 : null;
        });
    }

    private static List<String> tooltipKeys(Minecraft client, ItemStack stack) {
        return stack.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL).stream()
                .map(line -> line.getContents() instanceof TranslatableContents translatable ? translatable.getKey() : line.getString())
                .toList();
    }

    private static void check(boolean ok, String message) {
        if (!ok) {
            throw new AssertionError(message);
        }
    }
}
