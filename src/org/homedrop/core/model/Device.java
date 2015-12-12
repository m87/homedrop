package org.homedrop.core.model;


public abstract class Device {
    public enum DeviceType {LOGICAL, PRIMARY}
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
    public abstract void mount();
    public abstract void umount();

    public static Device create(String type, String name, String dev, String mount){
        if(type.toLowerCase().equals(DeviceType.LOGICAL.name().toLowerCase())) return new LogicalDevice(name,dev,mount);
        if(type.toLowerCase().equals(DeviceType.PRIMARY.name().toLowerCase())) return new PrimaryDevice(name,dev,mount);
        return null;
    }


    public short getID() {
        return id;
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
