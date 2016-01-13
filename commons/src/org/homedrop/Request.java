package org.homedrop;

/** Reuqest system model */
public class Request {
    private String userName;
    private Command command;
    private int specialKey;

    public Request(Command command, String userName, int specialKey){
        this.command = command;
        this.userName = userName;
        this.specialKey = specialKey;
    }

    public Command getCommand() {
        return command;
    }

    public String getUserName() {
        return userName;
    }

    public int getSpecialKey() {
        return specialKey;
    }
}
