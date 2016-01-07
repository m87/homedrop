package org.homedrop.thirdParty.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.manager.UsersManager;
import org.homedrop.thirdParty.db.sqliteModels.*;
import sun.awt.SunHints;

import java.sql.SQLException;
import java.util.List;

public class SqliteHDDB implements HDDB {
    private ConnectionSource connectionSource;
    private Dao<UserEntity,Long> userDao;
    private Dao<TagEntity,Long> tagDao;
    private Dao<FileEntity,Long> fileDao;
    private Dao<FileTagEntity,Long> fileTagDao;
    private Dao<RuleEntity,Long> ruleDao;

    public SqliteHDDB(JdbcConnectionSource connectionSource){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.connectionSource = connectionSource;
        try {
            userDao = DaoManager.createDao(connectionSource, UserEntity.class);
            fileDao = DaoManager.createDao(connectionSource, FileEntity.class);
            tagDao = DaoManager.createDao(connectionSource, TagEntity.class);
            ruleDao = DaoManager.createDao(connectionSource, RuleEntity.class);
            fileTagDao = DaoManager.createDao(connectionSource, FileTagEntity.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, UserEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, FileEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, TagEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, FileTagEntity.class);
            TableUtils.createTableIfNotExists(connectionSource, RuleEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade() {

    }

    @Override
    public void onDrop() {

    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public List<Rule> getAllRules() {
        return null;
    }


    @Override
    public void addFile(File file) {

    }

    @Override
    public void deleteFile(File file) {

    }

    @Override
    public void deleteFileById(long id) {

    }

    @Override
    public void editFile(File file) {

    }

    @Override
    public List<File> getFilesByName(String name) {
        return null;
    }

    @Override
    public File getFileById(long id) {
        return null;
    }

    @Override
    public void addUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setName(user.getName());
        entity.setPassword(user.getPassword());
        entity.setHome(user.getHome());

        try {
           if(1 == userDao.create(entity)){
               user.setId(entity.getId());
               Log.i(LogTag.DB, "User entity created ::"+user.getName());
           }else{
               user.setId(-1);
               Log.w(LogTag.DB, "User entity not created ::"+user.getName());
           }
        } catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [User creation :: "+user.getName()+" ]");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User user) {
        deleteUserById(user.getId());
    }

    @Override
    public void editUser(User user) {

    }

    @Override
    public User getUserByName(String name) {
        return null;
    }

    @Override
    public User getUserById(long id) {
        User user = getByIdFromDao(userDao, id);
        return user;
    }

    @Override
    public void deleteUserById(long id) {
        try {
            userDao.deleteById(id);
           Log.i(LogTag.DB, "User entity deleted ::" + id);
        } catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [User deletion :: "+id+" ]");
            e.printStackTrace();
        }
    }

    @Override
    public void addTag(Tag tag) {

    }

    @Override
    public void deleteTag(Tag tag) {

    }

    @Override
    public void deleteTagById(long id) {

    }

    @Override
    public void editTag(Tag tag) {

    }

    @Override
    public Tag getTagByName(String name) {
        return null;
    }

    @Override
    public Tag getTagById(long id) {
        return null;
    }

    @Override
    public void assignTag(File file, Tag tag) {

    }

    @Override
    public void unassignTag(File file, Tag tag) {

    }

    @Override
    public List<Tag> getTags(File file) {
        return null;
    }

    @Override
    public List<Tag> getTags(long id) {
        return null;
    }

    @Override
    public List<File> getFilesByTag(Tag tag) {
        return null;
    }

    @Override
    public List<File> getFilesByTag(long id) {
        return null;
    }

    private static <T> T getByIdFromDao(Dao <T, Long> dao, long id) {
        T item = null;
        try {
            item = dao.queryForId(id);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get item]");
            e.printStackTrace();
        }
        return item;
    }
}
