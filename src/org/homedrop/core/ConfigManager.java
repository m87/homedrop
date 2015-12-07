package org.homedrop.core;

public class ConfigManager {
    private static ConfigManager ourInstance = new ConfigManager();

    public static ConfigManager getInstance() {
        return ourInstance;
    }

    private ConfigManager() {
    }

    public void loadConfiguration(String rootPath){

    }
}
