package com.grim3212.assorted.storage.common.block;

import java.util.List;

import com.grim3212.assorted.storage.AssortedStorage;
import com.grim3212.assorted.storage.common.block.blockentity.GoldSafeBlockEntity;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.util.StorageLockCode;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

public class GoldSafeBlock extends BaseStorageBlock {

	public static final ResourceLocation CONTENTS = new ResourceLocation(AssortedStorage.MODID, "contents");

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
					new StorageLockCode(goldsafetileentity.getLockCode()).write(tag);
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
		GoldSafeBlockEntity goldsafetileentity = (GoldSafeBlockEntity) worldIn.getBlockEntity(pos);
		CompoundTag compoundnbt = goldsafetileentity.saveToNbt(new CompoundTag());
		if (!compoundnbt.isEmpty()) {
			itemstack.addTagElement("BlockEntityTag", compoundnbt);
		}

		return itemstack;
	}

	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		if (tileentity instanceof GoldSafeBlockEntity) {
			GoldSafeBlockEntity goldsafetileentity = (GoldSafeBlockEntity) tileentity;
			if (!worldIn.isClientSide && player.isCreative() && !goldsafetileentity.isEmpty()) {
				ItemStack itemstack = new ItemStack(StorageBlocks.GOLD_SAFE.get());
				CompoundTag compoundnbt = goldsafetileentity.saveToNbt(new CompoundTag());
				if (!compoundnbt.isEmpty()) {
					itemstack.addTagElement("BlockEntityTag", compoundnbt);
				}

				if (goldsafetileentity.hasCustomName()) {
					itemstack.setHoverName(goldsafetileentity.getCustomName());
				}

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
		if (tileentity instanceof GoldSafeBlockEntity) {
			GoldSafeBlockEntity goldsafetileentity = (GoldSafeBlockEntity) tileentity;
			builder = builder.withDynamicDrop(CONTENTS, (context, stackConsumer) -> {
				for (int i = 0; i < goldsafetileentity.getContainerSize(); ++i) {
					stackConsumer.accept(goldsafetileentity.getItem(i));
				}

			});
		}

		return super.getDrops(state, builder);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		CompoundTag compoundnbt = stack.getTagElement("BlockEntityTag");
		if (compoundnbt != null) {
			if (compoundnbt.contains("LootTable", 8)) {
				tooltip.add(new TextComponent("???????"));
			}

			if (compoundnbt.contains("Items", 9)) {
				NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
				ContainerHelper.loadAllItems(compoundnbt, nonnulllist);
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
					tooltip.add((new TranslatableComponent("container.shulkerBox.more", j - i)).withStyle(ChatFormatting.ITALIC));
				}
			}
		}
	}
}
