package service.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLog {
    static class MyLogLevels {
        public static final Level OPERATION = Level.forName("OPERATION", 350);
        public static final Level PROCESS = Level.forName("PROCESS", 450);
        public static final Level MYERROR = Level.forName("MYERROR", 100);

    }

    private static final Logger LOG = LogManager.getRootLogger();
    private static final Logger LOG_OPERATION = LogManager.getLogger("OPERATION");
    private static final Logger LOG_PROCESS = LogManager.getLogger("PROCESS");
    private static final Logger LOG_MYERROR = LogManager.getLogger("MYERROR");

    public static void Log(String out) {
        LOG.log(Level.INFO, out);
    }

    public static void LogProcess(String out) {
        LOG_PROCESS.log(MyLogLevels.PROCESS, out);
    }

    public static void LogOperation(String out) {
        LOG_OPERATION.log(MyLogLevels.OPERATION, out);
    }

    public static void LogMyError(String out) {
        LOG_MYERROR.log(MyLogLevels.MYERROR, out);
    }
}
