package com.grim3212.assorted.storage;

import com.grim3212.assorted.storage.client.data.StorageLanguageProvider;
import com.grim3212.assorted.lib.core.inventory.IInventoryBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IInventoryItem;
import com.grim3212.assorted.lib.data.ForgeBlockTagProvider;
import com.grim3212.assorted.lib.data.ForgeItemTagProvider;
import com.grim3212.assorted.lib.inventory.ForgePlatformInventoryStorageHandlerUnsided;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.storage.client.data.StorageBlockstateProvider;
import com.grim3212.assorted.storage.client.data.StorageItemModelProvider;
import com.grim3212.assorted.storage.client.data.StorageSpriteSourceProvider;
import com.grim3212.assorted.storage.common.block.StorageBlocks;
import com.grim3212.assorted.storage.common.block.blockentity.StorageBlockEntityTypes;
import com.grim3212.assorted.storage.common.item.StorageItems;
import com.grim3212.assorted.storage.data.StorageBlockLoot;
import com.grim3212.assorted.storage.data.StorageBlockTagProvider;
import com.grim3212.assorted.storage.data.StorageDataMapProvider;
import com.grim3212.assorted.storage.data.StorageItemTagProvider;
import com.grim3212.assorted.storage.data.StorageRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod(Constants.MOD_ID)
public class AssortedStorageNeoForge {

    /**
     * {@code FMLJavaModLoadingContext} is gone; the mod event bus and the mod container are injected
     * into the {@code @Mod} constructor instead.
     */
    public AssortedStorageNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::setup);
        modBus.addListener(this::gatherServerData);
        modBus.addListener(this::gatherClientData);
        modBus.addListener(this::registerCapabilities);

        StorageCommonMod.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(StorageBlocks::initDispenserHandlers);
    }

    /**
     * Server datagen. The server and client halves are separate events; if the wrong one runs, the
     * build still succeeds, with "All providers took: 0 ms".
     */
    private void gatherServerData(final GatherDataEvent.Server event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Recipe providers are not data providers any more - the Runner owns the output.
        event.addProvider(new StorageRecipes.Runner(packOutput, lookupProvider));
        ForgeBlockTagProvider blockTagProvider = event.addProvider(new ForgeBlockTagProvider(packOutput, lookupProvider, Constants.MOD_ID, new StorageBlockTagProvider(packOutput, lookupProvider)));
        event.addProvider(new ForgeItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), Constants.MOD_ID, new StorageItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter())));
        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(StorageBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider));
        event.addProvider(new StorageDataMapProvider(packOutput, lookupProvider));
    }

    /**
     * Client datagen: block states and models, item models, sprite sources and the lang file. The
     * two model providers split the mod between them so they never write the same file.
     */
    private void gatherClientData(final GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        event.addProvider(new StorageBlockstateProvider(packOutput));
        event.addProvider(new StorageItemModelProvider(packOutput));
        event.addProvider(new StorageSpriteSourceProvider(packOutput, lookupProvider));
        event.addProvider(new StorageLanguageProvider(packOutput));
    }

    /**
     * Exposes the storage blocks and items as item handlers ({@code Capabilities.Item.BLOCK} /
     * {@code .ITEM}), the NeoForge side of Fabric's {@code ItemStorage.SIDED}.
     */
    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        registerBlockEntity(event, StorageBlockEntityTypes.WOOD_CABINET);
        registerBlockEntity(event, StorageBlockEntityTypes.GLASS_CABINET);
        registerBlockEntity(event, StorageBlockEntityTypes.GOLD_SAFE);
        registerBlockEntity(event, StorageBlockEntityTypes.OBSIDIAN_SAFE);
        registerBlockEntity(event, StorageBlockEntityTypes.LOCKER);
        registerBlockEntity(event, StorageBlockEntityTypes.ITEM_TOWER);
        registerBlockEntity(event, StorageBlockEntityTypes.WAREHOUSE_CRATE);
        registerBlockEntity(event, StorageBlockEntityTypes.LOCKED_CHEST);
        registerBlockEntity(event, StorageBlockEntityTypes.LOCKED_ENDER_CHEST);
        registerBlockEntity(event, StorageBlockEntityTypes.LOCKED_HOPPER);
        registerBlockEntity(event, StorageBlockEntityTypes.LOCKED_BARREL);
        registerBlockEntity(event, StorageBlockEntityTypes.LOCKED_SHULKER_BOX);
        registerBlockEntity(event, StorageBlockEntityTypes.CRATE);
        registerBlockEntity(event, StorageBlockEntityTypes.CRATE_CONTROLLER);
        registerBlockEntity(event, StorageBlockEntityTypes.CRATE_COMPACTING);

        registerItem(event, StorageItems.BAG.get());
        registerItem(event, StorageItems.ENDER_BAG.get());
        registerItem(event, StorageItems.KEY_RING.get());
        for (IRegistryObject<? extends Item> bag : StorageItems.BAGS.values()) {
            registerItem(event, bag.get());
        }
    }

    private static <BE extends BlockEntity> void registerBlockEntity(RegisterCapabilitiesEvent event, IRegistryObject<BlockEntityType<BE>> type) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, type.get(), (blockEntity, side) -> {
            if (blockEntity.isRemoved() || !(blockEntity instanceof IInventoryBlockEntity inv)) {
                return null;
            }
            return ((ForgePlatformInventoryStorageHandlerUnsided) inv.getStorageHandler()).getCapability();
        });
    }

    /**
     * The stack-count guard is the one the deleted mixin carried: a stacked bag has no single
     * inventory to expose, so it answers with nothing rather than letting several stacks share one.
     */
    private static void registerItem(RegisterCapabilitiesEvent event, Item item) {
        event.registerItem(Capabilities.Item.ITEM, (stack, context) -> {
            if (stack.isEmpty() || stack.getCount() > 1 || !(stack.getItem() instanceof IInventoryItem inv)) {
                return null;
            }
            return ((ForgePlatformInventoryStorageHandlerUnsided) inv.getStorageHandler(stack)).getCapability();
        }, item);
    }
}
