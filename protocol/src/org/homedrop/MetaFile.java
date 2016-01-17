package org.homedrop;

public class MetaFile {
    public boolean isDir;
    public String path;
    public MetaRule[] rules;
    public MetaTag[] tags;
    public MetaFile(String path){
        this.path = path;
    }
}
