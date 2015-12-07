package org.homedrop.core.conifg;

import org.homedrop.core.model.Device;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.util.Map;

public class ConfigManager {
    private static ConfigManager ourInstance = new ConfigManager();

    public static ConfigManager getInstance() {
        return ourInstance;
    }

    private ConfigManager() {
    }

    public void loadConfiguration(String rootPath){

    }



    //Settings
    private String serverType;
    public String getServerType() {
        return serverType;
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
