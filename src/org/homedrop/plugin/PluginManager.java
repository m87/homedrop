package org.homedrop.plugin;


import org.homedrop.core.LifeCycle;

/** Plugins manager */
public class PluginManager implements LifeCycle {
    private static PluginManager ourInstance = new PluginManager();

    public static PluginManager getInstance() {
        return ourInstance;
    }

    private PluginManager() {
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onExit() {

    }
}
