package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.File;
import org.homedrop.core.model.User;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FilesManager implements LifeCycle{
    private static FilesManager ourInstance = new FilesManager();

    public static FilesManager getInstance() {
        return ourInstance;
    }

    private FilesManager() {
    }


    public List<File> list(String userName, String path){
        Path p = Paths.get(DevicesManager.getInstance().getDevice().getMount(),
                FilesManager.getInstance().getHome(userName)
                ,path);
        List<File> out = DBManager.getInstance().getDb().getFilesByPath(p.toString());
        return out;
    }

    public String getHome(String userName){
        //return DBManager.getInstance().getHome(userName);
        //TODO simplify
        for(User u : DBManager.getInstance().getDb().getAllUsers()){
            if(u.getName().equals(userName)){
                return u.getHome();
            }
        }
        return null;
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
