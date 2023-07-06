package com.grim3212.assorted.storage.common.block.blockentity;

import com.google.common.collect.Queues;
import com.grim3212.assorted.lib.client.model.data.IBlockModelData;
import com.grim3212.assorted.lib.client.model.data.IModelDataBuilder;
import com.grim3212.assorted.lib.core.block.IBlockEntityWithModelData;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.INamed;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.core.inventory.locking.ILockable;
import com.grim3212.assorted.lib.core.inventory.locking.StorageUtil;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.StorageCommonMod;
import com.grim3212.assorted.storage.api.crates.CrateConnection;
import com.grim3212.assorted.storage.api.crates.ICrateSystem;
import com.grim3212.assorted.storage.common.inventory.crates.CrateControllerInvWrapper;
import com.grim3212.assorted.storage.common.properties.StorageModelProperties;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CrateControllerBlockEntity extends BlockEntity implements INamed, ILockable, IBlockEntityWithModelData, IInventoryBlockEntity {

    private String lockCode = "";
    private Component customName;

    private List<CrateConnection> connectedStorageCrates = new ArrayList<>();
    private Map<Integer, List<Integer>> slottedConnections = new HashMap<>();

    private final int maxRange;

    private UUID playerTimerUUID;
    private long playerTimerMillis;
    private ItemStack playerTimerStack = ItemStack.EMPTY;
    private IPlatformInventoryStorageHandler handler;
    private CrateControllerInvWrapper crateControllerInvWrapper;

    public CrateControllerBlockEntity(BlockPos pos, BlockState state) {
        this(StorageBlockEntityTypes.CRATE_CONTROLLER.get(), pos, state);
    }

    public CrateControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        this.maxRange = StorageCommonMod.COMMON_CONFIG.maxControllerSearchRange.get();
        this.crateControllerInvWrapper = new CrateControllerInvWrapper(this);
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler() {
        if (this.handler == null) {
            this.handler = this.createStorageHandler();
        }

        return this.handler;
    }

    private IPlatformInventoryStorageHandler createStorageHandler() {
        return Services.INVENTORY.createStorageInventoryHandler(this.crateControllerInvWrapper);
    }

    public CrateControllerInvWrapper getItemStackStorageHandler() {
        return this.crateControllerInvWrapper;
    }

    @Override
    public boolean isLocked() {
        return this.lockCode != null && !this.lockCode.isEmpty();
    }


    @Override
    public String getLockCode() {
        return this.lockCode;
    }

    @Override
    public void setLockCode(String s) {
        if (s == null || s.isEmpty())
            this.lockCode = "";
        else
            this.lockCode = s;

        this.setChanged();
        this.modelDataUpdate();
    }

    protected void modelDataUpdate() {
        Level level = this.getLevel();
        if (level != null && level.isClientSide) {
            ClientServices.MODELS.requestModelDataRefresh(this);
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }

        this.lockCode = StorageUtil.readLock(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        if (this.customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(this.customName));
        }

        StorageUtil.writeLock(compound, this.lockCode);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public @NotNull IBlockModelData getBlockModelData() {
        return IModelDataBuilder.create().withInitial(StorageModelProperties.IS_LOCKED, this.isLocked()).build();
    }

    @Override
    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.customName;
    }

    public boolean hasConnectedCrates() {
        return this.connectedStorageCrates.size() > 0;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.handler != null) {
            this.handler.invalidate();
        }
    }


    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (getLevel() == null)
            return;

        if (!getLevel().getBlockTicks().hasScheduledTick(getBlockPos(), this.getBlockState().getBlock()))
            getLevel().scheduleTick(getBlockPos(), this.getBlockState().getBlock(), 1);
    }

    public void tick() {
        findConnections();
    }

    public List<CrateConnection> findSlottedCrates(int slot) {
        List<Integer> connections = this.slottedConnections.get(slot);
        List<CrateConnection> foundConnections = new ArrayList<>();

        for (int i = 0; i < connections.size(); i++) {
            int connectionIndex = connections.get(i);
            CrateConnection checkConnection = this.connectedStorageCrates.get(connectionIndex);

            if (checkConnection != null) {
                foundConnections.add(checkConnection);
            }
        }

        return foundConnections;
    }

    public void findConnections() {
        if (getLevel() == null) {
            return;
        }

        connectedStorageCrates.clear();
        slottedConnections.clear();

        BlockPos start = this.getBlockPos();

        Queue<BlockPos> searching = Queues.newConcurrentLinkedQueue();
        List<BlockPos> processed = new ArrayList<>();
        List<BlockPos> knownConnections = new ArrayList<>();
        searching.add(start);
        processed.add(start);

        while (!searching.isEmpty()) {
            // Remove first index
            BlockPos checking = searching.remove();

            int dist = start.distManhattan(checking);
            if (dist > this.maxRange) {
                continue;
            }

            if (!getLevel().isLoaded(checking)) {
                continue;
            }

            Block block = getLevel().getBlockState(checking).getBlock();
            if (!(block instanceof ICrateSystem)) {
                continue;
            }

            if (knownConnections.contains(checking)) {
                continue;
            }

            ICrateSystem crateSystem = (ICrateSystem) block;
            int numSlots = crateSystem.numSlots(level, checking);
            if (numSlots > 0) {
                knownConnections.add(checking);
                connectedStorageCrates.add(new CrateConnection(checking, dist, numSlots));
            }

            BlockPos[] neighbors = new BlockPos[]{checking.west(), checking.east(), checking.south(), checking.north(), checking.above(), checking.below()};
            for (BlockPos pos : neighbors) {
                if (!processed.contains(pos)) {
                    searching.add(pos);
                    processed.add(pos);
                }
            }
        }

        if (connectedStorageCrates.size() == 0) {
            return;
        }

        // Sort by depth so when we query it later we don't need to sort each time
        connectedStorageCrates = connectedStorageCrates.stream().sorted((a, b) -> Integer.compare(a.getDepth(), b.getDepth())).collect(Collectors.toList());

        int maxSlots = connectedStorageCrates.stream().max((a, b) -> Integer.compare(a.getNumSlots(), b.getNumSlots())).map(x -> x.getNumSlots()).get();

        for (int i = 0; i < maxSlots; i++) {
            List<Integer> storageConnectionsForSlotIndex = new ArrayList<>();
            for (int j = 0; j < connectedStorageCrates.size(); j++) {
                CrateConnection connection = this.connectedStorageCrates.get(j);

                if (connection.getNumSlots() > i) {
                    storageConnectionsForSlotIndex.add(j);
                }
            }
            slottedConnections.put(i, storageConnectionsForSlotIndex);
        }

    }

    public int[] getPossibleSlots() {
        return this.slottedConnections.keySet().stream().mapToInt(x -> x).toArray();
    }

    public InteractionResult use(Player player, InteractionHand handIn) {
        ItemStack playerStack = player.getItemInHand(handIn).copyWithCount(1);
        if (player.getUUID().equals(playerTimerUUID) && Util.getMillis() - playerTimerMillis < 275 && playerTimerStack != ItemStack.EMPTY) {
            List<Integer> slots = CrateBlockEntity.findMatchingStacks(player, playerTimerStack);
            int[] possibleSlots = this.getPossibleSlots();

            for (int playerSlot : slots) {
                ItemStack slotItem = player.getInventory().getItem(playerSlot).copy();

                for (int i = 0; i < possibleSlots.length; i++) {
                    int connectionSlot = possibleSlots[i];
                    ItemStack insertReturn = this.getItemStackStorageHandler().insertItem(connectionSlot, slotItem, false, this.getLockCode(), false);

                    // We added all of the stack to this slot
                    if (insertReturn.isEmpty()) {
                        player.getInventory().setItem(playerSlot, ItemStack.EMPTY);

                        // We break out to process the next player stack
                        break;
                    }

                    // This slot accepted a partial amount of our item
                    if (slotItem != insertReturn) {
                        player.getInventory().getItem(playerSlot).shrink(slotItem.getCount() - insertReturn.getCount());
                        // We have a different amount to search for now in the remaining slots
                        slotItem = insertReturn.copy();

                        // Reset iteration to retry at the same slot we were on
                        connectionSlot--;
                    }
                }
            }

            playerTimerUUID = null;
            playerTimerMillis = 0;
            playerTimerStack = ItemStack.EMPTY;
            return InteractionResult.SUCCESS;
        }

        ItemStack playerItem = player.getItemInHand(handIn).copy();
        for (int connectionSlot = 0; connectionSlot < this.slottedConnections.size(); connectionSlot++) {
            ItemStack insertReturn = this.getItemStackStorageHandler().insertItem(connectionSlot, playerItem, false, this.getLockCode(), false);

            // We added all of the stack to this slot
            if (insertReturn.isEmpty()) {
                player.getItemInHand(handIn).shrink(playerItem.getCount());
                playerItem = ItemStack.EMPTY;
                break;
            }

            // This slot accepted a partial amount of our item
            if (playerItem != insertReturn) {
                player.getItemInHand(handIn).shrink(playerItem.getCount() - insertReturn.getCount());
                // We have a different amount to search for now in the remaining slots
                playerItem = insertReturn.copy();

                // Reset iteration to retry at the same slot we were on
                connectionSlot--;
            }
        }

        // If we were able to place the whole stack in our inventory then set
        // the timer fields for detecting double click
        if (playerItem.isEmpty()) {
            playerTimerUUID = player.getUUID();
            playerTimerMillis = Util.getMillis();
            playerTimerStack = playerStack;
        }

        return InteractionResult.SUCCESS;
    }
}
