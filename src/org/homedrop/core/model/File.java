package org.homedrop.core.model;

import org.homedrop.core.utils.Identifiable;

import java.util.Date;

public abstract class File implements Identifiable {

    public enum FileType { File, Directory }

    public abstract long getId();
    public abstract void setId(long id);

    public abstract String getName();
    public abstract void setName(String name);

    public abstract String getParentPath();
    public abstract void setParentPath(String path);

    public abstract String getPath();
    public abstract void setPath(String path);

    public abstract long getCheckSum();
    public abstract void setCheckSum(long checkSum);

    public abstract long getVersion();
    public abstract void setVersion(long version);

    public abstract Date getLastChange();
    public abstract void setLastChange(Date lastChange);

    public abstract User getOwner();
    public abstract void setOwner(User owner);

    public abstract FileType getType();
    public abstract void setType(FileType type);

    public long getOwnerId() {
        User owner = getOwner();
        return owner.getId();
    }

    public boolean isDirectory() {
        return getType() ==  FileType.Directory;
    }
}
