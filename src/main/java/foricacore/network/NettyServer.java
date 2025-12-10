package foricacore.network;

import foricacore.log.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(int port) {
        this.port = port;
    }

    // ===== サーバー起動 =====
    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup(1);    // 接続受付専用（1スレッドでOK）
        workerGroup = new NioEventLoopGroup();    // パケット処理用（CPUに合わせて自動）

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)  // NIOで軽量
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // パイプライン初期化
                            ChannelPipeline pipeline = ch.pipeline();

                            // 将来ここに PacketDecoder / Encoder を追加
                            // pipeline.addLast("decoder", new PacketDecoder());
                            // pipeline.addLast("encoder", new PacketEncoder());

                            pipeline.addLast("handler", new PacketHandler());
                        }
                    })
                    .childOption(ChannelOption.TCP_NODELAY, true) // 遅延なし
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(port).sync();
            Logger.info("Netty server started on port " + port);

            // サーバーを閉じるまで待機
            future.channel().closeFuture().sync();

        } finally {
            shutdown();
        }
    }

    // ===== サーバー停止 =====
    public void stop() {
        shutdown();
    }

    private void shutdown() {
        Logger.info("Shutting down Netty server...");
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
    }
}
