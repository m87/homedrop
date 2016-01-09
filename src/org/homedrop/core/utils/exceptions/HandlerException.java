package org.homedrop.core.utils.exceptions;

public class HandlerException extends Exception{
    @Override
    public String getMessage() {
        return "Invalid arguments or name";
    }
}
