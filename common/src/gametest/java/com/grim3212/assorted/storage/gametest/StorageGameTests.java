package com.grim3212.assorted.storage.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedStorage. The tests live in the {@code *Tests} classes; this
 * only lists them.
 */
public final class StorageGameTests {

    private StorageGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        LockTests.register(out);
        StorageBlockTests.register(out);
        CrateTests.register(out);
        MenuTests.register(out);
        BagTests.register(out);
        TooltipTests.register(out);
        AssetTests.register(out);
    }
}
