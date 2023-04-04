package com.grim3212.assorted.storage.common.item;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.ILoaderRegistry;
import com.grim3212.assorted.lib.util.LibCommonTags;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.block.LockedBarrelBlock;
import com.grim3212.assorted.storage.common.block.LockedHopperBlock;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Map;


public class PadlockItem extends CombinationItem {

    private final Map<Block, Block> lockMappings;

    public PadlockItem(Properties properties) {
        super(properties);

        // Populate map so that we can properly map unlocked doors to locked doors
        this.lockMappings = Maps.newHashMap();
        lockMappings.put(Blocks.OAK_DOOR, StorageBlocks.LOCKED_OAK_DOOR.get());
        lockMappings.put(Blocks.SPRUCE_DOOR, StorageBlocks.LOCKED_SPRUCE_DOOR.get());
        lockMappings.put(Blocks.BIRCH_DOOR, StorageBlocks.LOCKED_BIRCH_DOOR.get());
        lockMappings.put(Blocks.ACACIA_DOOR, StorageBlocks.LOCKED_ACACIA_DOOR.get());
        lockMappings.put(Blocks.JUNGLE_DOOR, StorageBlocks.LOCKED_JUNGLE_DOOR.get());
        lockMappings.put(Blocks.DARK_OAK_DOOR, StorageBlocks.LOCKED_DARK_OAK_DOOR.get());
        lockMappings.put(Blocks.CRIMSON_DOOR, StorageBlocks.LOCKED_CRIMSON_DOOR.get());
        lockMappings.put(Blocks.MANGROVE_DOOR, StorageBlocks.LOCKED_MANGROVE_DOOR.get());
        lockMappings.put(Blocks.WARPED_DOOR, StorageBlocks.LOCKED_WARPED_DOOR.get());
        lockMappings.put(Blocks.IRON_DOOR, StorageBlocks.LOCKED_IRON_DOOR.get());
        lockMappings.put(Blocks.ENDER_CHEST, StorageBlocks.LOCKED_ENDER_CHEST.get());
        lockMappings.put(Blocks.CHEST, StorageBlocks.LOCKED_CHEST.get());
        lockMappings.put(Blocks.BARREL, StorageBlocks.LOCKED_BARREL.get());
        lockMappings.put(Blocks.HOPPER, StorageBlocks.LOCKED_HOPPER.get());

        lockMappings.put(Blocks.SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.WHITE_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.ORANGE_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.MAGENTA_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.LIGHT_BLUE_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.YELLOW_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.LIME_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.PINK_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.GRAY_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.LIGHT_GRAY_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.CYAN_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.PURPLE_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.BLUE_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.BROWN_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.GREEN_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.RED_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());
        lockMappings.put(Blocks.BLACK_SHULKER_BOX, StorageBlocks.LOCKED_SHULKER_BOX.get());

        ILoaderRegistry<Block> blockRegistry = Services.PLATFORM.getRegistry(Registries.BLOCK);

        Block quartzDoor = blockRegistry.getValue(new ResourceLocation("assorteddecor:quartz_door")).orElse(Blocks.AIR);
        if (quartzDoor != Blocks.AIR) {
            lockMappings.put(quartzDoor, StorageBlocks.LOCKED_QUARTZ_DOOR.get());
        }

        Block glassDoor = blockRegistry.getValue(new ResourceLocation("assorteddecor:glass_door")).orElse(Blocks.AIR);
        if (glassDoor != Blocks.AIR) {
            lockMappings.put(glassDoor, StorageBlocks.LOCKED_GLASS_DOOR.get());
        }

        Block steelDoor = blockRegistry.getValue(new ResourceLocation("assorteddecor:steel_door")).orElse(Blocks.AIR);
        if (steelDoor != Blocks.AIR) {
            lockMappings.put(steelDoor, StorageBlocks.LOCKED_STEEL_DOOR.get());
        }

        Block chainLinkDoor = blockRegistry.getValue(new ResourceLocation("assorteddecor:chain_link_door")).orElse(Blocks.AIR);
        if (chainLinkDoor != Blocks.AIR) {
            lockMappings.put(chainLinkDoor, StorageBlocks.LOCKED_CHAIN_LINK_DOOR.get());
        }
    }

