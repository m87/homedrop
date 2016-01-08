package org.homedrop.thirdParty.db;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.ModelHelpers;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.DependencyProvider;
import org.homedrop.testUtils.TestHelpers;
import org.homedrop.thirdParty.db.sqliteModels.UserEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class SqliteHDDBTest {

    static DependencyProvider dependencyProvider;
    static SqliteHDDB sqliteHDDB;
    static JdbcConnectionSource connectionSource;

    @BeforeClass
    static public void setUpTest() throws Exception {
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop.cfg");
        dependencyProvider = DependencyProvider.getInstance();
        dependencyProvider.setConfig(config);
        connectionSource = dependencyProvider.getDbConnectionSource();
        sqliteHDDB = new SqliteHDDB(connectionSource);
    }

    @After
    public void tearDown() throws Exception {
        TableUtils.clearTable(connectionSource, UserEntity.class);
    }

    @Test
    public void testOnCreate() throws Exception {
        Class<?>[] entityClasses = { UserEntity.class };
        for (Class<?> entityClass : entityClasses) {
            TableUtils.dropTable(connectionSource, entityClass, true);
        }
        sqliteHDDB.onCreate();
        try {
            TransactionManager.callInTransaction(connectionSource, (Callable<Void>) () -> {
                for (Class<?> entityClass : entityClasses) {
                    TableUtils.dropTable(connectionSource, entityClass, false);
                }
                throw new TestHelpers.HelperException();
            });
        }
        catch (SQLException e) {
            Throwable causeException = e.getCause();
            assertThat(causeException, instanceOf(TestHelpers.HelperException.class));
        }
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User[] users = {
                new UserEntity(),
                new UserEntity()
        };
        ModelHelpers.setUserFields(users[0], "testuser", "pass", "testuser_home");
        ModelHelpers.setUserFields(users[1], "testuser2", "pass2", "home_testuser2");
        for (User user : users) {
            sqliteHDDB.addUser(user);
        }

        List<User> allUsers = sqliteHDDB.getAllUsers();

        assertEquals(allUsers.size(), users.length);
        for (User expectedUser : users) {
            TestHelpers.assertListContainsItemEqual(allUsers, expectedUser);
        }
    }

    @Test
    public void testGetAllRules() throws Exception {

    }

    @Test
    public void testAddFile() throws Exception {

    }

    @Test
    public void testDeleteFile() throws Exception {

    }

    @Test
    public void testDeleteFileById() throws Exception {

    }

    @Test
    public void testAddUser() throws Exception {
        User[] users = {
                new UserEntity(),
                new UserEntity()
        };
        ModelHelpers.setUserFields(users[0], "testuser", "pass", "testuser_home");
        ModelHelpers.setUserFields(users[1], "testuser2", "pass2", "home_testuser2");
        for (User user : users) {
            sqliteHDDB.addUser(user);
        }

        for (User expectedUser : users) {
            User actualUser = sqliteHDDB.getUserById(expectedUser.getId());
            assertTrue(ModelHelpers.areFieldsEqual(expectedUser, actualUser));
        }

    }
}