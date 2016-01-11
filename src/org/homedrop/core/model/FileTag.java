package org.homedrop.core.model;

import org.homedrop.core.utils.Identifiable;

public abstract class FileTag implements Identifiable {
    private long userId;
    private long tagId;

    public abstract long getUserId();
    public abstract void setUserId(long userId);

    public abstract long getTagId();
    public abstract void setTagId(long tagId);
}
