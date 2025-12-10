package com.yourname.yourmod.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class NettyServer {

    private final int port;
    private Channel serverChannel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    // プラグイン・MODが自由にハンドラを追加できるように
    private Consumer<ChannelPipeline> pipelineInjector = pipeline -> {};

    public NettyServer(int port) {
        this.port = port;
    }

    /**
     * 外部（プラグイン）が Netty Pipeline に自分のハンドラを差し込める
     */
    public void setPipelineInjector(Consumer<ChannelPipeline> injector) {
        this.pipelineInjector = injector;
    }

    /**
     * サーバーを起動
     */
    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);     // 接続受付
        workerGroup = new NioEventLoopGroup();    // 実データ処理

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            // Idle timeout → 切断処理
                            pipeline.addLast("idleStateHandler",
                                    new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS)
                            );

                            // パケットのエンコード/デコード
                            pipeline.addLast("packetEncoder", new SimplePacketEncoder());
                            pipeline.addLast("packetDecoder", new SimplePacketDecoder());

                            // パケット処理ハンドラ
                            pipeline.addLast("packetHandler", new ServerPacketHandler());

                            // プラグイン側インジェクション
                            pipelineInjector.accept(pipeline);
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            serverChannel = future.channel();
            System.out.println("[ForicaCore] Netty server started on port " + port);

        } catch (Exception e) {
            System.err.println("[ForicaCore] Failed to start Netty server");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * サーバー停止
     */
    public void stop() {
        try {
            if (serverChannel != null) {
                serverChannel.close().sync();
                System.out.println("[ForicaCore] Netty channel closed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();

        System.out.println("[ForicaCore] Netty server fully stopped.");
    }

    /**
     * チャンネルを取得（必要なときのみ）
     */
    public Channel getChannel() {
        return serverChannel;
    }
}
