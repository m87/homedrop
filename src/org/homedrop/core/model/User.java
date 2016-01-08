package org.homedrop.core.model;

import org.homedrop.core.model.device.Identifiable;

public abstract class User implements Identifiable {
    public abstract String getName();
    public abstract void setName(String name);
    public abstract String getPassword();
    public abstract void setPassword(String password);
    public abstract String getHome();
    public abstract void setHome(String home);
}
