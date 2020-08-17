package me.badbones69.crazycrates.api;

public class CcLogger {

    private static final CcLogger instance = new CcLogger();

    private static FileManager fileManager = FileManager.getInstance();

    public static CcLogger getInstance() {
        return instance;
    }

    public void log(String message) {
        if (fileManager.isLogging()) {
            System.out.println(fileManager.getPrefix() + message);
        }
    }
}
