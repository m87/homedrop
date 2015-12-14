package org.homedrop.manager;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.device.Device;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/** Main configuration class */
public class ConfigManager implements LifeCycle{
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

    /** Available servers */
    public enum ServerType{APACHE}
    private static ConfigManager ourInstance = new ConfigManager();

    public static ConfigManager getInstance() {
        return ourInstance;
    }

    private ConfigManager() {
    }

    /** Loads configuration from yaml file
     *
     * @param rootPath path to main config file
     */
    public void loadConfiguration(String rootPath){
        try {
            YamlReader reader = new YamlReader(new FileReader(rootPath));
            Object object = reader.read();
            Map map = (Map)object;

            this.serverType = (String)map.get("server");
            this.serverConfigPath = (String)map.get("server-confg");
            HashMap deviceMap = (HashMap)map.get("device");

            DevicesManager.getInstance().setDevice(Device.create((String) deviceMap.get("type"),
                    "device",
                    (String) deviceMap.get("device"),
                    (String) deviceMap.get("mountpoint")));

            YamlReader usersReader = new YamlReader(new FileReader((String)map.get("users")));
            Object usersObject = usersReader.read();
            Map<String, Map> usersMap = (Map)usersObject;

            for(String m : usersMap.keySet()){
                User u = new User(m,(String)usersMap.get(m).get("pass"), (String)usersMap.get(m).get("home"));
                UsersManager.getInstance().addUser(u.getId(),u);
            }
            Map<String, String> pluginsMap = (Map)map.get("plugins");

            for(String m: pluginsMap.keySet()){
                PluginsManager.getInstance().addPlugin(pluginsMap.get(m), m);
            }



        } catch (FileNotFoundException e) {
            Log.d(LogTag.CONFIG, "File not found");
            e.printStackTrace();
        } catch (YamlException e) {
            Log.d(LogTag.CONFIG, "YAML file error");
            e.printStackTrace();
        }
    }



    //Settings
    private String serverType;
    public String getServerType() {
        return serverType;
    }

    private String serverConfigPath;

    public String getServerConfigPath() {
        return serverConfigPath;
    }

    private String systemLog;

    public String getSystemLog() {
        return systemLog;
    }

    private String sessionLogRoot;

    public String getSessionLogRoot() {
        return sessionLogRoot;
    }

    private Map<Short, Device> devices;

    public void setDevices(Map<Short, Device> devices) {
        if(this.devices != null){
            Log.w(LogTag.DEV, "Devices redefinition!", systemLog);
        }
        this.devices = devices;
    }


}
