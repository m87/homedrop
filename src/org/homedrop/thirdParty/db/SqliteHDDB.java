package org.homedrop.thirdParty.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.manager.ConfigManager;
import org.homedrop.thirdParty.db.sqliteModels.*;

import java.sql.SQLException;
import java.util.List;

public class SqliteHDDB implements HDDB{
    private ConnectionSource connectionSource;
    private Dao<UserEntity,Long> userDao;
    private Dao<TagEntity,Long> tagDao;
    private Dao<FileEntity,Long> fileDao;
    private Dao<FileTagEntity,Long> fileTagDao;
    private Dao<RuleEntity,Long> ruleDao;

    public SqliteHDDB(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String dbConnectionString = "jdbc:sqlite:"+ ConfigManager.getInstance().getDbPath();
        try {
            connectionSource = new JdbcConnectionSource(dbConnectionString);

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
    public List<File> getAllFiles() {
        return null;
    }

    @Override
    public void addFile(File file) {

    }

    @Override
    public void deleteFile(File file) {

    }

    @Override
    public void editFile(File file) {

    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void editUser(User user) {

    }

    @Override
    public void addTag(Tag tag) {

    }

    @Override
    public void deleteTag(Tag tag) {

    }

    @Override
    public void editTag(Tag tag) {

    }

    @Override
    public void assignTag(File file, Tag tag) {

    }

    @Override
    public void unassignTag(File file, Tag tag) {

    }
}
