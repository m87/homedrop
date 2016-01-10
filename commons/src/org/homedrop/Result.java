package org.homedrop;

/** Result system model*/
public class Result {
    public static final int OK = 1;
    public static final int ERROR = -1;

    private int code = -1;
    public Result(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
