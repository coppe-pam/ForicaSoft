package foricacore;

import foricacore.config.ConfigManager;
import foricacore.log.Logger;

public class ForicaCoreMain {

    public static void main(String[] args) {
        Logger.info("==== ForicaCore Starting... ====");

        // コンフィグ読み込み
        ConfigManager.load();

        // ローンチャー起動
        ServerLauncher launcher = new ServerLauncher();
        launcher.startServer();

        Logger.info("==== ForicaCore Shutdown ====");
    }
}
