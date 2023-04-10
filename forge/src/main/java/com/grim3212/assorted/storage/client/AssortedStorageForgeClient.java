package com.grim3212.assorted.storage.client;

import com.grim3212.assorted.storage.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
public class AssortedStorageForgeClient {

    @SubscribeEvent
    public static void initClientSide(final FMLConstructModEvent event) {
        StorageClient.init();
    }
}
