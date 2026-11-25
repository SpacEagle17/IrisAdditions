package com.spaceagle17.irisadditions.core.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log {
    private static final Logger LOGGER = LogManager.getLogger("iris_additions");
    private static final String PREFIX = "IrisAdditions: ";
    private static final String REPORT = "Report this issue on GitHub or Discord: "
            + "https://github.com/SpacEagle17/IrisAdditions/issues | https://www.euphoriapatches.com/discord: ";

    private static final int SPAM_PROTECTION_QUEUE_SIZE = 100;
    private static final LogMessageQueue RECENT = new LogMessageQueue(SPAM_PROTECTION_QUEUE_SIZE);

    private static volatile boolean debugEnabled;

    private Log() {}

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void info(String message) {
        if (RECENT.shouldLog(message)) LOGGER.info(PREFIX + message);
    }

    public static void warn(String message) {
        if (RECENT.shouldLog(message)) LOGGER.warn(PREFIX + message);
    }

    public static void error(String message) {
        if (RECENT.shouldLog(message)) LOGGER.error(PREFIX + REPORT + message);
    }

    public static void error(String message, Throwable cause) {
        if (RECENT.shouldLog(message)) LOGGER.error(PREFIX + REPORT + message, cause);
    }

    public static void debug(String message) {
        if (debugEnabled) info(message);
    }
}
