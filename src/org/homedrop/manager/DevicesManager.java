package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.Device;

import java.util.Map;
import java.util.TreeMap;

public class DevicesManager implements LifeCycle{
    private Map<Short, Device> devices;
    private static DevicesManager ourInstance = new DevicesManager();

    public static DevicesManager getInstance() {
        return ourInstance;
    }

    public void addDevice(Device device){
        devices.put(device.getID(),  device);
    }

    public Map<Short, Device> getDevices() {
        return devices;
    }

    private DevicesManager() {
        devices = new TreeMap<>();
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
