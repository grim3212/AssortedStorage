package com.grim3212.assorted.storage.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;

/**
 * A locked copper door, which oxidises on its own like the vanilla door it stands in for. The four
 * waxed copper doors get a plain {@link LockedDoorBlock} instead, because a waxed door never
 * changes.
 * <p>
 * The lock survives each step: {@link LockedDoorBlock#shouldChangedStateKeepBlockEntity} keeps the
 * block entity, and {@link LockedDoorBlock#updateShape} carries the change to the other half.
 * Scraping with an axe and waxing with a honeycomb are vanilla's own item code rather than
 * anything here, and reach these blocks through the loaders' oxidation registries - NeoForge's
 * {@code neoforge:oxidizables} and {@code neoforge:waxables} data maps, written by
 * {@code StorageDataMapProvider}, and Fabric's {@code OxidizableBlocksRegistry}, called from
 * {@code AssortedStorageFabric}.
 */
public class LockedCopperDoorBlock extends LockedDoorBlock implements WeatheringCopper {

    private final WeatherState age;

    public LockedCopperDoorBlock(DoorBlock parent, WeatherState age, Properties builder) {
        super(parent, builder);
        this.age = age;
    }

    @Override
    public WeatherState getAge() {
        return this.age;
    }

    /**
     * The locked door of the next oxidation stage. Worked out from this block's own stage rather
     * than read back out of {@link WeatheringCopper#NEXT_BY_BLOCK}, because on both loaders that map
     * is filled from data that loads after the blocks are built - an empty read there would leave
     * the door simply never oxidising, with nothing logged.
     */
    @Override
    public Optional<BlockState> getNext(BlockState state) {
        if (this.age == WeatherState.OXIDIZED) {
            return Optional.empty();
        }

        Block next = Blocks.COPPER_DOOR.weathering().pick(this.age.next());
        return Optional.of(StorageBlocks.VANILLA_DOORS.get(next).get().withPropertiesOf(state));
    }

    /**
     * Only the lower half oxidises, as vanilla's copper door does. Both halves are randomly ticked,
     * so ticking both would roll the chance twice and oxidise at double the rate; the upper half
     * follows the lower through {@link LockedDoorBlock#updateShape}.
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            this.changeOverTime(state, level, pos, random);
        }
    }
}
