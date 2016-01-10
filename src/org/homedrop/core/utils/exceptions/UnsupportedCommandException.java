package org.homedrop.core.utils.exceptions;

public class UnsupportedCommandException extends Exception{
    @Override
    public String getMessage() {
        return "unsupported command";
    }
}
