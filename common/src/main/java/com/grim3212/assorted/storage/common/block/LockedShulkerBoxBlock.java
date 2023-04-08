package com.grim3212.assorted.storage.common.block;

import com.grim3212.assorted.lib.core.block.IBlockMapColor;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.Constants;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.api.block.IStorageMaterial;
import com.grim3212.assorted.storage.common.block.blockentity.BaseStorageBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.LockedShulkerBoxBlockEntity;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.item.StorageItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity.AnimationStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class LockedShulkerBoxBlock extends Block implements EntityBlock, IStorageMaterial, IBlockMapColor {

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final ResourceLocation CONTENTS = new ResourceLocation(Constants.MOD_ID, "contents");
    private final StorageMaterial material;

    public LockedShulkerBoxBlock(StorageMaterial material) {
        this(material, material.getProps());
    }

    public LockedShulkerBoxBlock(StorageMaterial material, Block.Properties props) {
        super(props.dynamicShape().noOcclusion().isSuffocating(Predicates.isShulkerBlock).isViewBlocking(Predicates.isShulkerBlock));
        this.material = material;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public MaterialColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MaterialColor defaultColor) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof LockedShulkerBoxBlockEntity shulkerBE) {
            return shulkerBE.getColor().getMaterialColor();
        }

        return state.getMapColor(level, pos);
    }

    protected boolean canBeLocked(Level worldIn, BlockPos pos) {
        return !((ILockable) worldIn.getBlockEntity(pos)).isLocked();
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof ILockable) {
            ILockable tileentity = (ILockable) te;

            if (tileentity.isLocked() && !StorageAccessUtil.canAccess(worldIn, pos, player))
                return -1.0F;
        }

        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    public boolean removeLock(Level level, BlockPos pos, Player player) {
        if (this.getStorageMaterial() != null) {
            return BaseStorageBlock.tryRemoveLock(level, pos, player);
        }

        level.playSound(player, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof LockedShulkerBoxBlock && level.getBlockEntity(pos) instanceof LockedShulkerBoxBlockEntity shulkerBE) {
            DyeColor color = shulkerBE.getColor();
            level.setBlock(pos, ShulkerBoxBlock.getBlockByColor(color).defaultBlockState().setValue(ShulkerBoxBlock.FACING, state.getValue(LockedShulkerBoxBlock.FACING)), 3);
            if (level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity newShulkerBE) {
                for (int i = 0; i < shulkerBE.getItemStackStorageHandler().getSlots(); i++) {
                    newShulkerBE.setItem(i, shulkerBE.getItemStackStorageHandler().getStackInSlot(i).copy());
                }

            }
        }
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (this.canBeLocked(level, pos) && player.getItemInHand(hand).getItem() == StorageItems.LOCKSMITH_LOCK.get()) {
            if (BaseStorageBlock.tryPlaceLock(level, pos, player, hand))
                return InteractionResult.SUCCESS;
        }

        if (player.isShiftKeyDown() && StorageAccessUtil.canAccess(level, pos, player)) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof ILockable) {
                ILockable teStorage = (ILockable) tileentity;

                if (teStorage.isLocked()) {
                    ItemStack lockStack = new ItemStack(StorageItems.LOCKSMITH_LOCK.get());
                    CompoundTag tag = new CompoundTag();
                    StorageUtil.writeLock(tag, teStorage.getLockCode());
                    lockStack.setTag(tag);

                    if (removeLock(level, pos, player)) {
                        ItemEntity blockDropped = new ItemEntity(level, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), lockStack);
                        if (!level.isClientSide) {
                            level.addFreshEntity(blockDropped);
                            if (!Services.PLATFORM.isFakePlayer(player)) {
                                blockDropped.playerTouch(player);
                            }
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof LockedShulkerBoxBlockEntity shulkerBE) {
            if (canOpen(state, level, pos, shulkerBE) && StorageAccessUtil.canAccess(level, pos, player)) {
                if (!level.isClientSide) {
                    MenuProvider inamedcontainerprovider = this.getMenuProvider(state, level, pos);
                    if (inamedcontainerprovider != null) {
                        Services.PLATFORM.openMenu((ServerPlayer) player, inamedcontainerprovider, byteBuf -> {
                            byteBuf.writeEnum(this.material);
                            byteBuf.writeBlockPos(pos);
                        });
                        player.awardStat(Stats.OPEN_SHULKER_BOX);
                        PiglinAi.angerNearbyPiglins(player, true);
                    }
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof LockedShulkerBoxBlockEntity shulkerBE) {
            boolean hasColor = stack.hasTag() && stack.getTag().contains("Color");
            if (hasColor) {
                int savedColor = NBTHelper.getInt(stack, "Color");
                shulkerBE.setColor(savedColor == -1 ? null : DyeColor.byId(savedColor));
            }

            ILockable lockeable = (ILockable) level.getBlockEntity(pos);
            if (lockeable != null) {
                lockeable.setLockCode(StorageUtil.getCode(stack));
            }
        }

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public StorageMaterial getStorageMaterial() {
        return material;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LockedShulkerBoxBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        BlockEntity blockentity = worldIn.getBlockEntity(pos);
        return blockentity instanceof LockedShulkerBoxBlockEntity ? Shapes.create(((LockedShulkerBoxBlockEntity) blockentity).getBoundingBox(state)) : Shapes.block();
    }

    private static boolean canOpen(BlockState state, Level level, BlockPos pos, LockedShulkerBoxBlockEntity shulker) {
        if (shulker.getAnimationStatus() != AnimationStatus.CLOSED) {
            return true;
        } else {
            AABB aabb = Shulker.getProgressDeltaAabb(state.getValue(FACING), 0.0F, 0.5F).move(pos).deflate(1.0E-6D);
            return level.noCollision(aabb);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState neighborState, boolean p_56238_) {
        if (!state.is(neighborState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof LockedShulkerBoxBlockEntity shulkerBE) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }

            super.onRemove(state, level, pos, neighborState, p_56238_);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state) {
        ItemStack itemstack = super.getCloneItemStack(worldIn, pos, state);
        worldIn.getBlockEntity(pos, StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get()).ifPresent((shulkerBE) -> {
            shulkerBE.saveToItem(itemstack);
            NBTHelper.putInt(itemstack, "Color", shulkerBE.colorToSave());
            String lockCode = StorageUtil.getCode(shulkerBE);
            StorageUtil.writeCodeToStack(lockCode, itemstack);
        });
        return itemstack;
    }

    @Override
    public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LockedShulkerBoxBlockEntity shulkerBE) {
            if (!worldIn.isClientSide && player.isCreative() && !shulkerBE.getItemStackStorageHandler().isEmpty()) {
                ItemStack itemstack = new ItemStack(this);
                tileentity.saveToItem(itemstack);
                NBTHelper.putInt(itemstack, "Color", shulkerBE.colorToSave());

                if (shulkerBE.hasCustomName()) {
                    itemstack.setHoverName(shulkerBE.getCustomName());
                }

                String lockCode = StorageUtil.getCode(shulkerBE);
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
        if (tileentity instanceof LockedShulkerBoxBlockEntity shulkerBE) {
            builder = builder.withDynamicDrop(CONTENTS, (context, stackConsumer) -> {
                for (int i = 0; i < shulkerBE.getItemStackStorageHandler().getSlots(); ++i) {
                    stackConsumer.accept(shulkerBE.getItemStackStorageHandler().getStackInSlot(i).copy());
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

        tooltip.add(Component.translatable(Constants.MOD_ID + ".info.level_upgrade_level", Component.literal("" + (material == null ? 0 : material.getStorageLevel())).withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));

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

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity tileentity = world.getBlockEntity(pos);
        return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.triggerEvent(id, param);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BaseStorageBlockEntity storageBlockEntity) {
            return BaseStorageBlock.getRedstoneSignalFromContainer(storageBlockEntity.getItemStackStorageHandler());
        }

        return super.getAnalogOutputSignal(state, level, pos);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, StorageBlockEntityTypes.LOCKED_SHULKER_BOX.get(), LockedShulkerBoxBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> entityType, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> entityTicker) {
        return p_152134_ == entityType ? (BlockEntityTicker<A>) entityTicker : null;
    }
}
