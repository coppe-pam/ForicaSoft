package com.yourname.yourmod.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ForicaCore のパケット → バイト列にエンコードするクラス
 * ここでは超軽量プロトコルとして
 *
 * [packetId:int][payload bytes]
 *
 * の形で送る
 */
public class SimplePacketEncoder extends MessageToByteEncoder<SimplePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SimplePacket packet, ByteBuf out) {
        try {
            // パケットID
            out.writeInt(packet.getPacketId());

            // 本文データ
            byte[] payload = packet.getPayload();
            out.writeInt(payload.length);
            out.writeBytes(payload);

        } catch (Exception e) {
            System.err.println("[ForicaCore] Packet encode error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
