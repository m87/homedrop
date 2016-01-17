package org.homedrop.thirdParty.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.*;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.log4j.BasicConfigurator;
import org.homedrop.core.model.*;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Identifiable;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.sqliteModels.*;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;

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
            TableUtils.dropTable(connectionSource, UserEntity.class,true);
            TableUtils.dropTable(connectionSource, FileEntity.class,true);
            TableUtils.dropTable(connectionSource, TagEntity.class,true);
            TableUtils.dropTable(connectionSource, FileEntity.class,true);
            TableUtils.dropTable(connectionSource, RuleEntity.class,true);
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
        List allRules = getAllFromDao(ruleDao);
        return allRules;
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
        FileEntity fileAsEntity = (FileEntity) file;
        createWithDao(fileDao, fileAsEntity, "File", file.getName());
    }

    @Override
    public void deleteFile(File file) {
        deleteFileById(file.getId());
    }

    @Override
    public void deleteFileById(long id) {
        try {
            TransactionManager.callInTransaction(connectionSource, (Callable<Void>) () -> {
                deleteByIdFromDao(fileDao, id, "File");
                Map<String, Long> foreignIdsToFieldsMap = new HashMap<>();
                foreignIdsToFieldsMap.put("file_id", id);
                deleteFromWeakEntity(fileTagDao, foreignIdsToFieldsMap, "FileTag");
                return null;
            });
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql transaction error! [Could not delete file by id: " + id + "]");
        }
    }

    @Override
    public void updateFile(File file) throws ItemNotFoundException {
        FileEntity fileAsEntity = (FileEntity) file;
        updateWithDao(fileDao, fileAsEntity, "File", file.getName());
    }

    @Override
    public List<File> getFilesByName(String name) {
        List<File> filesWithName = getFilesByField(name, "name");
        return filesWithName;
    }

    @Override
    public List<File> getAllFilesByPathPrefix(String prefix, User owner) {
        List<File> filesWithPathPrefix = getFilesByField(owner.getId(), "owner_id");
        filesWithPathPrefix.removeIf(file -> !file.getPath().startsWith(prefix));
        return filesWithPathPrefix;
    }

    @Override
    public List<File> getFilesByParentPath(String parentPath) {
        List<File> filesWithParentPath = getFilesByField(DBHelper.formatPath(parentPath), "parentPath");
        return filesWithParentPath;
    }

    private List<File> getFilesByField(Object fieldValue, String fieldName) {
        List<File> filesWithFieldValue = new ArrayList<>();
        try {
            PreparedQuery<FileEntity> preparedQuery = fileDao.queryBuilder().where().eq(fieldName, fieldValue).prepare();
            List<FileEntity> temporary = fileDao.query(preparedQuery);
            filesWithFieldValue.addAll(temporary);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get user by " + fieldName + "]");
            filesWithFieldValue = new ArrayList<>();
        }
        return filesWithFieldValue;
    }

    @Override
    public File getFileByPath(String path, User owner) throws ItemNotFoundException {
        List<File> filesWithPath = getFilesByField(path, "path");
        for (File fileWithPath : filesWithPath) {
            if (fileWithPath.getOwnerId() == owner.getId()) {
                return fileWithPath;
            }
        }
        throw new ItemNotFoundException("");
    }

    @Override
    public File getFileById(long id) throws ItemNotFoundException {
        File file = getByIdFromDao(fileDao, id, "File");
        return file;
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
    public void updateUser(User user) throws ItemNotFoundException {
        UserEntity userAsEntity = (UserEntity) user;
        updateWithDao(userDao, userAsEntity, "User", user.getName());
    }

    @Override
    public User getUserByName(String name) throws ItemNotFoundException {
        User user = null;
        try {
            PreparedQuery<UserEntity> preparedQuery = userDao.queryBuilder().where().eq("name", name).prepare();
            user = userDao.queryForFirst(preparedQuery);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get user by name]");
        }
        if (null == user) {
            throw new ItemNotFoundException("User");
        }
        return user;
    }

    @Override
    public User getUserById(long id) throws ItemNotFoundException {
        User user = getByIdFromDao(userDao, id, "User");
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
    public void deleteTag(Tag tag) throws ItemNotFoundException {
       deleteTagById(tag.getId());
    }

    @Override
    public void deleteTagById(long id) throws ItemNotFoundException {
        try {
            TransactionManager.callInTransaction(connectionSource, (Callable<Void>) () -> {
                deleteByIdFromDao(tagDao, id, "Tag");
                Map<String, Long> foreignIdsToFieldsMap = new HashMap<>();
                foreignIdsToFieldsMap.put("tag_id", id);
                deleteFromWeakEntity(fileTagDao, foreignIdsToFieldsMap, "FileTag");
                return null;
            });
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql transaction error! [Could not delete tag by id: " + id + "]");
            throw new ItemNotFoundException("Tag");
        }
    }

    @Override
    public void updateTag(Tag tag) throws ItemNotFoundException {
        TagEntity tagAsEntity = (TagEntity) tag;
        updateWithDao(tagDao, tagAsEntity, "Tag", tag.getName());
    }

    @Override
    public Tag getTagByName(String name) throws ItemNotFoundException {
        Tag tag = null;
        try {
            PreparedQuery<TagEntity> preparedQuery = tagDao.queryBuilder().where().eq("name", name).prepare();
            tag = tagDao.queryForFirst(preparedQuery);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get tag by name]");
        }
        if (null == tag) {
            throw new ItemNotFoundException("Tag");
        }
        return tag;
    }

    @Override
    public Tag getTagById(long id) throws ItemNotFoundException {
        Tag tag = getByIdFromDao(tagDao, id, "Tag");
        return tag;
    }

    @Override
    public void addRule(Rule rule) {
        RuleEntity ruleAsEntity = (RuleEntity) rule;
        createWithDao(ruleDao, ruleAsEntity, "Rule", "" + rule.getId());
    }

    @Override
    public void updateRule(Rule rule) throws ItemNotFoundException {
        RuleEntity ruleAsEntity = (RuleEntity) rule;
        updateWithDao(ruleDao, ruleAsEntity, "Rule", "" + rule.getId());
    }

    @Override
    public Rule getRuleById(long id) throws ItemNotFoundException {
        Rule rule = getByIdFromDao(ruleDao, id, "Rule");
        return rule;
    }

    @Override
    public void deleteRule(Rule rule) throws ItemNotFoundException {
        deleteRuleById(rule.getId());
    }

    @Override
    public void deleteRuleById(long id) throws ItemNotFoundException {
        deleteByIdFromDao(ruleDao, id, "Rule");
    }

    @Override
    public List<Rule> getValidRulesByFile(File file) {
        List<Rule>validRules = new ArrayList<Rule>();
        try {
            Where<RuleEntity, Long> whereClause = ruleDao.queryBuilder().where();
            Date today = new Date();
            whereClause.and(
                    whereClause.or(whereClause.isNull("file_id"), whereClause.eq("file_id", file.getId())),
                    whereClause.or(whereClause.isNull("holdsSince"), whereClause.le("holdsSince", today)),
                    whereClause.or(whereClause.isNull("holdsUntil"), whereClause.ge("holdsUntil", today)));
            PreparedQuery<RuleEntity> preparedQuery = whereClause.prepare();
            List<RuleEntity> temporary = ruleDao.query(preparedQuery);
            validRules.addAll(temporary);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get valid rules by file!]");
            e.printStackTrace();
        }
        return validRules;
    }

    @Override
    public void assignTag(File file, Tag tag) {
        FileTagEntity fileTag = new FileTagEntity();
        fileTag.setFile((FileEntity) file);
        fileTag.setTag((TagEntity) tag);
        String expressiveInfo = "FileId: " + file.getId() + ", TagId: " + tag.getId();
        createWithDao(fileTagDao, fileTag, "FileTag", expressiveInfo);
    }

    @Override
    public void unassignTag(File file, Tag tag) {
        Map<String, Long> foreignIdsToFieldsMap = new HashMap<>();
        foreignIdsToFieldsMap.put("file_id", file.getId());
        foreignIdsToFieldsMap.put("tag_id", tag.getId());
        deleteFromWeakEntity(fileTagDao, foreignIdsToFieldsMap, "FileTag");
    }

    private static <T> void deleteFromWeakEntity(Dao<T, Long> weakEntityDao,
                                                 Map<String, Long> foreignIdsToFieldsMap, String entityName) {
        try {
            DeleteBuilder<T, Long> deleteBuilder = weakEntityDao.deleteBuilder();
            Iterator<Map.Entry<String, Long>> iterator = foreignIdsToFieldsMap.entrySet().iterator();
            Map.Entry<String, Long> entry = iterator.next();
            Where<T, Long> where = deleteBuilder.where().eq(entry.getKey(), entry.getValue());
            while (iterator.hasNext()) {
                entry = iterator.next();
                where = where.and().eq(entry.getKey(), entry.getValue());
            }
            deleteBuilder.delete();
        }
        catch (SQLException e) {
            e.printStackTrace();
            Log.d(LogTag.DB, "Sql error! [Delete " + entityName + "]");
        }
    }

    @Override
    public List<Tag> getFileTags(File file) {
        List<Tag> fileTags = getFileTagsById(file.getId());
        return fileTags;
    }

    @Override
    public List<Tag> getFileTagsById(long id) {
        List<Tag> fileTags = new ArrayList<>();
        try {
            QueryBuilder queryForIdsOfTags = fileTagDao.queryBuilder();
            queryForIdsOfTags.where().eq("file_id", id);
            queryForIdsOfTags.selectColumns("tag_id");
            PreparedQuery<TagEntity> preparedQuery = tagDao.queryBuilder().where().in("id", queryForIdsOfTags).prepare();
            List<TagEntity> temporary = tagDao.query(preparedQuery);
            fileTags.addAll(temporary);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Files retrieval by fileId :: " + id +  " ]");
        }
        return fileTags;
    }

    @Override
    public List<File> getFilesByTag(Tag tag) {
        List<File> filesAssignedToTag = getFilesByTagId(tag.getId());
        return filesAssignedToTag;
    }

    @Override
    public List<File> getFilesByTagId(long id) {
        List<File> filesAssignedToTag = new ArrayList<>();
        try {
            QueryBuilder queryForIdsOfFiles = fileTagDao.queryBuilder();
            queryForIdsOfFiles.where().eq("tag_id", id);
            queryForIdsOfFiles.selectColumns("file_id");
            PreparedQuery<FileEntity> preparedQuery = fileDao.queryBuilder().where().in("id", queryForIdsOfFiles).prepare();
            List<FileEntity> temporary = fileDao.query(preparedQuery);
            filesAssignedToTag.addAll(temporary);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Files retrieval by tagId :: " + id +  " ]");
        }
        return filesAssignedToTag;
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

    private static <T> T getByIdFromDao(Dao <T, Long> dao, long id, String entityName) throws ItemNotFoundException {
        T item = null;
        try {
            item = dao.queryForId(id);
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get item]");
            e.printStackTrace();
        }
        if (null == item) {
            throw new ItemNotFoundException(entityName);
        }
        return item;
    }

    private static <T extends Identifiable> void updateWithDao(Dao <T, Long> dao, T entity, String entityName,
                                                               String expressiveValue) throws ItemNotFoundException {
        try {
            if(1 == dao.update(entity)){
                Log.i(LogTag.DB, entityName + " entity updated ::"+expressiveValue);
            }else{
                Log.w(LogTag.DB, entityName + "entity not updated ::" + expressiveValue);
                throw new ItemNotFoundException(entityName);
            }
        } catch (SQLException e) {
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
