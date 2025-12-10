package com.yourname.yourmod.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ForicaCore の受信パケットを復元するデコーダ。
 *
 * [packetId:int][payloadLength:int][payload:byte[]]
 */
public class SimplePacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            // まだデータが不足しているなら待つ
            if (in.readableBytes() < 8) { 
                return;
            }

            in.markReaderIndex();

            int packetId = in.readInt();
            int length = in.readInt();

            // 負荷対策 & 攻撃対策（DoS防止）
            if (length < 0 || length > 5_000_000) {
                System.err.println("[ForicaCore] Packet length abnormal: " + length);
                ctx.close();
                return;
            }

            // 全部来てないなら待つ
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            byte[] payload = new byte[length];
            in.readBytes(payload);

            SimplePacket packet = new SimplePacket(packetId, payload);
            out.add(packet);

        } catch (Exception e) {
            System.err.println("[ForicaCore] Packet decode error: " + e.getMessage());
            e.printStackTrace();
            ctx.close();
        }
    }
}
