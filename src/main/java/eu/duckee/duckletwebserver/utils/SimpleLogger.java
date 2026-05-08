package eu.duckee.duckletwebserver.utils;

public class SimpleLogger {

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    private static Level currentLevel = Level.DEBUG;

    public static void setLevel(Level level) {
        currentLevel = level;
    }

    private static void log(Level level, String message) {
        //if (level.ordinal() < currentLevel.ordinal()) return;

        String time = java.time.LocalDateTime.now().toString();
        String thread = Thread.currentThread().getName();

        System.out.println(
                "[" + time + "]" +
                        " [" + thread + "]" +
                        " [" + level + "] " +
                        message
        );
    }

    public static void line() {

    }

    public static void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        log(Level.WARN, msg);
    }

    public static void error(String msg) {
        log(Level.ERROR, msg);
    }

    public static void error(String msg, Throwable t) {
        log(Level.ERROR, msg);
        t.printStackTrace(System.out);
    }
}