package org.homedrop.core.model;

import org.homedrop.core.utils.Identifiable;
import org.homedrop.thirdParty.db.sqliteModels.UserEntity;

import java.sql.Date;
import java.util.List;

public abstract class File implements Identifiable {
    public abstract long getId();
    public abstract void setId(long id);

    public abstract String getName();
    public abstract void setName(String name);

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

    public long getOwnerId() {
        User owner = getOwner();
        return owner.getId();
    }
}
