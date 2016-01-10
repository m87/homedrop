package org.homedrop;

/** Reuqest system model */
public class Request {
    private Command command;

    public Request(Command command){
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
