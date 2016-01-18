package org.homedrop;

public class HandlerException extends Exception{
    @Override
    public String getMessage() {
        return "Invalid arguments or name";
    }
}
