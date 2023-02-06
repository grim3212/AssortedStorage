package com.grim3212.assorted.storage.common.block.blockentity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.CrateBlock;
import com.grim3212.assorted.storage.common.inventory.StorageContainerTypes;
import com.grim3212.assorted.storage.common.inventory.crates.CrateContainer;
import com.grim3212.assorted.storage.common.inventory.crates.CrateSidedInv;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.network.PacketHandler;
import com.grim3212.assorted.storage.common.network.SyncCrate;
import com.grim3212.assorted.storage.common.util.CrateLayout;
import com.grim3212.assorted.storage.common.util.LargeItemStack;
import com.grim3212.assorted.storage.common.util.NBTHelper;
import com.grim3212.assorted.storage.common.util.StorageLockCode;
import com.grim3212.assorted.storage.common.util.StorageMaterial;
import com.grim3212.assorted.storage.common.util.StorageUtil;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class CrateBlockEntity extends BlockEntity implements LockedWorldlyContainer, MenuProvider, INamed, ILockable {

	private final StorageMaterial storageMaterial;
	private final CrateLayout layout;
	private Component customName;
	// One for each slots on the face of a Storage Crate
	private NonNullList<LargeItemStack> slotContents;
	// Upgrade slots as well as lock slot
	private NonNullList<ItemStack> enhancements;

	private UUID playerTimerUUID;
	private long playerTimerMillis;
	private ItemStack playerTimerStack = ItemStack.EMPTY;

	public CrateBlockEntity(BlockPos pos, BlockState state) {
		this(StorageBlockEntityTypes.CRATE.get(), pos, state);
	}

	public CrateBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		if (state.getBlock()instanceof CrateBlock storageCrate) {
			this.storageMaterial = storageCrate.getStorageMaterial();
			layout = storageCrate.getLayout();
		} else {
			// Default to regular chest
			this.storageMaterial = null;
			this.layout = CrateLayout.SINGLE;
		}

		this.slotContents = NonNullList.<LargeItemStack>withSize(this.layout.getNumStacks(), LargeItemStack.empty());
		// 4 upgrades and 1 lock slot
		this.enhancements = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
	}

	public CrateLayout getLayout() {
		return layout;
	}

	public StorageMaterial getStorageMaterial() {
		return storageMaterial;
	}

	public ItemStack getLockStack() {
		// Lock is always slot 0
		return this.enhancements.get(0);
	}

	@Override
	public boolean isLocked() {
		return StorageUtil.hasCode(getLockStack());
	}

	@Override
	public StorageLockCode getStorageLockCode() {
		return getLockCode() == null || getLockCode().isEmpty() ? StorageLockCode.EMPTY_CODE : new StorageLockCode(getLockCode());
	}

	@Override
	public String getLockCode() {
		return StorageUtil.getCode(getLockStack());
	}

	@Override
	public void setLockCode(String s) {
		if (s != null && !s.isEmpty()) {
			this.enhancements.set(0, StorageUtil.setCodeOnStack(new StorageLockCode(s), new ItemStack(StorageItems.LOCKSMITH_LOCK.get())));

			this.setChanged();
			this.modelUpdate();
		}
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.slotContents = NonNullList.withSize(this.getContainerSize(), LargeItemStack.empty());

		ListTag items = nbt.getList("Items", 10);
		for (int i = 0; i < items.size(); i++) {
			CompoundTag slot = items.getCompound(i);
			int slotIdx = slot.getByte("Slot") & 255;
			if (slotIdx >= 0 && slotIdx < this.slotContents.size()) {
				int amount = slot.getInt("SlotAmount");
				int rotation = slot.getInt("SlotRotation");
				boolean locked = slot.getBoolean("SlotLocked");
				ItemStack stack = ItemStack.of(slot);
				this.slotContents.set(slotIdx, new LargeItemStack(stack, amount, rotation, locked));
			}
		}

		ListTag enhancementItems = nbt.getList("Enhancements", 10);
		for (int i = 0; i < enhancementItems.size(); i++) {
			CompoundTag slot = enhancementItems.getCompound(i);
			int slotIdx = slot.getByte("Slot") & 255;
			if (slotIdx >= 0 && slotIdx < this.enhancements.size()) {
				this.enhancements.set(slotIdx, ItemStack.of(slot));
			}
		}

		if (nbt.contains("CustomName", 8)) {
			this.customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		// Save all the items
		saveToNbt(compound);

		if (this.customName != null) {
			compound.putString("CustomName", Component.Serializer.toJson(this.customName));
		}
	}

	public CompoundTag saveToNbt(CompoundTag compound) {
		ListTag items = new ListTag();
		for (int i = 0; i < this.slotContents.size(); i++) {
			LargeItemStack stack = this.slotContents.get(i);
			CompoundTag slot = new CompoundTag();
			slot.putByte("Slot", (byte) i);
			slot.putInt("SlotAmount", stack.getAmount());
			slot.putInt("SlotRotation", stack.getRotation());
			slot.putBoolean("SlotLocked", stack.isLocked());
			stack.getStack().save(slot);
			items.add(slot);
		}
		compound.put("Items", items);

		ListTag enhancementItems = new ListTag();
		for (int i = 0; i < this.enhancements.size(); ++i) {
			ItemStack itemstack = this.enhancements.get(i);
			if (!itemstack.isEmpty()) {
				CompoundTag compoundtag = new CompoundTag();
				compoundtag.putByte("Slot", (byte) i);
				itemstack.save(compoundtag);
				enhancementItems.add(compoundtag);
			}
		}
		compound.put("Enhancements", enhancementItems);

		return compound;
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
	public int getContainerSize() {
		// Don't include the enhancements here since they are more or less separate
		return this.getItems().size();
	}

	@Override
	public boolean isEmpty() {
		for (LargeItemStack itemstack : this.slotContents) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemstack : this.enhancements) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public boolean areSlotsEmpty() {
		for (LargeItemStack itemstack : this.slotContents) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public List<ItemStack> getItemStacks() {
		return this.slotContents.stream().map(i -> i.getStack().copyWithCount(1)).collect(Collectors.toList());
	}

	public NonNullList<LargeItemStack> getItems() {
		return this.slotContents;
	}

	public NonNullList<ItemStack> getEnhancements() {
		return enhancements;
	}

	public ItemStack removeEnhancement(int index, int count) {
		ItemStack stack = ContainerHelper.removeItem(this.getEnhancements(), index, count);
		if (!stack.isEmpty()) {
			this.setChanged();
			this.modelUpdate();
		}
		return stack;
	}

	@Override
	public void clearContent() {
		this.getItems().clear();
		this.enhancements.clear();
	}

	@Override
	public void setCustomName(Component name) {
		this.customName = name;
	}

	@Override
	public Component getName() {
		return this.customName != null ? this.customName : this.getDefaultName();
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

	protected Component getDefaultName() {
		if (this.storageMaterial == null) {
			return Component.translatable(AssortedStorage.MODID + ".container.storage_crate");
		}

		return Component.translatable(AssortedStorage.MODID + ".container.storage_crate_" + this.storageMaterial.toString());
	}

	@Override
	public ItemStack getItem(int index) {
		LargeItemStack largeStack = this.getItems().get(index);

		if (!largeStack.isEmpty() && !largeStack.getStack().isEmpty()) {
			if (largeStack.getAmount() >= 64) {
				return largeStack.getStack().copyWithCount(64);
			} else {
				int amount = largeStack.isLocked() ? Math.max(1, largeStack.getAmount()) : largeStack.getAmount();
				return largeStack.getStack().copyWithCount(amount);
			}
		}

		return ItemStack.EMPTY;
	}

	public LargeItemStack getLargeItemStack(int index) {
		LargeItemStack largeStack = this.getItems().get(index);
		return !largeStack.isEmpty() && !largeStack.getStack().isEmpty() ? largeStack : LargeItemStack.empty();
	}

	public void setSlotLocked(int index, boolean lock) {
		LargeItemStack largeStack = this.getItems().get(index);

		if (largeStack.getStack().isEmpty()) {
			// No locking an empty slot
			return;
		}

		largeStack.setLock(lock);
		if (!lock && largeStack.getAmount() <= 0) {
			largeStack.setEmpty();
		}

		this.setSlotChanged(index);
	}

	public void setAllSlotsLocked(boolean lock) {
		for (int i = 0; i < this.getItems().size(); i++) {
			this.setSlotLocked(i, lock);
		}
	}

	public boolean isSlotLocked(int index) {
		LargeItemStack largeStack = this.getItems().get(index);
		return largeStack.isLocked() && !largeStack.isEmpty();
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack;
		if (index >= 0 && index < this.getItems().size() && !this.getItems().get(index).isEmpty() && count > 0) {
			int maxStackSize = this.getItem(index).getMaxStackSize();

			stack = this.getItems().get(index).split(Math.min(maxStackSize, count), true);
		} else {
			stack = ItemStack.EMPTY;
		}

		if (!stack.isEmpty()) {
			this.setSlotChanged(index);
		}
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		if (index >= 0 && index < this.getItems().size())
			return this.getItems().get(index).takeStack();
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index >= 0 && index < this.slotContents.size()) {
			LargeItemStack largeStack = this.getLargeItemStack(index).withStack(stack);

			if (largeStack.getAmount() > this.getMaxStackSizeForSlot(index)) {
				largeStack.setAmount(this.getMaxStackSizeForSlot(index));
			}

			this.getItems().set(index, largeStack);
		}

		this.setSlotChanged(index);
	}

	/**
	 * Used to sync the server side to the client side for slot changes
	 * 
	 * @param slot
	 * @param stack
	 */
	public void setItem(int slot, LargeItemStack stack) {
		if (slot >= 0 && slot < this.slotContents.size()) {

			if (stack.getAmount() > this.getMaxStackSizeForSlot(slot)) {
				stack.setAmount(this.getMaxStackSizeForSlot(slot));
			}

			this.getItems().set(slot, stack);
		}

		this.setSlotChanged(slot);
	}

	protected void setSlotChanged(int slot) {
		this.setChanged();
		this.level.blockUpdated(getBlockPos(), getBlockState().getBlock());

		if (this.level != null && !this.level.isClientSide()) {
			PacketHandler.sendToNearby(level, getBlockPos(), new SyncCrate(getBlockPos(), slot, this.getLargeItemStack(slot)));
		}
	}

	public boolean hasVoidUpgrade() {
		return this.enhancements.stream().anyMatch(stack -> stack.getItem() == StorageItems.VOID_UPGRADE.get());
	}

	public boolean hasGlowUpgrade() {
		return this.enhancements.stream().anyMatch(stack -> stack.getItem() == StorageItems.GLOW_UPGRADE.get());
	}

	private int getSignalFromPercentage(float percentage) {
		return Math.round(0 + percentage * (15 - 0));
	}

	public int getSignalStrength() {
		ItemStack redstoneUpgrade = this.enhancements.stream().filter(stack -> stack.getItem() == StorageItems.REDSTONE_UPGRADE.get()).findAny().orElse(ItemStack.EMPTY);

		if (redstoneUpgrade == ItemStack.EMPTY) {
			return 0;
		}

		int mode = NBTHelper.getInt(redstoneUpgrade, "Mode");
		if (mode == 4) {
			// 4: Based on all slots
			int runningTotal = 0;
			int maxAllowed = 0;
			for (int i = 0; i < this.getItems().size(); i++) {
				runningTotal += this.getLargeItemStack(i).getAmount();
				maxAllowed += this.getMaxStackSizeForSlot(i);
			}
			return getSignalFromPercentage((float) runningTotal / maxAllowed);
		} else if (mode == 5) {
			// 5: Based on most full slot
			int maxAmountSlot = IntStream.range(0, this.getItems().size()).reduce((a, b) -> this.getItems().get(a).getAmount() < this.getItems().get(b).getAmount() ? b : a).orElse(-1);
			if (maxAmountSlot == -1) {
				return 0;
			}

			int amount = this.getLargeItemStack(maxAmountSlot).getAmount();
			int maxAllowed = this.getMaxStackSizeForSlot(maxAmountSlot);
			return getSignalFromPercentage((float) amount / maxAllowed);
		} else if (mode == 6) {
			// 6: Based on least full slot
			int minAmountSlot = IntStream.range(0, this.getItems().size()).reduce((a, b) -> this.getItems().get(a).getAmount() > this.getItems().get(b).getAmount() ? b : a).orElse(-1);
			if (minAmountSlot == -1) {
				return 0;
			}

			int amount = this.getLargeItemStack(minAmountSlot).getAmount();
			int maxAllowed = this.getMaxStackSizeForSlot(minAmountSlot);
			return getSignalFromPercentage((float) amount / maxAllowed);
		} else {
			// 0: Based on slot 0
			// 1: Based on slot 1
			// 2: Based on slot 2
			// 3: Based on slot 3
			// Bail early if the slot doesn't exist in this storage crate
			if (mode < 0 || mode >= this.getItems().size()) {
				return 0;
			}

			int amount = this.getLargeItemStack(mode).getAmount();
			int maxAllowed = this.getMaxStackSizeForSlot(mode);
			return getSignalFromPercentage((float) amount / maxAllowed);
		}
	}

	/**
	 * 
	 * @param index
	 * @param stack
	 * @return
	 * 
	 *         -1 means that we couldn't add anything 0 means everything was added
	 *         1+ means there was a remainder of items not added
	 */
	public int addItem(int index, ItemStack stack) {
		if (index >= 0 && index < this.slotContents.size() && !stack.isEmpty()) {
			LargeItemStack largeStack = this.slotContents.get(index);

			if (ItemHandlerHelper.canItemStacksStack(stack, largeStack.getStack())) {
				int currentAmount = largeStack.getAmount();
				int amount = Math.min(this.getMaxStackSizeForSlot(index), currentAmount + stack.getCount());
				largeStack.setAmount(amount);
				this.setSlotChanged(index);
				// If too many items were given this will have a positive value otherwise 0
				// If void force that we say all items were added
				return hasVoidUpgrade() ? 0 : stack.getCount() + currentAmount - amount;
			} else if (largeStack.isEmpty()) {
				int amount = Math.min(this.getMaxStackSizeForSlot(index), stack.getCount());
				this.getItems().set(index, largeStack.withStack(stack.copyWithCount(amount)));
				this.setSlotChanged(index);
				// We tried to initialize this slot
				// If too many items were given this will have a positive value otherwise 0
				// If void force that we say all items were added
				return hasVoidUpgrade() ? 0 : stack.getCount() - amount;
			} else {
				// Stack did not match what was currently in this slot
				return -1;
			}
		}

		// Invalid input
		return -1;
	}

	/**
	 * Attacking (left-click) will drop items if they are contained in the slot hit
	 * 
	 * if player is shifting then drop a stack else drop 1
	 * 
	 * @param player
	 * @param hit
	 */
	public boolean attack(Player player, BlockHitResult hit) {
		int hitSlot = getHitSlot(hit);
		if (hitSlot < 0)
			return false;

		int maxAmount = player.isShiftKeyDown() ? 64 : 1;

		ItemStack stack = removeItem(hitSlot, maxAmount);
		if (!stack.isEmpty()) {
			if (!player.getInventory().add(stack)) {
				Containers.dropItemStack(level, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), stack);
			}
			return true;
		}
		return !stack.isEmpty();
	}

	/**
	 * 
	 * On use (right-click) we will try and do a number of things depending on how
	 * it was interacted
	 * 
	 * if rotate item is used rotate hit slot
	 * 
	 * 
	 * if first use add the current item by 1 if second use add the current item by
	 * all found in players inventory
	 * 
	 * @param player
	 * @param handIn
	 * @param hit
	 */
	public InteractionResult use(Player player, InteractionHand handIn, BlockHitResult hit) {
		int hitSlot = getHitSlot(hit);

		if (hitSlot < 0) {
			return InteractionResult.PASS;
		}

		ItemStack playerStack = player.getItemInHand(handIn).copyWithCount(1);

		if (playerStack.getItem() == StorageItems.ROTATOR_MAJIG.get()) {
			this.getLargeItemStack(hitSlot).cycleRotation();
			this.setSlotChanged(hitSlot);
			return InteractionResult.SUCCESS;
		}

		if (player.getUUID().equals(playerTimerUUID) && Util.getMillis() - playerTimerMillis < 275 && playerTimerStack != ItemStack.EMPTY) {
			List<Integer> slots = findMatchingStacks(player, playerTimerStack);
			for (int slot : slots) {
				ItemStack slotItem = player.getInventory().getItem(slot).copy();
				int addRet = addItem(hitSlot, slotItem);
				if (addRet < 0) {
					// We can't add anymore to this storage crate
					break;
				} else if (addRet == 0) {
					player.getInventory().setItem(slot, ItemStack.EMPTY);
				} else {
					player.getInventory().getItem(slot).shrink(slotItem.getCount() - addRet);
				}
			}

			playerTimerUUID = null;
			playerTimerMillis = 0;
			playerTimerStack = ItemStack.EMPTY;
			return InteractionResult.SUCCESS;
		}

		ItemStack playerItem = player.getItemInHand(handIn).copy();
		int addRet = addItem(hitSlot, playerItem);
		if (addRet < 0) {
			// We can't add anymore to this storage crate
			return InteractionResult.SUCCESS;
		} else if (addRet == 0) {
			player.getItemInHand(handIn).shrink(playerItem.getCount());

			playerTimerUUID = player.getUUID();
			playerTimerMillis = Util.getMillis();
			playerTimerStack = playerStack;
		} else {
			player.getItemInHand(handIn).shrink(playerItem.getCount() - addRet);
		}

		return InteractionResult.SUCCESS;
	}

	public static List<Integer> findMatchingStacks(Player player, ItemStack stack) {
		List<Integer> slots = Lists.newArrayList();

		for (int slot = 0; slot < player.getInventory().getContainerSize(); ++slot) {
			ItemStack itemstack = player.getInventory().getItem(slot);

			if (!itemstack.isEmpty()) {
				if (ItemHandlerHelper.canItemStacksStack(stack, itemstack)) {
					slots.add(slot);
				}
			}
		}

		return slots;
	}

	private int getHitSlot(BlockHitResult hit) {
		if (hit.getDirection() != this.getBlockState().getValue(CrateBlock.FACING)) {
			return -1;
		}

		return this.layout.getHitSlot(hit);
	}

	public int getMaxStackSizeForSlot(int slot) {
		if (slot < 0 && slot >= this.getItems().size()) {
			return 0;
		}

		int slotBase = this.layout.getSlotsBaseStacks()[slot];
		int stackSizeMultiplier = this.getItem(slot).getMaxStackSize();

		return stackSizeMultiplier * (this.storageMaterial == null ? slotBase : slotBase * (this.storageMaterial.getStorageLevel() + 1));
	}

	@Override
	public int getMaxStackSize() {
		return 0;
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return !this.isLocked();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return !this.isLocked();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction, String code, boolean force) {
		return force || (this.isLocked() ? this.getLockCode().equals(code) : true);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction, String code, boolean force) {
		return force || (this.isLocked() ? this.getLockCode().equals(code) : true);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return this.layout.getSlots();
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
		return new CrateSidedInv(this, null);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		this.storageItemHandler.invalidate();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory player, Player playerEntity) {
		return new CrateContainer(StorageContainerTypes.CRATES.get(this.storageMaterial).get(), windowId, player, this);
	}
}
