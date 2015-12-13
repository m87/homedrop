package org.homedrop.manager;

public class UsersManager {
    private static UsersManager ourInstance = new UsersManager();

    public static UsersManager getInstance() {
        return ourInstance;
    }

    private UsersManager() {
    }
}
