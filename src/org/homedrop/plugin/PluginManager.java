package org.homedrop.plugin;

public class PluginManager {
    private static PluginManager ourInstance = new PluginManager();

    public static PluginManager getInstance() {
        return ourInstance;
    }

    private PluginManager() {
    }
}
