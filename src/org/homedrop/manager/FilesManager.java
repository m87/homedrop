package org.homedrop.manager;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.homedrop.MetaFile;
import org.homedrop.core.Default;
import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.File;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.SqliteHDDB;
import org.homedrop.thirdParty.db.sqliteModels.FileEntity;

import java.io.IOException;
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


    public boolean delete(String userName, String path) throws ItemNotFoundException {
        FileUtils.deleteQuietly(new java.io.File(DBHelper.mapUserPathAsString(userName, path)));

        return true;
    }

    public boolean removeList(String userName, String path) throws ItemNotFoundException{
            return FileUtils.deleteQuietly(new java.io.File(Paths.get(getHome(userName),path).toString()));
    }

    public List<File> list(String userName, String path) {
        List<File> out = new ArrayList<>();
        try {
            Path p = Paths.get(FilesManager.getInstance().getHome(userName), path);
            Log.d(LogTag.DEV, p.toString());
            out = DBManager.getInstance().getDb().getFilesByParentPath(p.toString());
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "Could not retrieve user's home!");
        }
        return out;
    }
    public class EmptyFilter extends EmptyFileFilter{

    }

    public void indexAll(String userName){
        try {
            String home = getHome(userName);
            Collection<java.io.File> files = FileUtils.listFilesAndDirs(new java.io.File(home), new EmptyFilter(), null);
            for(java.io.File file : files){
                FileEntity fe = new FileEntity();
                fe.setPath(DBHelper.removeHome(userName, file.getPath()));
                fe.setOwner(DBManager.getInstance().getDb().getUserByName(userName));
                fe.setName(file.getName());
                fe.setParentPath(file.getParent());
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

    public String getTmpPath(String userName, String path) throws ItemNotFoundException {
        Path p = Paths.get(getHome(userName), Default.MAIN_TMP, Default.BUF_TMP, path);
        return p.toString();
    }


    public boolean createDirsFromMeta(String userName, MetaFile file, int specialKey){
        if(!file.isDir) //return true;
        {
try {
            String home = getHome(userName);
            Path tmp = Paths.get(home, Default.MAIN_TMP, Default.BUF_TMP, file.path + "." + String.valueOf(specialKey));

            java.io.File tmpFile = tmp.toFile();
            tmpFile.createNewFile();



        } catch (ItemNotFoundException e) {
            return false;
        } catch (IOException e) {
}
        }

        try {
            String home = getHome(userName);
            Path tmp = Paths.get(home, Default.MAIN_TMP, Default.BUF_TMP, file.path);
            Path real =Paths.get(home, file.path);

            java.io.File tmpFile = tmp.toFile();
            java.io.File realFile = real.toFile();

            if(tmpFile.getParentFile()!=null){
                tmpFile.getParentFile().mkdirs();
            }
            if(realFile.getParentFile()!=null){
                realFile.getParentFile().mkdirs();
            }



        } catch (ItemNotFoundException e) {
            return false;
        }

        return true;
    }

    public boolean addFileFromMeta(String userName, MetaFile file, int specialKey) {
        if(file.isDir) return true;
        try{
        String path = file.path;
        Path p = Paths.get(getHome(userName), path);
        java.io.File f = p.toFile();

        File entity = new FileEntity();
        entity.setName(f.getName());
        entity.setParentPath(DBHelper.formatPath(f.getParent()));
        entity.setPath(DBHelper.formatPath(f.getPath()));
        entity.setOwner(DBManager.getInstance().getDb().getUserByName(userName));

        p = Paths.get(getHome(userName), Default.MAIN_TMP, Default.BUF_TMP, path+"."+String.valueOf(specialKey));
        Path pdst = Paths.get(getHome(userName), path);
        java.io.File src = p.toFile();
        java.io.File dst = pdst.toFile();
            FileUtils.deleteQuietly(dst); //TODO <<<<<<<<<<<<<<<<<<<<<<
            FileUtils.moveFile(src, dst);
            DBManager.getInstance().getDb().addFile(entity);

        } catch (IOException e) {
            Log.d(LogTag.DEV, "cannot move file");
            return false;
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "user not found");
            return false;
        }


        return true;
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