    private Block getMatchingBlock(Block doorIn) {
        if (this.lockMappings.containsKey(doorIn)) {
            return this.lockMappings.get(doorIn);
        }
        return Blocks.AIR;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();

        if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
            if (tryPlaceLockOnDoor(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        } else if (world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST) {
            if (tryPlaceLockOnEnderChest(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        } else if (new ItemStack(world.getBlockState(pos).getBlock()).is(LibCommonTags.Items.CHESTS_WOODEN)) {
            if (tryPlaceLockOnChest(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        } else if (world.getBlockState(pos).getBlock() instanceof ShulkerBoxBlock) {
            if (tryPlaceLockOnShulker(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        } else if (new ItemStack(world.getBlockState(pos).getBlock()).is(LibCommonTags.Items.BARRELS_WOODEN)) {
            if (tryPlaceLockOnBarrel(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        } else if (world.getBlockState(pos).getBlock() instanceof HopperBlock) {
            if (tryPlaceLockOnHopper(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        } else if (world.getBlockState(pos).getBlock() instanceof CrateBlock) {
            if (tryPlaceLockOnHopper(world, pos, player, hand)) {
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }

    private boolean tryPlaceLockOnEnderChest(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);

        if (itemstack.hasTag()) {
            String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
            if (!code.isEmpty()) {
                BlockState currentBlockState = worldIn.getBlockState(pos);

                Block newBlock = getMatchingBlock(currentBlockState.getBlock());
                if (newBlock == Blocks.AIR) {
                    return false;
                }

                if (worldIn.getBlockEntity(pos) instanceof EnderChestBlockEntity previousChestEntity) {

                    if (!entityplayer.isCreative())
                        itemstack.shrink(1);

                    worldIn.setBlock(pos, newBlock.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, currentBlockState.getValue(HorizontalDirectionalBlock.FACING)), 3);
                    worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                    if (worldIn.getBlockEntity(pos) instanceof LockedEnderChestBlockEntity chestBE) {
                        chestBE.setLockCode(code);
                    }

                    return true;
                }
            }

        }

        return false;
    }

    private boolean tryPlaceLockOnShulker(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);

        if (itemstack.hasTag()) {
            String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
            if (!code.isEmpty()) {
                BlockState currentBlockState = worldIn.getBlockState(pos);

                Block newBlock = getMatchingBlock(currentBlockState.getBlock());
                if (newBlock == Blocks.AIR) {
                    return false;
                }

                if (worldIn.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity previousShulkerEntity) {
                    NonNullList<ItemStack> shulkerItems = NonNullList.withSize(previousShulkerEntity.getContainerSize(), ItemStack.EMPTY);
                    for (int i = 0; i < previousShulkerEntity.getContainerSize(); i++) {
                        shulkerItems.set(i, previousShulkerEntity.getItem(i).copy());
                    }

                    if (!entityplayer.isCreative())
                        itemstack.shrink(1);

                    worldIn.setBlock(pos, newBlock.defaultBlockState().setValue(ShulkerBoxBlock.FACING, currentBlockState.getValue(ShulkerBoxBlock.FACING)), 3);
                    worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                    if (worldIn.getBlockEntity(pos) instanceof LockedShulkerBoxBlockEntity shulkerBE) {
                        shulkerBE.getItemStackStorageHandler().setStacks(shulkerItems);
                        shulkerBE.setLockCode(code);
                        shulkerBE.setColor(ShulkerBoxBlock.getColorFromBlock(currentBlockState.getBlock()));
                    }

                    return true;
                }
            }

        }

        return false;
    }

    private boolean tryPlaceLockOnChest(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);

        if (itemstack.hasTag()) {
            String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
            if (!code.isEmpty()) {
                BlockState currentBlockState = worldIn.getBlockState(pos);

                Block newBlock = getMatchingBlock(currentBlockState.getBlock());
                if (newBlock == Blocks.AIR) {
                    return false;
                }

                if (worldIn.getBlockEntity(pos) instanceof ChestBlockEntity previousChestEntity) {
                    NonNullList<ItemStack> chestItems = NonNullList.withSize(previousChestEntity.getContainerSize(), ItemStack.EMPTY);
                    for (int i = 0; i < previousChestEntity.getContainerSize(); i++) {
                        chestItems.set(i, previousChestEntity.getItem(i).copy());
                    }
                    // This way the block doesn't drop items and dupe
                    previousChestEntity.clearContent();

                    if (!entityplayer.isCreative())
                        itemstack.shrink(1);

                    worldIn.setBlock(pos, newBlock.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, currentBlockState.getValue(HorizontalDirectionalBlock.FACING)), 3);
                    worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                    if (worldIn.getBlockEntity(pos) instanceof LockedChestBlockEntity chestBE) {
                        chestBE.setLockCode(code);
                        chestBE.getItemStackStorageHandler().setStacks(chestItems);
                    }

                    return true;
                }
            }

        }

        return false;
    }

    private boolean tryPlaceLockOnBarrel(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);

        if (itemstack.hasTag()) {
            String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
            if (!code.isEmpty()) {
                BlockState currentBlockState = worldIn.getBlockState(pos);

                Block newBlock = getMatchingBlock(currentBlockState.getBlock());
                if (newBlock == Blocks.AIR) {
                    return false;
                }

                if (worldIn.getBlockEntity(pos) instanceof BarrelBlockEntity previousBarrelEntity) {
                    NonNullList<ItemStack> chestItems = NonNullList.withSize(previousBarrelEntity.getContainerSize(), ItemStack.EMPTY);
                    for (int i = 0; i < previousBarrelEntity.getContainerSize(); i++) {
                        chestItems.set(i, previousBarrelEntity.getItem(i).copy());
                    }
                    // This way the block doesn't drop items and dupe
                    previousBarrelEntity.clearContent();

                    if (!entityplayer.isCreative())
                        itemstack.shrink(1);

                    worldIn.setBlock(pos, newBlock.defaultBlockState().setValue(LockedBarrelBlock.FACING, currentBlockState.getValue(LockedBarrelBlock.FACING)), 3);
                    worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                    if (worldIn.getBlockEntity(pos) instanceof LockedBarrelBlockEntity barrelBE) {
                        barrelBE.setLockCode(code);
                        barrelBE.getItemStackStorageHandler().setStacks(chestItems);
                    }

                    return true;
                }
            }

        }

        return false;
    }

    private boolean tryPlaceLockOnHopper(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);

        if (itemstack.hasTag()) {
            String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
            if (!code.isEmpty()) {
                BlockState currentBlockState = worldIn.getBlockState(pos);

                Block newBlock = getMatchingBlock(currentBlockState.getBlock());
                if (newBlock == Blocks.AIR) {
                    return false;
                }

                if (worldIn.getBlockEntity(pos) instanceof HopperBlockEntity previousHopperBE) {
                    NonNullList<ItemStack> chestItems = NonNullList.withSize(previousHopperBE.getContainerSize(), ItemStack.EMPTY);
                    for (int i = 0; i < previousHopperBE.getContainerSize(); i++) {
                        chestItems.set(i, previousHopperBE.getItem(i).copy());
                    }
                    // This way the block doesn't drop items and dupe
                    previousHopperBE.clearContent();

                    if (!entityplayer.isCreative())
                        itemstack.shrink(1);

                    worldIn.setBlock(pos, newBlock.defaultBlockState().setValue(LockedHopperBlock.FACING, currentBlockState.getValue(LockedHopperBlock.FACING)), 3);
                    worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                    if (worldIn.getBlockEntity(pos) instanceof LockedHopperBlockEntity hopperBE) {
                        hopperBE.setLockCode(code);
                        hopperBE.getItemStackStorageHandler().setStacks(chestItems);
                    }

                    return true;
                }
            }

        }

        return false;
    }

    private boolean tryPlaceLockOnDoor(Level worldIn, BlockPos pos, Player entityplayer, InteractionHand hand) {
        ItemStack itemstack = entityplayer.getItemInHand(hand);

        if (itemstack.hasTag()) {
            String code = itemstack.getTag().contains("Storage_Lock", 8) ? itemstack.getTag().getString("Storage_Lock") : "";
            if (!code.isEmpty()) {
                BlockState currentDoor = worldIn.getBlockState(pos);

                Block newDoor = getMatchingBlock(currentDoor.getBlock());
                if (newDoor == Blocks.AIR || currentDoor.isAir()) {
                    return false;
                }

                if (!entityplayer.isCreative())
                    itemstack.shrink(1);

                BlockState newState = newDoor.defaultBlockState().setValue(DoorBlock.FACING, currentDoor.getValue(DoorBlock.FACING)).setValue(DoorBlock.OPEN, currentDoor.getValue(DoorBlock.OPEN)).setValue(DoorBlock.HINGE, currentDoor.getValue(DoorBlock.HINGE));
                DoubleBlockHalf currentHalf = currentDoor.getValue(DoorBlock.HALF);
                worldIn.setBlock(pos, newState.setValue(DoorBlock.HALF, currentHalf), Block.UPDATE_KNOWN_SHAPE);
                if (currentHalf == DoubleBlockHalf.UPPER) {
                    worldIn.setBlock(pos.below(), newState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), Block.UPDATE_ALL);
                } else {
                    worldIn.setBlock(pos.above(), newState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
                }

                if (worldIn.getBlockEntity(pos) instanceof BaseLockedBlockEntity lockedDoor) {
                    lockedDoor.setLockCode(code);
                }

                if (worldIn.getBlockEntity(currentHalf == DoubleBlockHalf.UPPER ? pos.below() : pos.above()) instanceof BaseLockedBlockEntity otherHalfLockedDoor) {
                    otherHalfLockedDoor.setLockCode(code);
                }

                worldIn.playSound(entityplayer, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
                return true;
            }

        }

        return false;
    }

}
