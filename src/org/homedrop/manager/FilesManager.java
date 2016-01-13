package org.homedrop.manager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.File;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.sqliteModels.FileEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilesManager implements LifeCycle{
    private static FilesManager ourInstance = new FilesManager();

    public static FilesManager getInstance() {
        return ourInstance;
    }

    private FilesManager() {
    }

    public boolean removeList(String userName, String path) throws ItemNotFoundException{
            return FileUtils.deleteQuietly(new java.io.File(Paths.get(getHome(userName),path).toString()));
    }

    public List<File> list(String userName, String path) {
        List<File> out = new ArrayList<>();
        try {
            Path p = Paths.get(FilesManager.getInstance().getHome(userName), path);
            Log.d(LogTag.DEV, p.toString());
            out = DBManager.getInstance().getDb().getFilesByPath(p.toString());
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "Could not retrieve user's home!");
        }
        return out;
    }
    public class EmptyFilter extends EmptyFileFilter{

    }

    public void indexAll(String userName){
        try {
            Collection<java.io.File> files = FileUtils.listFilesAndDirs(new java.io.File(getHome(userName)), new EmptyFilter(), null);
            for(java.io.File file : files){
                FileEntity fe = new FileEntity();
                fe.setPath(file.getPath());
                fe.setOwner(DBManager.getInstance().getDb().getUserByName(userName));
                fe.setName(file.getName());
                DBManager.getInstance().getDb().addFile(fe);
            }

        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, e.getMessage());
        }
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
