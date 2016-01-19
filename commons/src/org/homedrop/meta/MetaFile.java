package org.homedrop.meta;

public class MetaFile {
    public boolean fileTransfered;

    public boolean isDir;
    public String path;
    public MetaRule[] rules;
    public MetaTag[] tags;
    public MetaFile(String path){
        this.path = path;
    }
}
