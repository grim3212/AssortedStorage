package com.grim3212.assorted.storage;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.crafting.StorageConditions;
import com.grim3212.assorted.storage.common.crafting.StorageRecipeSerializers;
import com.grim3212.assorted.storage.common.events.StorageEvents;
import com.grim3212.assorted.storage.common.handlers.StorageCreativeItems;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.common.loot.StorageLootConditions;
import com.grim3212.assorted.storage.common.loot.StorageLootEntries;
import com.grim3212.assorted.storage.common.network.StoragePackets;
import com.grim3212.assorted.storage.config.StorageCommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class StorageCommonMod {

    public static final StorageCommonConfig COMMON_CONFIG = new StorageCommonConfig();

    public static void init() {
        StorageBlocks.init();
        StorageBlockEntityTypes.init();
        StorageItems.init();
        StoragePackets.init();
        StorageConditions.init();
        StorageLootConditions.init();
        StorageLootEntries.init();
        StorageRecipeSerializers.init();
        StorageEvents.init();

        Services.PLATFORM.registerCreativeTab(new ResourceLocation(Constants.MOD_ID, "tab"), Component.translatable("itemGroup." + Constants.MOD_ID), () -> new ItemStack(StorageBlocks.WOOD_CABINET.get()), () -> StorageCreativeItems.getCreativeItems());
    }
}
