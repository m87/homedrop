package org.homedrop.thirdParty.db;

import com.esotericsoftware.yamlbeans.parser.CollectionStartEvent;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.*;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.*;
import org.homedrop.core.utils.DBHelper;
import org.homedrop.core.utils.Identifiable;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.exceptions.ItemWithValueAlreadyExistsException;
import org.homedrop.thirdParty.db.sqliteModels.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

import static java.util.stream.Collectors.toList;

public class SqliteHDDB implements HDDB {
    public static final long IdFailed = -1;
    private ConnectionSource connectionSource;
    private Dao<UserEntity,Long> userDao;
    private Dao<TagEntity,Long> tagDao;
    private Dao<FileEntity,Long> fileDao;
    private Dao<FileTagEntity,Long> fileTagDao;
    private Dao<RuleEntity,Long> ruleDao;

    public SqliteHDDB(JdbcPooledConnectionSource connectionSource){
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

    @Override
    public List<File> getAllFiles() {
        List allFiles = getAllFromDao(fileDao);
        return allFiles;
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
        createWithDao(fileDao, fileAsEntity, "File", file.getPath());
    }

    @Override
    public void deleteFileByPath(String username, String filePath) throws ItemNotFoundException {
        try {
            TransactionManager.callInTransaction(connectionSource, (Callable<Void>) () -> {
                deleteFileByPathWithinTransaction(username, filePath);
                return null;
            });
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql transaction error! [Could not delete files by path: " + filePath + "]");
            if (e.getCause() instanceof ItemNotFoundException) {
                throw (ItemNotFoundException) e.getCause();
            }
        }
    }

    void deleteFileByPathWithinTransaction(String username, String filePath) throws ItemNotFoundException, SQLException {
        List<File> files = getSubtreeWithRootDirectory(username, filePath);
        if (0 == files.size()) {
            throw new ItemNotFoundException("Attempt to remove not existing files!");
        }

        List<Long> fileIds = files.stream().map(File::getId).collect(toList());

        Map<String, List<Long>> foreignIdsToFieldsMap = new HashMap<>();
        foreignIdsToFieldsMap.put("file_id", fileIds);
        deleteFromWeakEntity(fileTagDao, foreignIdsToFieldsMap, "FileTag");
        // clear file to tag assignments

        List<String> filePaths = files.stream().map(File::getPath).collect(toList());
        DeleteBuilder<RuleEntity, Long> deleteBuilder = ruleDao.deleteBuilder();
        deleteBuilder.where().in("filePath", filePaths);
        deleteBuilder.delete();
        // remove rules assigned to files to be removed

        deleteByIdFromDao(fileDao, fileIds, "file");
        // and finally remove requested files
    }

    @Override
    public void updateFile(File file) throws ItemNotFoundException {
        FileEntity fileAsEntity = (FileEntity) file;
        updateWithDao(fileDao, fileAsEntity, "File", file.getPath());
    }

    @Override
    public void renameFileReplaceIfNecessary(String username, String pathSrc, String pathDest)
            throws ItemNotFoundException {
        try {
            renameTemplate(username, pathSrc, pathDest, true);
        }
        catch (ItemWithValueAlreadyExistsException e) {
            // will never happen
        }
    }

    @Override
    public void renameFile(String username, String pathSrc, String pathDest)
            throws ItemNotFoundException, ItemWithValueAlreadyExistsException {
        renameTemplate(username, pathSrc, pathDest, false);
    }

    void renameTemplate(String username, String pathSrc, String pathDest, boolean doReplace)
            throws ItemNotFoundException, ItemWithValueAlreadyExistsException  {
        final String formattedPathSrc = DBHelper.formatPath(pathSrc);
        final String formattedPathDest = DBHelper.formatPath(pathDest);
        try {
            TransactionManager.callInTransaction(connectionSource, (Callable<Void>) () -> {
                List<File> files = getSubtreeWithRootDirectory(username, formattedPathSrc);
                if (0 == files.size()) {
                    throw new ItemNotFoundException("Attempt to rename not existing files!");
                }

                if (!doReplace && 0 < getSubtreeWithRootDirectory(username, formattedPathDest).size()) {
                    throw new ItemWithValueAlreadyExistsException("Attempt to rename for already existing file!");
                }

                if (doReplace) {
                    List<File> filesToReplace = getSubtreeWithRootDirectory(username, formattedPathDest);
                    if (0 < filesToReplace.size()) {
                        deleteFileByPathWithinTransaction(username, pathDest);
                    }

                }
                UpdateBuilder<RuleEntity, Long> updateBuilder = ruleDao.updateBuilder();
                updateItemsHavingPath(updateBuilder, files, pathSrc, pathDest, "filePath");
                UpdateBuilder<FileEntity, Long> fileUpdateBuilder = fileDao.updateBuilder();
                updateItemsHavingPath(fileUpdateBuilder, files, pathSrc, pathDest, "path");
                return null;
            });
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql transaction error! [Could not rename files by path: " + pathSrc + "]");
            e.printStackTrace();
            if (e.getCause() instanceof ItemNotFoundException) {
                throw (ItemNotFoundException) e.getCause();
            }
            else if (e.getCause() instanceof ItemWithValueAlreadyExistsException) {
                throw (ItemWithValueAlreadyExistsException) e.getCause();
            }
        }
    }

    public static <T> void updateItemsHavingPath(UpdateBuilder<T, Long> updateBuilder, List<File> files,
                                                 String pathSrc, String pathDest, String pathField) throws SQLException {
        for (File file : files) {
            String newFilePath = pathDest + file.getPath().substring(pathSrc.length());
            updateBuilder.where().eq(pathField, file.getPath());
            updateBuilder.updateColumnValue(pathField, newFilePath);
            if (pathField.equals("path")) {
                Path parentPath = Paths.get(newFilePath).getParent();
                    updateBuilder.updateColumnValue("parentPath", parentPath.toString());
            }
            updateBuilder.update();
            updateBuilder.reset();
        }
    }

    @Override
    public List<File> getSubtreeWithRootDirectory(String username, String prefix) {
        return getSubtree(username, prefix, true);
    }

    @Override
    public List<File> getSubtreeExcludingRootDirectory(String username, String prefix) {
        return getSubtree(username, prefix, false);
    }

    private List<File> getSubtree(String username, String prefix, boolean includeRootDir) {
        List<File> subtree = new ArrayList<>();
        String errorMsg = "Sql error! Couldn't get subtree!";
        try {
            QueryBuilder<FileEntity, Long> queryBuilder = getSubtreeQueryBuilder(username, prefix, includeRootDir);
            PreparedQuery<FileEntity> preparedQuery =  queryBuilder.prepare();
            List<FileEntity> temporary = fileDao.query(preparedQuery);
            subtree.addAll(temporary);
        } catch (SQLException e) {
            Log.d(LogTag.DB, errorMsg);
        }
        return subtree;
    }

    private QueryBuilder<FileEntity, Long> getSubtreeQueryBuilder(String username, String prefix,
                                                                  boolean includeRootDir) throws SQLException {
        QueryBuilder<UserEntity, Long> userQueryBuilder = userDao.queryBuilder();
        userQueryBuilder.where().eq("name", username);
        QueryBuilder<FileEntity, Long> fileQueryBuilder = fileDao.queryBuilder();
        addToQueryRelationToSubtree(fileQueryBuilder.where(), prefix, includeRootDir);
        return fileQueryBuilder.join(userQueryBuilder);
    }

    private void addToQueryRelationToSubtree(Where<FileEntity, Long> whereClause,
                                                       String path, boolean includeRootDir) {
        String preparedPath = path.replace("!", "!!").replace("?", "!?");
        preparedPath = DBHelper.formatPath(preparedPath);
        System.out.println(preparedPath);
        String rawQuery = "(path LIKE ? ESCAPE '!'";
        SelectArg[] queryParams;
        if (includeRootDir) {
            rawQuery += " OR path == ?)";
            queryParams = new SelectArg[] { new SelectArg("path", preparedPath + "/%"), new SelectArg("path", preparedPath) };
        }
        else {
            rawQuery += ")";
            queryParams = new SelectArg[] { new SelectArg("path", preparedPath + "/%") };
        }
        whereClause.raw(rawQuery, queryParams);
    }

    @Override
    public List<File> getFilesByParentPath(String username, String parentPath) {
        List<File> filesWithParentPath = new ArrayList<>();
        try {
            QueryBuilder<UserEntity, Long> userQueryBuilder = userDao.queryBuilder();
            userQueryBuilder.where().eq("name", username);
            QueryBuilder<FileEntity, Long> fileQueryBuilder = fileDao.queryBuilder();
            fileQueryBuilder.where().eq("parentPath", parentPath);
            PreparedQuery<FileEntity> preparedQuery = fileQueryBuilder.join(userQueryBuilder).prepare();
            filesWithParentPath.addAll(fileDao.query(preparedQuery));
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Couldn't get files by parentPath!");
        }
        return filesWithParentPath;
    }

    @Override
    public boolean fileExists(String username, String path){
        try {
            User owner = getUserByName(username);
            getFileByPath(path, owner);
        }
        catch (ItemNotFoundException e) {
            return false;
        }
        return true;
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
        Tag duplicateTag = null;
        try {
            PreparedQuery<TagEntity> preparedQuery = tagDao.queryBuilder().where().eq("name", tag.getName()).prepare();
            duplicateTag = tagDao.queryForFirst(preparedQuery);
            if (null == duplicateTag) {
                createWithDao(tagDao, tagAsEntity, "Tag", tag.getName());
            }
            else {
                tag.setId(duplicateTag.getId());
            }
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get tag item :: " + tag.getName() + "]");
        }
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
                Map<String, List<Long>> foreignIdsToFieldsMap = new HashMap<>();
                foreignIdsToFieldsMap.put("tag_id", Collections.singletonList(id));
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
        createWithDao(ruleDao, ruleAsEntity, "Rule", "" + rule.getFilePath());
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
        List<Rule> validRules = new ArrayList<>();
        try {
            User owner = getUserById(file.getOwnerId());
            validRules = getRules(owner.getName(), AnyType, file.getPath(), true);
        }
        catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "Couldn't find owner of requested file to get rules for!");
        }
        return validRules;
    }

    private final int AnyType = -1;

    @Override
    public List<Rule> getValidGlobalRules(String username) {
        List<Rule> validGlobalRules = getRules(username, AnyType, null, false);
        return validGlobalRules;
    }

    @Override
    public List<Rule> getValidGlobalRulesByType(String username, int type) {
        List<Rule> validGlobalRulesByType = getRules(username, type, null, false);
        return validGlobalRulesByType;
    }

    @Override
    public List<Rule> getValidSpecificRulesByType(String username, int type, String filePath) {
        List<Rule> validSpecificRulesByType = getRules(username, type, filePath, false);
        return validSpecificRulesByType;
    }

    @Override
    public boolean ruleExists(String username, String filePath) {
        List<Rule> rules = getRules(username, AnyType, filePath, false);
        return rules.size() > 0;
    }

    private List<Rule> getRules(String username, Integer type, String filePath, boolean getGlobalAndSpecific) {
        List<Rule>rules = new ArrayList<>();
        try {
            User user = getUserByName(username);
            Where<RuleEntity, Long> whereClause = ruleDao.queryBuilder().where();
            int clauseCount = 3;
            whereClause.eq("owner_id", user.getId());

            Date today = new Date();
            whereClause.or(whereClause.isNull("holdsSince"), whereClause.le("holdsSince", today));
            whereClause.or(whereClause.isNull("holdsUntil"), whereClause.ge("holdsUntil", today));

            if (type >= 0) {
                whereClause.eq("type", type);
                ++clauseCount;
            }
            if (null != filePath && getGlobalAndSpecific) {
                whereClause.or(
                        whereClause.eq("filePath", filePath),
                        whereClause.isNull("filePath")
                );
            }
            else if (null != filePath) {
                whereClause.eq("filePath", filePath);
            }
            else {
                whereClause.isNull("filePath");
            }
            ++clauseCount;

            whereClause.and(clauseCount);
            PreparedQuery<RuleEntity> preparedQuery = whereClause.prepare();
            List<RuleEntity> temporary = ruleDao.query(preparedQuery);
            rules.addAll(temporary);
        } catch (ItemNotFoundException e) {
            Log.d(LogTag.DB, "Error! [Given username does not fit any user!]");
        }
        catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [Could not get rules!]");
        }
        return rules;
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
        Map<String, List<Long>> foreignIdsToFieldsMap = new HashMap<>();
        foreignIdsToFieldsMap.put("file_id", Collections.singletonList(file.getId()));
        foreignIdsToFieldsMap.put("tag_id", Collections.singletonList(tag.getId()));
        deleteFromWeakEntity(fileTagDao, foreignIdsToFieldsMap, "FileTag");
    }

    private static <T> void deleteFromWeakEntity(Dao<T, Long> weakEntityDao,
                                                 Map<String, List<Long>> foreignIdsToFieldsMap, String entityName) {
        try {
            DeleteBuilder<T, Long> deleteBuilder = weakEntityDao.deleteBuilder();
            Iterator<Map.Entry<String, List<Long>>> iterator = foreignIdsToFieldsMap.entrySet().iterator();
            Map.Entry<String, List<Long>> entry = iterator.next();
            Where<T, Long> where = deleteBuilder.where();
            if (1 == entry.getValue().size()) {
                where.eq(entry.getKey(), entry.getValue().get(0));
            }
            else {
                where.in(entry.getKey(), entry.getValue());
            }
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (1 == entry.getValue().size()) {
                    where.and().eq(entry.getKey(), entry.getValue().get(0));
                }
                else {
                    where.and().in(entry.getKey(), entry.getValue());
                }
            }
            Log.d(LogTag.DB, deleteBuilder.prepareStatementString());
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
        deleteByIdFromDao(dao, Collections.singletonList(id), entityName);
    }

    private void deleteByIdFromDao(Dao dao, List<Long> ids, String entityName) {
        try {
            int size = dao.deleteIds(ids);
            Log.i(LogTag.DB, "" + ids.size());
            Log.i(LogTag.DB, "" + size);
            Log.i(LogTag.DB, entityName + " a group of entities successfully deleted.");
        } catch (SQLException e) {
            Log.d(LogTag.DB, "Sql error! [" + entityName + " deletion :: "+e.getMessage()+" ]");
        }
    }


}
