package org.homedrop.core.model;

/** Device model*/
public abstract class Device {
    /** Devices types.
     *   Primary: real device; pendrive, hard drive etc.
     *   Logical: dedicated folder, mountpoint represents link name in symbolic link
     */
    public enum DeviceType {LOGICAL, PRIMARY}
    private static short ID=0;
    private static short id;
    private String name;
    private String dev;
    private String mount;

    /**
     *
     * @param name device name
     * @param dev path to device
     * @param mount path to mountpoint or link name
     */
    public Device(String name, String dev, String mount){
        id = ID++;
        this.name = name;
        this.dev = dev;
        this.mount = mount;
    }

    public abstract void mount();
    public abstract void umount();

    /** Device factory*/
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
