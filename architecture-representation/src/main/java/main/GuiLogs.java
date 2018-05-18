package main;

import logs.log_log.LogLog;

public class GuiLogs {
    
    private static LogLog logger;

    public static LogLog getLogger() {
        return logger;
    }

    public static void setLogger(LogLog ll) {
        logger = ll;
    }
    
}
