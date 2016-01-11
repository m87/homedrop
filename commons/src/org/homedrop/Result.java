package org.homedrop;

/** Result system model*/
public class Result {
    public static final int OK = 1;
    public static final int ERROR = -1;
    public static final int IS_FILE = 2;
    public static final int UNSUPPORTED = -3;

    private String message;
    private int code = -1;
    public Result(int code, String message){
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
