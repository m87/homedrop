package org.homedrop.thirdParty.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Identifiable;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.thirdParty.db.sqliteModels.*;

import java.sql.SQLException;
import java.util.List;

public class SqliteHDDB implements HDDB {
    public static final long IdFailed = -1;
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
        List allUsers = getAllFromDao(userDao);
        return allUsers;
    }

    @Override
    public List<Rule> getAllRules() {
        return null;
    }

    private static <T> List<T> getAllFromDao(Dao <T, Long> dao) {
        List<T> allItems = null;
        try {
            allItems = dao.queryForAll();
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get item]");
            e.printStackTrace();
        }
        return allItems;
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
    public void updateFile(File file) {

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
        UserEntity userAsEntity = (UserEntity) user;
        createWithDao(userDao, userAsEntity, "User", user.getName());
    }

    @Override
    public void deleteUser(User user) {
        deleteUserById(user.getId());
    }

    @Override
    public void updateUser(User user) {
        UserEntity userAsEntity = (UserEntity) user;
        updateWithDao(userDao, userAsEntity, "User", user.getName());
    }

    @Override
    public User getUserByName(String name) {
        User user = null;
        try {
            PreparedQuery<UserEntity> preparedQuery = userDao.queryBuilder().where().eq("name", name).prepare();
            user = userDao.queryForFirst(preparedQuery);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get user by name]");
        }
        if (null == user) {
            user = new UserEntity();
            user.setId(IdFailed);
        }
        return user;
    }

    @Override
    public User getUserById(long id) {
        User user = getByIdFromDao(userDao, id);
        return user;
    }

    @Override
    public void deleteUserById(long id) {
        deleteByIdFromDao(userDao, id, "User");
    }

    @Override
    public void addTag(Tag tag) {
        TagEntity tagAsEntity = (TagEntity) tag;
        createWithDao(tagDao, tagAsEntity, "Tag", tag.getName());
    }

    @Override
    public void deleteTag(Tag tag) {
       deleteTagById(tag.getId());
    }

    @Override
    public void deleteTagById(long id) {
        deleteByIdFromDao(tagDao, id, "Tag");
    }

    @Override
    public void updateTag(Tag tag) {
        TagEntity tagAsEntity = (TagEntity) tag;
        updateWithDao(tagDao, tagAsEntity, "Tag", tag.getName());
    }

    @Override
    public Tag getTagByName(String name) {
        Tag tag = null;
        try {
            PreparedQuery<TagEntity> preparedQuery = tagDao.queryBuilder().where().eq("name", name).prepare();
            tag = tagDao.queryForFirst(preparedQuery);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get tag by name]");
        }
        if (null == tag) {
            tag = new TagEntity();
            tag.setId(IdFailed);
        }
        return tag;
    }

    @Override
    public Tag getTagById(long id) {
        Tag tag = getByIdFromDao(tagDao, id);
        return tag;
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

    private static <T extends Identifiable> void createWithDao(Dao <T, Long> dao, T entity, String entityName, String expressiveValue) {
        try {
            if(1 == dao.create(entity)){
                Log.i(LogTag.DB, entityName + " entity created ::"+expressiveValue);
            }else{
                entity.setId(IdFailed);
                Log.w(LogTag.DB, entityName + "entity not created ::" + expressiveValue);
            }
        } catch (SQLException e) {
            entity.setId(IdFailed);
            Log.d(LogTag.DB, "Sql error! [" + entityName + " creation :: "+ expressiveValue +" ]");
        }
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

    private static <T extends Identifiable> void updateWithDao(Dao <T, Long> dao, T entity, String entityName, String expressiveValue) {
        try {
            if(1 == dao.update(entity)){
                Log.i(LogTag.DB, entityName + " entity updated ::"+expressiveValue);
            }else{
                entity.setId(IdFailed);
                Log.w(LogTag.DB, entityName + "entity not updated ::" + expressiveValue);
            }
        } catch (SQLException e) {
            entity.setId(IdFailed);
            Log.d(LogTag.DB, "Sql error! [" + entityName + " update :: "+ expressiveValue +" ]");
        }
    }

    private void deleteByIdFromDao(Dao dao, long id, String entityName) {
        try {
            dao.deleteById(id);
            Log.i(LogTag.DB, entityName + " entity deleted ::" + id);
        } catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [" + entityName + " deletion :: "+id+" ]");
            e.printStackTrace();
        }
    }
}
