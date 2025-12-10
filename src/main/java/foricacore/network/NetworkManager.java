package com.yourname.yourmod.network;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class NetworkManager {

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.register();
        });
    }
}
