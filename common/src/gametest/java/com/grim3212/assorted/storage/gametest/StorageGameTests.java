package com.grim3212.assorted.storage.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Automated in-world checks for AssortedStorage.
 * <p>
 * The bodies live in common because the behaviour they check is common; each loader module only
 * registers them into {@code Registries.TEST_FUNCTION} through its own hook, and
 * {@code data/assortedstorage/test_instance/*.json} pairs each one with the shared
 * {@code test_box} structure.
 * <p>
 * The tests themselves are split by feature into the {@code *Tests} classes in this package,
 * with shared helpers in {@code StorageTestSupport}; this only lists them.
 */
public final class StorageGameTests {

    private StorageGameTests() {
    }

    /** Every test in this mod, named once, so both loaders register the same set. */
    public static void forEach(BiConsumer<String, Consumer<GameTestHelper>> out) {
        LockTests.register(out);
        StorageBlockTests.register(out);
        CrateTests.register(out);
        BagTests.register(out);
        TooltipTests.register(out);
        AssetTests.register(out);
    }
}
