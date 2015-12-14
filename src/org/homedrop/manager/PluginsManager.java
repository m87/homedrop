package org.homedrop.manager;


import org.homedrop.Plugin;
import org.homedrop.core.LifeCycle;
import org.homedrop.plugin.PluginLoader;

import java.util.Map;
import java.util.TreeMap;

/** Plugins manager */
public class PluginsManager implements LifeCycle {
    private Map<String, Plugin> plugins;
    private static PluginsManager ourInstance = new PluginsManager();

    public static PluginsManager getInstance() {
        return ourInstance;
    }



    public void addPlugin(String path, String name){
        plugins.put(name, PluginLoader.loadFromJar(path,name));

    }

    private PluginsManager() {
        plugins = new TreeMap<>();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onExit() {

    }
}
