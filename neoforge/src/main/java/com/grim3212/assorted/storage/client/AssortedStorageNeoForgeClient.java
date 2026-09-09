package com.grim3212.assorted.storage.client;

import com.grim3212.assorted.storage.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * {@code @Mod.EventBusSubscriber} no longer nests under {@code @Mod} and no longer picks a bus, and
 * hanging client setup off {@code FMLConstructModEvent} is not the idiom any more: {@code @Mod}
 * takes a {@code dist} now, so a client-only entry point is simply a second {@code @Mod} class for
 * the same mod id whose constructor runs only on the client.
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class AssortedStorageNeoForgeClient {

    public AssortedStorageNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        StorageClient.init();
    }
}
