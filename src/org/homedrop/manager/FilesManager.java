package org.homedrop.manager;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.homedrop.Commons;
import org.homedrop.MetaFile;
import org.homedrop.MetaPackage;
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
import java.util.Arrays;
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

    public String getHDPath(String userName, String path) throws ItemNotFoundException {
        Path p = Paths.get(getHome(userName), Default.MAIN_TMP, path);
        return p.toString();
    }


    public boolean prepare(MetaPackage metaPackage, String userName, int specialKey){

        List<MetaFile> files = Arrays.asList(metaPackage.files);

        for(MetaFile file : files){
            if(file.fileTransfered) {
                createDirsFromMeta(userName, file, specialKey, false);
            }
        }
        return true;
    }

    public boolean process(MetaPackage metaPackage, String userName, int specialKey){

        List<MetaFile> files = Arrays.asList(metaPackage.files);

            for(MetaFile file : files){
                if(file.fileTransfered) {
                    newFileFromMeta(userName, file, specialKey);
                }else{
                    editFileFromMeta(userName, file, specialKey);
                }
            }
        return true;
    }

    public boolean clean(MetaPackage metaPackage){

        return true;
    }


    public boolean editFileFromMeta(String userName, MetaFile file, int specialKey){

        return true;
    }


    public boolean createDirsFromMeta(String userName, MetaFile file, int specialKey, boolean realLocation){
        if(!file.isDir) return true;

        try {
            String home = getHome(userName);
            if(!realLocation) {
                Path tmp = Paths.get(home, Default.MAIN_TMP, Default.BUF_TMP, "s"+String.valueOf(specialKey) ,file.path);
                java.io.File tmpFile = tmp.toFile();
                tmpFile.mkdirs();
            }else {
                Path real = Paths.get(home, file.path);
                java.io.File realFile = real.toFile();
                realFile.mkdirs();
            }



        } catch (ItemNotFoundException e) {
            return false;
        }

        return true;
    }

    public boolean newFileFromMeta(String userName, MetaFile file, int specialKey) {
        if(file.isDir) {
            createDirsFromMeta(userName,file,specialKey,true);
            return true;
        }
        try{
            String path = file.path;
            Path p = Paths.get(getHome(userName), path);
            java.io.File f = p.toFile();

            File entity = new FileEntity();
            entity.setName(f.getName());
            entity.setParentPath(DBHelper.formatPath(f.getParent()));
            entity.setPath(DBHelper.formatPath(f.getPath()));
            entity.setOwner(DBManager.getInstance().getDb().getUserByName(userName));
            if(file.isDir){
                entity.setType(File.FileType.Directory);
            }else{
                entity.setType(File.FileType.File);
            }


            p = Paths.get(getHome(userName), Default.MAIN_TMP, Default.BUF_TMP,"s"+String.valueOf(specialKey), path);
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
