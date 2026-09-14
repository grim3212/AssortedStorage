package com.grim3212.assorted.storage.gametest;

import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.LockerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.storage.gametest.StorageTestSupport.*;

/**
 * Lockers: the door animation, which rides on the count of players who have one open.
 */
final class LockerTests {

    private LockerTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("a_locker_in_use_counts_the_player_using_it", LockerTests::aLockerInUseCountsThePlayerUsingIt);
        out.accept("a_locker_door_closes_again_after_a_long_look", LockerTests::aLockerDoorClosesAgainAfterALongLook);
    }

    /** A locker placed at {@link StorageTestSupport#BLOCK} with the player stood next to it. */
    private static LockerBlockEntity lockerWithPlayerNearby(GameTestHelper helper, ServerPlayer player) {
        helper.setBlock(BLOCK, StorageBlocks.LOCKER.get());
        Vec3 beside = Vec3.atCenterOf(helper.absolutePos(BLOCK)).add(1.0D, 0.0D, 0.0D);
        player.teleportTo(helper.getLevel(), beside.x, beside.y, beside.z, java.util.Set.of(), player.getYRot(), player.getXRot(), false);
        return helper.getBlockEntity(BLOCK, LockerBlockEntity.class);
    }

    /**
     * The 200 tick recount of who has a block open has to find the player who has it open; a menu it
     * does not recognise reads as nobody and the count drifts.
     */
    private static void aLockerInUseCountsThePlayerUsingIt(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        LockerBlockEntity locker = lockerWithPlayerNearby(helper, player);
        BlockPos abs = helper.absolutePos(BLOCK);

        helper.assertValueEqual(locker.getNumberOfPlayersUsing(helper.getLevel(), locker, abs.getX(), abs.getY(), abs.getZ()), 0,
                "the count of players using a locker nobody has open");

        player.containerMenu = locker.createMenu(1, player.getInventory(), player);
        helper.assertValueEqual(locker.getNumberOfPlayersUsing(helper.getLevel(), locker, abs.getX(), abs.getY(), abs.getZ()), 1,
                "the count of players using a locker one player has open");
        helper.succeed();
    }

    /**
     * A locker held open long enough for that recount to run still closes afterwards. Below zero the
     * door animation stops running at all.
     */
    private static void aLockerDoorClosesAgainAfterALongLook(GameTestHelper helper) {
        ServerPlayer player = survivalPlayer(helper);
        LockerBlockEntity locker = lockerWithPlayerNearby(helper, player);

        // The container's constructor is what opens the locker, so this is the whole of opening one.
        AbstractContainerMenu menu = locker.createMenu(1, player.getInventory(), player);
        player.containerMenu = menu;
        helper.assertValueEqual(locker.numPlayersUsing, 1, "the open count once a player opened a locker");

        // Long enough to be sure the once-every-200-ticks recount has run.
        for (int tick = 0; tick < 200; tick++) {
            locker.tick();
        }

        helper.assertValueEqual(locker.numPlayersUsing, 1, "the open count while the player still has the locker open");

        player.containerMenu = player.inventoryMenu;
        menu.removed(player);
        helper.assertValueEqual(locker.numPlayersUsing, 0, "the open count after the player closed the locker");

        // The animation only runs at zero or above, so a negative count leaves the door where it is.
        for (int tick = 0; tick < 20; tick++) {
            locker.tick();
        }

        helper.assertValueEqual(locker.getRotation(1.0F), 0.0F, "how far open the locker door is once it has closed");
        helper.succeed();
    }
}
