package org.homedrop;

/** Reuqest system model */
public class Request {
    private String userName;
    private Command command;

    public Request(Command command, String userName){
        this.command = command;
        this.userName = userName;
    }

    public Command getCommand() {
        return command;
    }

    public String getUserName() {
        return userName;
    }
}
