package org.homedrop.manager;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.homedrop.core.model.Device;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/** Main configuration class */
public class ConfigManager {
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
            Map<String, Map> devices = ((Map) map.get("devices-def"));
            for(String m :devices.keySet()){
                DevicesManager.getInstance().addDevice(Device.create(
                        (String)devices.get(m).get("type"),
                        m,
                        (String)devices.get(m).get("device"),
                        (String)devices.get(m).get("mountpoint")));
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
