package org.homedrop.core.model;

import org.homedrop.core.utils.Identifiable;

import java.util.Date;

public abstract class Rule implements Identifiable {
    @Override
    public abstract long getId();

    @Override
    public abstract void setId(long id);

    public abstract File getFile();
    public abstract void setFile(File file);

    public abstract User getOwner();
    public abstract void setOwner(User owner);

    public abstract String getBody();
    public abstract void setBody(String body);

    public abstract Date getHoldsSince();
    public abstract void setHoldsSince(Date date);

    public abstract Date getHoldsUntil();
    public abstract void setHoldsUntil(Date date);

    public long getOwnerId() {
        return getOwner().getId();
    }

    public Long getFileId() {
        File file = getFile();
        return (null != file) ? file.getId() : null;
    }
}
