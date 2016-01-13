package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.ModelHelpers;
import org.homedrop.thirdParty.db.sqliteModels.UserEntity;

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


    public void loadUsers(){
        Map<String, Map> usersMap = ConfigManager.getInstance().getUsersMap();
        for(String m : usersMap.keySet()){
                User u = new UserEntity();
                ModelHelpers.setUserFields(u, m, (String) usersMap.get(m).get("pass"), (String) usersMap.get(m).get("home"));
                UsersManager.getInstance().addUser(u.getId(),u);
        }
    }

    public void addUser(Long id, User user){
        DBManager.getInstance().getDb().addUser(user);
        for(User u : users.values()){
            if(u.getId() == user.getId()) continue;
            if(user.getHome().equals(u.getHome())) {
                Log.w(LogTag.CONFIG, "Same home folder: " +user.getName() + " and " + u.getName() );
                return;
            }
            if(user.getName().equals(u.getName())) {
                Log.w(LogTag.CONFIG, "Same login: " +user.getName() + " and " + u.getName() );
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
