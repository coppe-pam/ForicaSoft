package foricacore.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ANSIカラー
    private static final String RESET = "\u001B[0m";
    private static final String RED   = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    private static String formatTime() {
        return "[" + LocalDateTime.now().format(TIME) + "]";
    }

    public static void info(String msg) {
        System.out.println(formatTime() + " " + GREEN + "[INFO] " + RESET + msg);
    }

    public static void warn(String msg) {
        System.out.println(formatTime() + " " + YELLOW + "[WARN] " + RESET + msg);
    }

    public static void error(String msg) {
        System.out.println(formatTime() + " " + RED + "[ERROR] " + RESET + msg);
    }

    public static void debug(String msg) {
        // 本番では切れるように後で設定対応してもOK
        System.out.println(formatTime() + " " + CYAN + "[DEBUG] " + RESET + msg);
    }
}
