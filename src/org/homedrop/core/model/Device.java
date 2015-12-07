package org.homedrop.core.model;

public class Device {
    private static short ID=0;
    private static short id;
    private String name;
    private String dev;
    private String mount;


    public Device(String name, String dev, String mount){
        id = ID++;
        this.name = name;
        this.dev = dev;
        this.mount = mount;
    }


    public static short getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDev() {
        return dev;
    }

    public String getMount() {
        return mount;
    }
}
