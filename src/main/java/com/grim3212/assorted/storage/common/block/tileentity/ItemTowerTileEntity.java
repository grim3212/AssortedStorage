package com.grim3212.assorted.storage.common.block.tileentity;

import java.util.stream.IntStream;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.client.model.ItemTowerModel;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.inventory.ItemTowerContainer;
import com.grim3212.assorted.storage.common.inventory.ItemTowerInventory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class ItemTowerTileEntity extends BaseStorageTileEntity {

	@OnlyIn(Dist.CLIENT)
	public ItemTowerModel model;

	public ItemTowerTileEntity() {
		super(StorageTileEntityTypes.ITEM_TOWER.get(), 18);
	}

	@OnlyIn(Dist.CLIENT)
	public void animate(int animId) {
		if (model == null) {
			this.model = new ItemTowerModel();
		}

		this.model.setAnimation(animId);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory player, PlayerEntity playerEntity) {
		return ItemTowerContainer.createItemTowerContainer(windowId, player, new ItemTowerInventory(getItemTowers(), this.worldPosition));
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent(AssortedStorage.MODID + ".container.item_tower");
	}

	protected static final int[] ITEM_TOWER_SLOTS = IntStream.range(0, 18).toArray();

	@Override
	public int[] getSlotsForFace(Direction side) {
		return ITEM_TOWER_SLOTS;
	}

	public NonNullList<ItemTowerTileEntity> getItemTowers() {
		BlockState state = level.getBlockState(worldPosition);
		NonNullList<ItemTowerTileEntity> itemTowers = NonNullList.create();

		int downBlocks = 1;
		while (level.getBlockState(worldPosition.below(downBlocks)) == state) {
			downBlocks++;
		}

		int upBlocks = 1;
		BlockPos bottomPos = worldPosition.below(downBlocks);
		while (level.getBlockState(bottomPos.above(upBlocks)) == state) {
			itemTowers.add((ItemTowerTileEntity) level.getBlockEntity(bottomPos.above(upBlocks)));
			upBlocks++;
		}

		return itemTowers;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return itemTowerItemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private LazyOptional<?> itemTowerItemHandler = LazyOptional.of(() -> createSidedHandler());

	protected IItemHandler createSidedHandler() {
		return new SidedInvWrapper(new ItemTowerInventory(getItemTowers(), this.worldPosition), null);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		this.itemTowerItemHandler.invalidate();
	}

	@Override
	public Block getBlockToUse() {
		return StorageBlocks.ITEM_TOWER.get();
	}
}
