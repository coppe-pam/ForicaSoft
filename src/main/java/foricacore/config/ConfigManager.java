package foricacore.config;

import foricacore.log.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "server.properties";
    private static Properties props = new Properties();

    // ====== ロード ======
    public static void load() {
        try {
            Logger.info("Loading server.properties...");

            // ファイルが存在しないなら自動生成
            java.io.File file = new java.io.File(CONFIG_FILE);
            if (!file.exists()) {
                Logger.info("server.properties not found. Creating default config...");
                createDefaultConfig();
            }

            FileInputStream in = new FileInputStream(CONFIG_FILE);
            props.load(in);
            in.close();

            Logger.info("Config loaded!");
        } catch (Exception e) {
            Logger.error("Failed to load config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ====== デフォルト設定を自動生成 ======
    private static void createDefaultConfig() {
        props.setProperty("server-port", "25565");
        props.setProperty("motd", "ForicaCore Server");
        props.setProperty("max-players", "20");
        props.setProperty("level-type", "flat"); // 生成はフラットのみ

        save();
    }

    // ====== 保存 ======
    public static void save() {
        try {
            FileOutputStream out = new FileOutputStream(CONFIG_FILE);
            props.store(out, "ForicaCore server config");
            out.close();
        } catch (IOException e) {
            Logger.error("Failed to save config: " + e.getMessage());
        }
    }

    // ====== 設定取得系 ======

    public static int getPort() {
        return Integer.parseInt(props.getProperty("server-port", "25565"));
    }

    public static String getMOTD() {
        return props.getProperty("motd", "ForicaCore Server");
    }

    public static int getMaxPlayers() {
        return Integer.parseInt(props.getProperty("max-players", "20"));
    }

    public static String getWorldType() {
        return props.getProperty("level-type", "flat");
    }
}
