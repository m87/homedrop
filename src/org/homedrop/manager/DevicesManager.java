package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.Device;


public class DevicesManager implements LifeCycle{
    private Device device;
    private static DevicesManager ourInstance = new DevicesManager();

    public static DevicesManager getInstance() {
        return ourInstance;
    }

    public void setDevice(Device device){
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    private DevicesManager() {
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
