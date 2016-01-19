package org.homedrop.manager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.homedrop.Result;
import org.homedrop.RulesCommons;
import org.homedrop.core.utils.exceptions.ItemWithValueAlreadyExistsException;
import org.homedrop.meta.MetaFile;
import org.homedrop.meta.MetaPackage;
import org.homedrop.core.Default;
import org.homedrop.core.LifeCycle;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.RulesHelper;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.sqliteModels.FileEntity;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FilesManager implements LifeCycle {


    private static FilesManager ourInstance = new FilesManager();

    public static FilesManager getInstance() {
        return ourInstance;
    }

    private FilesManager() {
    }

    public void copy(String userName, String pathSrc, String pathDst) throws ItemNotFoundException, ItemWithValueAlreadyExistsException, IOException {
        //TODO boolean for removing entity
        DBManager.getInstance().getDb().renameFile(userName, pathSrc, pathDst);
        java.io.File fileSrc = Paths.get(getHome(userName), pathSrc).toFile();
        java.io.File fileDst = Paths.get(getHome(userName), pathDst).toFile();
        if (fileSrc.isDirectory()) {
            FileUtils.copyDirectory(fileSrc, fileDst);
        } else {
            FileUtils.copyFile(fileSrc, fileDst);
        }
    }

    public void move(String userName, String pathSrc, String pathDst) throws ItemNotFoundException, ItemWithValueAlreadyExistsException, IOException {
        DBManager.getInstance().getDb().renameFile(userName, pathSrc, pathDst);
        java.io.File fileSrc = Paths.get(getHome(userName), pathSrc).toFile();
        java.io.File fileDst = Paths.get(getHome(userName), pathDst).toFile();
        if (fileSrc.isDirectory()) {
            FileUtils.moveDirectory(fileSrc, fileDst);
        } else {
            FileUtils.moveFile(fileSrc, fileDst);
        }
    }

    public void rename(String userName, String pathSrc, String pathDst) throws ItemNotFoundException, ItemWithValueAlreadyExistsException {
        DBManager.getInstance().getDb().renameFile(userName, pathSrc, pathDst);
        java.io.File fileSrc = Paths.get(getHome(userName), pathSrc).toFile();
        java.io.File fileDst = Paths.get(getHome(userName), pathDst).toFile();
        fileSrc.renameTo(fileDst);
    }


    public boolean delete(String userName, String path) throws ItemNotFoundException {
        DBManager.getInstance().getDb().deleteFileByPath(userName, DBHelper.formatPath(path));
        FileUtils.deleteQuietly(new java.io.File(DBHelper.mapUserPathAsString(userName, path)));

        return true;
    }

    public boolean removeList(String userName, String path) throws ItemNotFoundException {
        return FileUtils.deleteQuietly(new java.io.File(Paths.get(getHome(userName), path).toString()));
    }

    public List<File> list(String userName, String path) {
        //TODO patse path as path|#tag#tag#tag
        Log.d(LogTag.DEV, path);
        List<File> out = new ArrayList<>();
        try {
            Path p = Paths.get(path);
            HDDB db = DBManager.getInstance().getDb();
            User owner = db.getUserByName(userName);
            out = db.getFilesByParentPath(p.toString(), owner);
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "Could not retrieve user's home!");
        }
        return out;
    }

    public void indexAll(String userName) {
        try {
            String home = getHome(userName);
            Collection<java.io.File> files = FileUtils.listFilesAndDirs(new java.io.File(home), TrueFileFilter.INSTANCE, null);
            for (java.io.File file : files) {
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


    public boolean prepare(MetaPackage metaPackage, String userName, int specialKey) {

        List<MetaFile> files = Arrays.asList(metaPackage.files);

        for (MetaFile file : files) {
            if (file.fileTransfered) {
                createDirsFromMeta(userName, file, specialKey, false);
            }
        }
        return true;
    }

    public boolean process(MetaPackage metaPackage, String userName, int specialKey) {

        List<MetaFile> files = Arrays.asList(metaPackage.files);
        RulesManager.getInstance().addGlobalFromMeta(metaPackage.settings);

        for (MetaFile file : files) {
            if (file.fileTransfered) {
                processFileFromMetaTransfered(userName, file, specialKey);
            } else {
                processFileFromMetaNotTransfered(userName, file, specialKey);
            }


        }
        return true;
    }

    public boolean clean(MetaPackage metaPackage) {

        return true;
    }


    public boolean processFileFromMetaNotTransfered(String userName, MetaFile file, int specialKey) {
        //check if file was transfered
        try {
            Path filePath = Paths.get(getHome(userName), file.path);
            if (!filePath.toFile().exists()) return false;

            RulesManager.getInstance().addLocalFromMeta(file);
            TagsManager.getInstance().process(file, userName);

        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "user home not found");
            return false;
        }


        return true;
    }


    public boolean createDirsFromMeta(String userName, MetaFile file, int specialKey, boolean realLocation) {
        if (!file.isDir) return true;

        try {
            String home = getHome(userName);
            if (!realLocation) {
                Path tmp = Paths.get(home, Default.MAIN_TMP, Default.BUF_TMP, "s" + String.valueOf(specialKey), file.path);
                java.io.File tmpFile = tmp.toFile();
                tmpFile.mkdirs();
            } else {
                Path real = Paths.get(home, file.path);
                java.io.File realFile = real.toFile();
                // Collection<java.io.File> files = FileUtils.listFilesAndDirs(realFile, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                //for (java.io.File f : files) {

                if (!DBManager.getInstance().getDb().fileExists(userName, DBHelper.formatPath(file.path))) {
                    File entity = new FileEntity();
                    entity.setName(realFile.getName());
                    entity.setParentPath(DBHelper.formatPath(DBHelper.removeHome(userName, realFile.getParentFile().getAbsolutePath())));
                    entity.setPath(DBHelper.formatPath(DBHelper.removeHome(userName, realFile.getAbsolutePath())));
                    entity.setOwner(DBManager.getInstance().getDb().getUserByName(userName));
                    entity.setType(File.FileType.Directory);
                    DBManager.getInstance().getDb().addFile(entity);
                }
                //}

                //add tags
                TagsManager.getInstance().process(file, userName);
                //add local rules
                RulesManager.getInstance().addLocalFromMeta(file); //TODO one beckup rule

                realFile.mkdirs();
            }


        } catch (ItemNotFoundException e) {
            return false;
        }

        return true;
    }

    public boolean processFileFromMetaTransfered(String userName, MetaFile file, int specialKey) {
        //check if file was transfered
        try {
            Path transFilePath = Paths.get(getHDPath(userName, Default.BUF_TMP), "s" + String.valueOf(specialKey), file.path);
            if (!transFilePath.toFile().exists()) {
                Log.d(LogTag.DEV, "file doesn't exist");
                return false;
            }


            //creates dirs tree with entities
            if (file.isDir) {
                createDirsFromMeta(userName, file, specialKey, true); //<< always replace settings with new
                return true;
            }

            HDDB db = DBManager.getInstance().getDb();

            //dstination relative path
            String relativePath = file.path;
            //destination absolute path
            Path absPath = Paths.get(getHome(userName), relativePath);
            java.io.File absFile = absPath.toFile();

            //TODO if exists
            //create entity

            File entity = null;
            boolean exists = DBManager.getInstance().getDb().fileExists(userName, DBHelper.formatPath(file.path));
            if (!exists) {
                entity = new FileEntity();
                entity.setName(absFile.getName());
                entity.setParentPath(DBHelper.formatPath(DBHelper.removeHome(userName, absFile.getParent())));
                entity.setPath(DBHelper.formatPath(DBHelper.removeHome(userName, absFile.getAbsolutePath())));
                entity.setOwner(DBManager.getInstance().getDb().getUserByName(userName));
                entity.setType(File.FileType.File);
            }

            //add local rules, effects existing file with same name
            RulesManager.getInstance().addLocalFromMeta(file);

            //destination absolute path
            java.io.File src = transFilePath.toFile();
            java.io.File dst = absFile;


            //get backup rule
            Rule rule = RulesHelper.getFirst(RulesCommons.BACKUP_RULE);
            //if null, replace file with new
            if (null == rule) {
                FileUtils.deleteQuietly(dst);
                FileUtils.moveFile(src, dst);
                //db.deleteFile(db.getFileByPath(relativePath, db.getUserByName(userName)));
                if (!exists) {
                    db.addFile(entity);
                }

            } else {
                //manage backup
                Backup.process(rule, userName, specialKey); //move file //TODO manage db
                FileUtils.moveFile(src, dst);
                if (!exists) {
                    db.addFile(entity);
                }
            }
            //add tags
            TagsManager.getInstance().process(file, userName);


        } catch (IOException e) {
            Log.d(LogTag.DEV, "cannot move file");
            return false;
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "user home not found");
            return false;
        }


        return true;
    }

    public static class Backup {
        public static void process(Rule rule, String userName, int specialKey) {
        }
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
