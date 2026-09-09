package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.api.crates.CrateLayout;
import com.grim3212.assorted.storage.common.block.blockentity.CrateCompactingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrateCompactingBlock extends CrateBlock {

    public CrateCompactingBlock(CrateLayout layout, Block.Properties props) {
        super(null, layout, props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateCompactingBlockEntity(pos, state);
    }

    /**
     * {@code onRemove} split in two: this only fires for a real removal, and the block entity is
     * already gone by now - anything that needed it moved onto the block entity's
     * {@code preRemoveSideEffects}.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel worldIn, BlockPos pos, boolean movedByPiston) {
        worldIn.updateNeighbourForOutputSignal(pos, this);
    }
}
