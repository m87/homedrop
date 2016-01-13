package org.homedrop;

public class ReportElement {
    private String path;
    private int code;

    public ReportElement(String path, int code){
        this.path = path;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }
}
