package org.homedrop.manager;

import org.homedrop.core.LifeCycle;

public class UsersManager implements LifeCycle{
    private static UsersManager ourInstance = new UsersManager();

    public static UsersManager getInstance() {
        return ourInstance;
    }

    private UsersManager() {
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onExit() {

    }
}
