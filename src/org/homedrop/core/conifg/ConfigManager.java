package org.homedrop.core.conifg;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.homedrop.core.model.Device;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

public class ConfigManager {
    public enum ServerType{APACHE}
    private static ConfigManager ourInstance = new ConfigManager();

    public static ConfigManager getInstance() {
        return ourInstance;
    }

    private ConfigManager() {
    }

    public void loadConfiguration(String rootPath){
        try {
            YamlReader reader = new YamlReader(new FileReader(rootPath));
            Object object = reader.read();
            Map map = (Map)object;

            this.serverType = (String)map.get("server");
            this.serverConfigPath = (String)map.get("server-confg");

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
