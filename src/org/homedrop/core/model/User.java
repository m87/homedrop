package org.homedrop.core.model;

public interface User {

    String getName();
    void setName(String name);
    String getPassword();
    void setPassword(String password);
    String getHome();
    void setHome(String home);

    void setId(long id);
    long getId();
}
