package org.homedrop.manager;


import org.homedrop.core.LifeCycle;

/** Plugins manager */
public class PluginsManager implements LifeCycle {
    private static PluginsManager ourInstance = new PluginsManager();

    public static PluginsManager getInstance() {
        return ourInstance;
    }

    private PluginsManager() {
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
