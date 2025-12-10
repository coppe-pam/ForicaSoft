package com.yourname.yourmod.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("yourmodid", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // ここでパケットを登録する
        // 例:
        // CHANNEL.registerMessage(id(), PacketExample.class, PacketExample::encode, PacketExample::decode, PacketExample::handle);

    }

    private static int id() {
        return packetId++;
    }
}
