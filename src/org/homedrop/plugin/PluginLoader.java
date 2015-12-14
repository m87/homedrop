package org.homedrop.plugin;

import org.homedrop.Plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/** Plugins loader*/
public class PluginLoader {
    public static Plugin loadFromJar(String path, String name){
        File file = new File(path);
        try {
            URL url = file.toURI().toURL();
            ClassLoader loader = new URLClassLoader(new URL[]{url});
            Class cls = loader.loadClass(name);
            Object obj = cls.newInstance();
            if(obj instanceof Plugin){
                return (Plugin) obj;
            }else{
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;

    }

}
