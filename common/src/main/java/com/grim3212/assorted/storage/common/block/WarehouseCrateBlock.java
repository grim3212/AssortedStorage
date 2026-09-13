package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.storage.api.Wood;
import com.grim3212.assorted.storage.common.block.blockentity.WarehouseCrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WarehouseCrateBlock extends BaseStorageBlock {

    private final Wood type;

    public WarehouseCrateBlock(Wood type, Properties props) {
        super(props);
        this.type = type;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarehouseCrateBlockEntity(pos, state);
    }

    protected boolean isDoorBlocked(LevelAccessor world, BlockPos pos) {
        return isInvalidBlock(world, pos.above());
    }

    public Wood getWoodType() {
        return this.type;
    }

}
