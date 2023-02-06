package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Queues;
import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.api.crates.ICrateSystem;
import com.grim3212.assorted.storage.common.handler.StorageConfig;
import com.grim3212.assorted.storage.common.inventory.crates.CrateControllerInvWrapper;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.util.CrateConnection;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class CrateControllerBlockEntity extends BlockEntity implements LockedWorldlyContainer, INamed, ILockable {

	private StorageLockCode lockCode = StorageLockCode.EMPTY_CODE;
	private Component customName;

	private List<CrateConnection> connectedStorageCrates = new ArrayList<>();
	private Map<Integer, List<Integer>> slottedConnections = new HashMap<>();

	private final int maxRange;

	private UUID playerTimerUUID;
	private long playerTimerMillis;
	private ItemStack playerTimerStack = ItemStack.EMPTY;

	public CrateControllerBlockEntity(BlockPos pos, BlockState state) {
		this(StorageBlockEntityTypes.CRATE_CONTROLLER.get(), pos, state);
	}

	public CrateControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		this.maxRange = StorageConfig.COMMON.maxControllerSearchRange.get();
	}

	@Override
	public boolean isLocked() {
		return this.lockCode != null && this.lockCode != StorageLockCode.EMPTY_CODE;
	}

	@Override
	public StorageLockCode getStorageLockCode() {
		return this.lockCode;
	}

	@Override
	public String getLockCode() {
		return this.lockCode.getLockCode();
	}

	@Override
	public void setLockCode(String s) {
		if (s == null || s.isEmpty())
			this.lockCode = StorageLockCode.EMPTY_CODE;
		else
			this.lockCode = new StorageLockCode(s);

		this.setChanged();
		this.modelUpdate();
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		if (nbt.contains("CustomName", 8)) {
			this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
		}

		this.lockCode = StorageLockCode.read(nbt);
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);

		if (this.customName != null) {
			compound.putString("CustomName", Component.Serializer.toJson(this.customName));
		}

		this.lockCode.write(compound);
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
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		modelUpdate();
	}

	public void modelUpdate() {
		requestModelDataUpdate();
		this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
	}

	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(ModelProperties.IS_LOCKED, this.isLocked()).build();
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

	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	public boolean hasConnectedCrates() {
		return this.connectedStorageCrates.size() > 0;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return this.canPlaceItemThroughFace(index, itemStackIn, direction, "", false);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return this.canTakeItemThroughFace(index, stack, direction, "", false);
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack itemStackIn, Direction direction, String code, boolean force) {
		if (force) {
			return true;
		}

		boolean hasMatchLock = this.isLocked() ? this.getLockCode().equals(code) : true;
		if (!hasMatchLock) {
			return false;
		}

		List<CrateConnection> connections = this.findSlottedCrates(slot);
		for (CrateConnection connection : connections) {
			if (connection == null) {
				continue;
			}

			if (this.getLevel().getBlockEntity(connection.getPos())instanceof CrateBlockEntity crate) {
				CrateSidedInv inv = crate.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).<CrateSidedInv>cast().orElse(null);
				if (inv != null) {
					ItemStack response = code.isEmpty() ? inv.insertItem(slot, itemStackIn, true) : inv.insertItem(slot, itemStackIn, true, code);
					if (response != itemStackIn) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction, String code, boolean force) {
		if (force) {
			return true;
		}

		boolean hasMatchLock = this.isLocked() ? this.getLockCode().equals(code) : true;
		if (!hasMatchLock) {
			return false;
		}

		int amount = 1;
		List<CrateConnection> connections = this.findSlottedCrates(slot);

		for (CrateConnection connection : connections) {
			if (connection == null) {
				continue;
			}

			if (this.getLevel().getBlockEntity(connection.getPos())instanceof CrateBlockEntity crate) {
				CrateSidedInv inv = crate.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).<CrateSidedInv>cast().orElse(null);
				if (inv != null) {
					ItemStack response = code.isEmpty() ? inv.extractItem(slot, amount, true) : inv.extractItem(slot, amount, true, code);
					if (!response.isEmpty()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return this.slottedConnections.keySet().stream().mapToInt(x -> x).toArray();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
			return storageItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private LazyOptional<?> storageItemHandler = LazyOptional.of(() -> createSidedHandler());

	protected IItemHandler createSidedHandler() {
		return new CrateControllerInvWrapper(this, null);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		this.storageItemHandler.invalidate();
	}

	@Override
	public boolean isEmpty() {
		return this.getContainerSize() <= 0;
	}

	@Override
	public int getContainerSize() {
		return this.slottedConnections.size();
	}

	@Override
	public ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		AssortedStorage.LOGGER.error("Controller called own setItem (This should not happen)");
	}

	@Override
	public void clearContent() {
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
			if (crateSystem.hasItems()) {
				knownConnections.add(checking);
				connectedStorageCrates.add(new CrateConnection(checking, dist, crateSystem.getItems(getLevel(), checking)));
			}

			BlockPos[] neighbors = new BlockPos[] { checking.west(), checking.east(), checking.south(), checking.north(), checking.above(), checking.below() };
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

		int maxSlots = connectedStorageCrates.stream().max((a, b) -> Integer.compare(a.getSupportedItems().size(), b.getSupportedItems().size())).map(x -> x.getSupportedItems().size()).get();

		for (int i = 0; i < maxSlots; i++) {
			List<Integer> storageConnectionsForSlotIndex = Lists.newArrayList();
			for (int j = 0; j < connectedStorageCrates.size(); j++) {
				CrateConnection connection = this.connectedStorageCrates.get(j);

				if (connection.getSupportedItems().size() > i) {
					storageConnectionsForSlotIndex.add(j);
				}
			}
			slottedConnections.put(i, storageConnectionsForSlotIndex);
		}

	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return this.canPlaceItemThroughFace(slot, stack, null, getLockCode(), true);
	}

	public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, String inLockCode) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		// Can't insert since this is locked
		if (this.isLocked() && !this.getLockCode().equals(inLockCode)) {
			return stack;
		}

		List<CrateConnection> connections = this.findSlottedCrates(slot);

		for (CrateConnection connection : connections) {
			if (connection == null) {
				continue;
			}

			if (this.getLevel().getBlockEntity(connection.getPos())instanceof CrateBlockEntity crate) {
				CrateSidedInv inv = crate.getCapability(ForgeCapabilities.ITEM_HANDLER).<CrateSidedInv>cast().orElse(null);
				if (inv != null) {
					ItemStack response = inLockCode.isEmpty() ? inv.insertItem(slot, stack, simulate) : inv.insertItem(slot, stack, simulate, inLockCode);

					if (response != stack) {
						return response;
					}
				}
			}
		}

		return stack;
	}

	public ItemStack extractItem(int slot, int amount, boolean simulate, String inLockCode) {
		if (amount == 0)
			return ItemStack.EMPTY;

		// Can't insert since this is locked
		if (this.isLocked() && !this.getLockCode().equals(inLockCode)) {
			return ItemStack.EMPTY;
		}

		List<CrateConnection> connections = this.findSlottedCrates(slot);

		for (CrateConnection connection : connections) {
			if (connection == null) {
				continue;
			}

			if (this.getLevel().getBlockEntity(connection.getPos())instanceof CrateBlockEntity crate) {
				CrateSidedInv inv = crate.getCapability(ForgeCapabilities.ITEM_HANDLER).<CrateSidedInv>cast().orElse(null);
				if (inv != null) {
					ItemStack response = inLockCode.isEmpty() ? inv.extractItem(slot, amount, simulate) : inv.extractItem(slot, amount, simulate, inLockCode);
					if (!response.isEmpty()) {
						return response;
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

	public InteractionResult use(Player player, InteractionHand handIn) {
		ItemStack playerStack = player.getItemInHand(handIn).copyWithCount(1);
		if (player.getUUID().equals(playerTimerUUID) && Util.getMillis() - playerTimerMillis < 275 && playerTimerStack != ItemStack.EMPTY) {
			List<Integer> slots = CrateBlockEntity.findMatchingStacks(player, playerTimerStack);
			int[] possibleSlots = this.getSlotsForFace(null);

			for (int playerSlot : slots) {
				ItemStack slotItem = player.getInventory().getItem(playerSlot).copy();

				for (int i = 0; i < possibleSlots.length; i++) {
					int connectionSlot = possibleSlots[i];
					ItemStack insertReturn = this.insertItem(connectionSlot, slotItem, false, this.getLockCode());

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
			ItemStack insertReturn = this.insertItem(connectionSlot, playerItem, false, this.getLockCode());

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
