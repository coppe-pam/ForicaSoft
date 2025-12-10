package foricacore;

import foricacore.log.Logger;
import foricacore.network.NettyServer;
import foricacore.player.PlayerManager;
import foricacore.world.World;
import foricacore.world.FlatWorldGenerator;
import foricacore.config.ConfigManager;

public class ServerLauncher {

    private NettyServer nettyServer;
    private World world;

    public void startServer() {
        Logger.info("Starting ForicaCore server...");

        // ==== 1. ワールド生成 / 読み込み ====
        Logger.info("Loading world...");
        FlatWorldGenerator generator = new FlatWorldGenerator();
        world = generator.generate("world");
        Logger.info("World loaded: " + world.getName());

        // ==== 2. PlayerManager 初期化 ====
        Logger.info("Initializing player manager...");
        PlayerManager.initialize(world);

        // ==== 3. Netty サーバー起動 ====
        int port = ConfigManager.getPort();  // server.properties から取得
        Logger.info("Starting Netty server on port " + port + "...");
        nettyServer = new NettyServer(port);

        try {
            nettyServer.start();
            Logger.info("Server is now running!");
        } catch (Exception e) {
            Logger.error("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }

        // 終了処理は手動（Ctrl+C）
    }

    public void shutdown() {
        Logger.info("Shutting down server...");
        if (nettyServer != null) {
            nettyServer.stop();
        }
        Logger.info("Goodbye!");
    }
}
