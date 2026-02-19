package me.matthewedevelopment.atheriallib;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class DebugLog {
    private static final String LOG_PATH = "debug-45c4d3.log";
    private static final String SESSION = "45c4d3";
    private static final Logger LOGGER = Logger.getLogger("AtherialLib-Debug");

    public static void log(String hypothesisId, String location, String message, String dataJson) {
        String consoleMsg = "[DEBUG-" + SESSION + "] [" + hypothesisId + "] " + location + " | " + message + " | " + (dataJson != null ? dataJson : "{}");
        LOGGER.info(consoleMsg);

        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_PATH, true))) {
            pw.println("{\"sessionId\":\"" + SESSION + "\",\"hypothesisId\":\"" + hypothesisId
                + "\",\"location\":\"" + location + "\",\"message\":\"" + message
                + "\",\"data\":" + (dataJson != null ? dataJson : "{}")
                + ",\"timestamp\":" + System.currentTimeMillis() + "}");
        } catch (Exception ignored) {}
    }
}
