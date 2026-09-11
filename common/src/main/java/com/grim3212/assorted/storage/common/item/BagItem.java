package com.grim3212.assorted.storage.common.item;

import com.grim3212.assorted.lib.annotations.LoaderImplement;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.core.inventory.IPlatformInventoryStorageHandler;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.util.NBTHelper;
import com.grim3212.assorted.storage.api.StorageAccessUtil;
import com.grim3212.assorted.storage.api.StorageMaterial;
import com.grim3212.assorted.storage.common.inventory.bag.BagContainer;
import com.grim3212.assorted.storage.common.inventory.bag.BagItemHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


public class BagItem extends Item implements IInventoryItem {

    public final static String TAG_PRIMARY_COLOR = "PrimaryColor";
    public final static String TAG_SECONDARY_COLOR = "SecondaryColor";

    private final StorageMaterial material;

    public BagItem(Properties props, @Nullable StorageMaterial material) {
        super(props.stacksTo(1).component(StorageDataComponents.STORAGE_INFO.get(), new StorageInfo(StorageInfo.LockLine.LOCKED, material == null ? 0 : material.getStorageLevel())));
        this.material = material;
    }

    @Override
    public IPlatformInventoryStorageHandler getStorageHandler(ItemStack stack) {
        return Services.INVENTORY.createStorageInventoryHandler(new BagItemHandler(stack, this.material));
    }

    public StorageMaterial getStorageMaterial() {
        return material;
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FORGE, value = "IForgeItem")
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @LoaderImplement(loader = LoaderImplement.Loader.FABRIC, value = "FabricItem")
    public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player playerIn, InteractionHand handIn) {
        if (StorageAccessUtil.canAccess(playerIn.getItemInHand(handIn), playerIn)) {
            if (!level.isClientSide()) {
                Services.PLATFORM.openMenu((ServerPlayer) playerIn, new MenuProvider() {
                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                        return new BagContainer(id, player.level(), player.blockPosition(), inv, player);
                    }

                    @Override
                    public Component getDisplayName() {
                        return playerIn.getItemInHand(handIn).getHoverName();
                    }
                }, buf -> buf.writeBlockPos(playerIn.blockPosition()));
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * A per stack name is a {@link Component} from {@code getName} now - {@code getDescriptionId}
     * lost its stack overload and is final. The dye colour lives in the stack's CUSTOM_DATA
     * component rather than raw stack NBT.
     */
    @Override
    public Component getName(ItemStack stack) {
        int color = NBTHelper.getInt(stack, TAG_PRIMARY_COLOR, -1);
        if (color == -1) {
            return super.getName(stack);
        }

        return Component.translatable(this.getDescriptionId() + "_" + DyeColor.byId(color).getName());
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
