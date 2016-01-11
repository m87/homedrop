package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.File;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;

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

    public List<File> list(String userName, String path) {
        List<File> out = new ArrayList<>();
        try {
            Path p = Paths.get(FilesManager.getInstance().getHome(userName), path);
            out = DBManager.getInstance().getDb().getFilesByPath(p.toString());
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "Could not retrieve user's home!");
        }
        return out;
    }

    public String getHome(String userName) throws ItemNotFoundException {
        HDDB db = DBManager.getInstance().getDb();
        String home = db.getUserByName(userName).getHome();
        return home;
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
