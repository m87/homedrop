package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.util.Map;
import java.util.TreeMap;

public class UsersManager implements LifeCycle{
    private static UsersManager ourInstance = new UsersManager();
    Map<Long, User> users ;

    public Map<Long, User> getUsers() {
        return users;
    }

    public static UsersManager getInstance() {
        return ourInstance;
    }

    public void addUser(Long id, User user){
        for(User u : users.values()){
            if(u.getId() == user.getId()) continue;
            if(user.getHome().equals(u.getHome())) {
                Log.w(LogTag.CONFIG, "Same home folder: " +user.getLogin() + " and " + u.getLogin() );
                return;
            }
            if(user.getLogin().equals(u.getLogin())) {
                Log.w(LogTag.CONFIG, "Same login: " +user.getLogin() + " and " + u.getLogin() );
                return;
            }
        }
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
