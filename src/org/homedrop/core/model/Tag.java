package org.homedrop.core.model;


import org.homedrop.core.utils.Identifiable;

public abstract class Tag implements Identifiable {
    public abstract String getName();
    public abstract void setName(String name);
}
