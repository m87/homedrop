package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.User;

import java.util.Map;
import java.util.TreeMap;

public class UsersManager implements LifeCycle{
    private static UsersManager ourInstance = new UsersManager();
    Map<Integer, User> users ;

    public static UsersManager getInstance() {
        return ourInstance;
    }

    public void addUser(Integer id, User user){
        users.put(id,user);
    }

    private UsersManager() {
        users = new TreeMap<>();
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
