package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.common.crafting.BagColoringRecipe;
import com.grim3212.assorted.storage.common.inventory.bag.BagItemHandler;
import com.grim3212.assorted.storage.common.inventory.enderbag.EnderBagContainer;
import com.grim3212.assorted.storage.common.item.BagItem;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.storage.gametest.StorageTestSupport.*;

/**
 * Bags and the ender bag: dye, contents across save/load, and the player's ender chest.
 */
final class BagTests {

    private BagTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("bag_dyes_and_reads_back_colour", BagTests::bagDyesAndReadsBackColour);
        out.accept("bag_holds_items_across_save_load", BagTests::bagHoldsItemsAcrossSaveLoad);
        out.accept("ender_bag_opens_the_players_ender_chest", BagTests::enderBagOpensThePlayersEnderChest);
    }

    /**
     * A dye on each side of the bag paints both halves. Which half a dye lands on is decided by its
     * column relative to the bag's, and the colours are read back off the stack's CUSTOM_DATA -
     * bags lost their free form stack NBT in the port.
     */
    private static void bagDyesAndReadsBackColour(GameTestHelper helper) {
        ItemStack bag = new ItemStack(StorageItems.BAG.get());
        ItemStack red = new ItemStack(Items.DYE.pick(DyeColor.RED));
        ItemStack blue = new ItemStack(Items.DYE.pick(DyeColor.BLUE));

        CraftingInput input = CraftingInput.of(3, 1, List.of(red, bag, blue));
        helper.assertTrue(BagColoringRecipe.INSTANCE.matches(input, helper.getLevel()), "dye + bag + dye was not a bag colouring recipe");

        ItemStack dyed = BagColoringRecipe.INSTANCE.assemble(input);
        helper.assertValueEqual(NBTHelper.getInt(dyed, BagItem.TAG_SECONDARY_COLOR, -1), DyeColor.RED.getId(), "secondary colour from the dye left of the bag");
        helper.assertValueEqual(NBTHelper.getInt(dyed, BagItem.TAG_PRIMARY_COLOR, -1), DyeColor.BLUE.getId(), "primary colour from the dye right of the bag");

        // The name is derived from the primary colour, and it is per stack now that
        // getDescriptionId lost its stack overload.
        Component name = StorageItems.BAG.get().getName(dyed);
        helper.assertTrue(name.getContents() instanceof TranslatableContents contents && contents.getKey().endsWith("_blue"),
                "a dyed bag did not take its name from the primary colour");

        helper.assertValueEqual(NBTHelper.getInt(bag, BagItem.TAG_PRIMARY_COLOR, -1), -1, "an undyed bag carried a colour");
        helper.succeed();
    }

    /**
     * A bag holds items and keeps them across a save/load. Its contents are a
     * {@code DataComponents.CONTAINER} component now rather than free form stack NBT, so the round
     * trip goes through {@code ItemStack.CODEC} - the same path the stack takes to disk.
     */
    private static void bagHoldsItemsAcrossSaveLoad(GameTestHelper helper) {
        ItemStack bag = new ItemStack(StorageItems.BAG.get());

        // Exactly what BagItem#getStorageHandler builds for the stack.
        BagItemHandler handler = new BagItemHandler(bag, null);
        handler.load();
        helper.assertValueEqual(handler.getSlots(), 28, "a plain bag's slot count, lock slot included");

        int last = handler.getSlots() - 1;
        handler.setStackInSlot(1, new ItemStack(Items.DIAMOND, 5));
        handler.setStackInSlot(last, new ItemStack(Items.EMERALD, 2));

        ItemStack reloaded = saveAndLoad(helper, bag);
        helper.assertTrue(reloaded.is(StorageItems.BAG.get()), "a saved bag did not load back as a bag");

        BagItemHandler after = new BagItemHandler(reloaded, null);
        after.load();
        helper.assertTrue(after.getStackInSlot(1).is(Items.DIAMOND), "the bag lost its first item across a save/load");
        helper.assertValueEqual(after.getStackInSlot(1).getCount(), 5, "the bag's first stack size across a save/load");
        helper.assertTrue(after.getStackInSlot(last).is(Items.EMERALD), "the bag lost its last item across a save/load");
        helper.assertTrue(after.getStackInSlot(0).isEmpty(), "an unlocked bag came back with something in its lock slot");
        helper.succeed();
    }

    /**
     * An ender bag with no code on it opens the player's own vanilla ender chest, rather than one
     * of the mod's code keyed shared inventories. Checked on the container the menu resolves to,
     * which is the decision; the screen on top of it is a manual check.
     */
    private static void enderBagOpensThePlayersEnderChest(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper, new ItemStack(StorageItems.ENDER_BAG.get()));
        helper.assertFalse(StorageUtil.hasCode(player.getMainHandItem()), "a fresh ender bag already carried a code");

        PlayerEnderChestContainer ownChest = player.getEnderChestInventory();
        ownChest.setItem(0, new ItemStack(Items.DIAMOND, 3));

        EnderBagContainer menu = new EnderBagContainer(1, helper.getLevel(), player.blockPosition(), player.getInventory(), player);
        helper.runBeforeTestEnd(() -> menu.removed(player));

        // One padlock slot, the 27 chest slots, then the player's own 36.
        helper.assertValueEqual(menu.slots.size(), 1 + 27 + 36, "the ender bag menu's slot count");
        helper.assertTrue(menu.getSlot(1).container == ownChest, "an uncoded ender bag did not resolve to the player's own ender chest");
        helper.assertTrue(menu.getSlot(1).getItem().is(Items.DIAMOND), "the ender bag did not show what was in the player's ender chest");
        helper.assertValueEqual(menu.getSlot(1).getItem().getCount(), 3, "the stack the ender bag showed from the player's ender chest");
        helper.succeed();
    }
}
