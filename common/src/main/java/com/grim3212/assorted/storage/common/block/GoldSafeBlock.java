package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.common.block.blockentity.GoldSafeBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class GoldSafeBlock extends BaseStorageBlock {

    public static final ResourceLocation CONTENTS = new ResourceLocation(Constants.MOD_ID, "contents");

    public GoldSafeBlock(Properties properties) {
        super(properties.requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GoldSafeBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return ObsidianSafeBlock.SAFE_SHAPE;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof GoldSafeBlockEntity) {
                GoldSafeBlockEntity goldsafetileentity = (GoldSafeBlockEntity) tileentity;

                if (goldsafetileentity.isLocked()) {
                    ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
                    CompoundTag tag = new CompoundTag();
                    StorageUtil.writeLock(tag, goldsafetileentity.getLockCode());
                    lockStack.setTag(tag);
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), lockStack);
                }

                worldIn.updateNeighbourForOutputSignal(pos, state.getBlock());
            }

            if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
                worldIn.removeBlockEntity(pos);
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack itemstack = super.getCloneItemStack(worldIn, pos, state);
        worldIn.getBlockEntity(pos, StorageBlockEntityTypes.GOLD_SAFE.get()).ifPresent((goldSafeBlockEntity) -> {
            goldSafeBlockEntity.saveToItem(itemstack);
            String lockCode = StorageUtil.getCode(goldSafeBlockEntity);
            StorageUtil.writeCodeToStack(lockCode, itemstack);
        });
        return itemstack;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof GoldSafeBlockEntity goldSafeBlockEntity) {
            if (!worldIn.isClientSide && player.isCreative() && !goldSafeBlockEntity.getItemStackStorageHandler().isEmpty()) {
                ItemStack itemstack = new ItemStack(this);
                tileentity.saveToItem(itemstack);

                if (goldSafeBlockEntity.hasCustomName()) {
                    itemstack.setHoverName(goldSafeBlockEntity.getCustomName());
                }

                String lockCode = StorageUtil.getCode(goldSafeBlockEntity);
                StorageUtil.writeCodeToStack(lockCode, itemstack);

                ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tileentity instanceof GoldSafeBlockEntity goldSafeBlockEntity) {
            builder = builder.withDynamicDrop(CONTENTS, (context, stackConsumer) -> {
                for (int i = 0; i < goldSafeBlockEntity.getItemStackStorageHandler().getSlots(); ++i) {
                    stackConsumer.accept(goldSafeBlockEntity.getItemStackStorageHandler().getStackInSlot(i).copy());
                }
            });
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        String code = StorageUtil.getCode(stack);

        if (!code.isEmpty()) {
            tooltip.add(Component.translatable(Constants.MOD_ID + ".info.combo", Component.literal(code).withStyle(ChatFormatting.AQUA)));
        }

        CompoundTag compoundnbt = stack.getTagElement("BlockEntityTag");
        if (compoundnbt != null && compoundnbt.contains("Inventory", Tag.TAG_COMPOUND)) {
            CompoundTag inventory = compoundnbt.getCompound("Inventory");
            if (inventory.contains("Items", Tag.TAG_LIST)) {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(inventory, nonnulllist);
                int i = 0;
                int j = 0;

                for (ItemStack itemstack : nonnulllist) {
                    if (!itemstack.isEmpty()) {
                        ++j;
                        if (i <= 4) {
                            ++i;
                            MutableComponent iformattabletextcomponent = itemstack.getHoverName().copy();
                            iformattabletextcomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                            tooltip.add(iformattabletextcomponent);
                        }
                    }
                }

                if (j - i > 0) {
                    tooltip.add((Component.translatable("container.shulkerBox.more", j - i)).withStyle(ChatFormatting.ITALIC));
                }
            }
        }
    }
}
