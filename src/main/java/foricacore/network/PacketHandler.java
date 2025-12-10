package com.yourname.yourmod.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * ForicaCore のパケット処理本体。
 * packetId ごとに処理を登録しておけば、めちゃ軽く動く。
 */
public class ServerPacketHandler extends SimpleChannelInboundHandler<SimplePacket> {

    // packetId → 処理関数
    private static final Map<Integer, BiConsumer<ChannelHandlerContext, SimplePacket>> handlers = new HashMap<>();

    /**
     * 外部（プラグイン、MOD、アドオン）からハンドラ登録可能
     */
    public static void register(int packetId, BiConsumer<ChannelHandlerContext, SimplePacket> handler) {
        handlers.put(packetId, handler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SimplePacket packet) {
        int id = packet.getPacketId();
        BiConsumer<ChannelHandlerContext, SimplePacket> handler = handlers.get(id);

        if (handler != null) {
            try {
                handler.accept(ctx, packet);
            } catch (Exception e) {
                System.err.println("[ForicaCore] PacketHandler error (id=" + id + "): " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("[ForicaCore] Unknown packetId received: " + id);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("[ForicaCore] Netty error: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
