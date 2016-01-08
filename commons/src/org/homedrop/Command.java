package org.homedrop;

/** Command model*/
public class Command {
    public static final String HD_ADD_FILE = "HDADD";
    public static final String HD_RM_FILE = "HDRM";


    private String name;
    private String[] args;
    public Command(String name, String[] args){
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }
}
