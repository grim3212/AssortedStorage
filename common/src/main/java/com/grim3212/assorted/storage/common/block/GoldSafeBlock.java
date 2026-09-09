package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.blockentity.GoldSafeBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class GoldSafeBlock extends BaseStorageBlock {

    public static final Identifier CONTENTS = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "contents");

    public GoldSafeBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GoldSafeBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ObsidianSafeBlock.SAFE_SHAPE;
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

    @Override
    protected ItemStack getCloneItemStack(LevelReader worldIn, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack itemstack = super.getCloneItemStack(worldIn, pos, state, includeData);
        worldIn.getBlockEntity(pos, StorageBlockEntityTypes.GOLD_SAFE.get()).ifPresent((goldSafeBlockEntity) -> {
            itemstack.applyComponents(goldSafeBlockEntity.collectComponents());
            String lockCode = StorageUtil.getCode(goldSafeBlockEntity);
            StorageUtil.writeCodeToStack(lockCode, itemstack);
        });
        return itemstack;
    }

    @Override
    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof GoldSafeBlockEntity goldSafeBlockEntity) {
            if (!worldIn.isClientSide() && player.isCreative() && (!goldSafeBlockEntity.getItemStackStorageHandler().isEmpty() || goldSafeBlockEntity.isLocked())) {
                ItemStack itemstack = new ItemStack(this);
                itemstack.applyComponents(tileentity.collectComponents());

                String lockCode = StorageUtil.getCode(goldSafeBlockEntity);
                StorageUtil.writeCodeToStack(lockCode, itemstack);

                ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }

        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tileentity instanceof GoldSafeBlockEntity goldSafeBlockEntity) {
            builder = builder.withDynamicDrop(CONTENTS, (stackConsumer) -> {
                for (int i = 0; i < goldSafeBlockEntity.getItemStackStorageHandler().getSlots(); ++i) {
                    stackConsumer.accept(goldSafeBlockEntity.getItemStackStorageHandler().getStackInSlot(i).copy());
                }
            });
        }

        return super.getDrops(state, builder);
    }

}
