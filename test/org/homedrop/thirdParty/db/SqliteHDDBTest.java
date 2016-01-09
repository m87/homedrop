package org.homedrop.thirdParty.db;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Identifiable;
import org.homedrop.core.utils.ModelHelpers;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.DependencyProvider;
import org.homedrop.testUtils.TestHelpers;
import org.homedrop.thirdParty.db.sqliteModels.TagEntity;
import org.homedrop.thirdParty.db.sqliteModels.UserEntity;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class SqliteHDDBTest {

    static DependencyProvider dependencyProvider;
    static SqliteHDDB sqliteHDDB;
    static JdbcConnectionSource connectionSource;

    static Map<Class, Method> areItemsEqualMethodsMap;
    static Map<String, Class> fieldTypesMap;

    @BeforeClass
    static public void setUpTest() throws Exception {
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop.cfg");
        dependencyProvider = DependencyProvider.getInstance();
        dependencyProvider.setConfig(config);
        connectionSource = dependencyProvider.getDbConnectionSource();
        sqliteHDDB = new SqliteHDDB(connectionSource);
        areItemsEqualMethodsMap = new HashMap<Class, Method>();
        for (Class theClass : new Class[] { User.class, Tag.class }){
            areItemsEqualMethodsMap.put(theClass, ModelHelpers.class
                    .getDeclaredMethod("areItemsEqual", theClass, theClass));
        }
        fieldTypesMap = new HashMap<String, Class>();
        fieldTypesMap.put("Id", Long.TYPE);
        fieldTypesMap.put("Name", String.class);
    }

    @After
    public void tearDown() throws Exception {
        TableUtils.clearTable(connectionSource, UserEntity.class);
        TableUtils.clearTable(connectionSource, TagEntity.class);
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
        User[] users = prepareUsersForTest();

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
    public void testUpdateFile() throws Exception {

    }

    @Test
    public void testUpdateFileWhenFileDoesNotExist() throws Exception {

    }

    @Test
    public void testGetFilesByName() throws Exception {

    }

    public static void deleteItemTestTemplate(Identifiable[] items, Class itemType) throws Exception {
        HDDB sqliteHDDBMock = mock(SqliteHDDB.class);
        Method deleteMethod = SqliteHDDB.class.getDeclaredMethod("delete" + itemType.getSimpleName(), itemType);

        deleteMethod.invoke(sqliteHDDBMock, items[0]);

        HDDB verifyMock = verify(sqliteHDDBMock, times(1));
        deleteMethod.invoke(verifyMock, items[0]);
        verifyNoMoreInteractions(sqliteHDDBMock);
    }

    public static void deleteItemByIdTestTemplate(Identifiable[] items, Class itemType) throws Exception {
        Method deleteMethod = SqliteHDDB.class.getDeclaredMethod("delete" + itemType.getSimpleName() + "ById", Long.TYPE);

        deleteMethod.invoke(sqliteHDDB, items[0].getId());

        Method getByIdMethod = SqliteHDDB.class.getDeclaredMethod("get" + itemType.getSimpleName() + "ById", Long.TYPE);
        Identifiable item = (Identifiable) getByIdMethod.invoke(sqliteHDDB, items[0].getId());
        assertNull(item);
        Identifiable actualItem = (Identifiable) getByIdMethod.invoke(sqliteHDDB, items[1].getId());
        Method areFieldsEqualMethod = areItemsEqualMethodsMap.get(itemType);
        assertTrue((boolean)areFieldsEqualMethod.invoke(null, items[1], actualItem));
    }

    @Test
    public void testAddUser() throws Exception {
        User[] users = prepareUsersForTest();
        assertCollectionIsConsistentWithDb(users, User.class, "Id");
    }

    public void assertCollectionIsConsistentWithDb(Identifiable[] items, Class itemType, String getterFieldName) throws Exception {
        Method getByFieldMethod = SqliteHDDB.class
                .getDeclaredMethod("get" + itemType.getSimpleName() + "By" + getterFieldName, fieldTypesMap.get(getterFieldName));
        Method getFieldFromItemMethod = itemType.getMethod("get" + getterFieldName);
        Method areFieldsEqualMethod = areItemsEqualMethodsMap.get(itemType);
        for (Identifiable expectedItem : items) {
            Object expectedFieldValue = getFieldFromItemMethod.invoke(expectedItem);
            Identifiable actualItem = (Identifiable) getByFieldMethod.invoke(sqliteHDDB, expectedFieldValue);
            assertTrue((boolean)areFieldsEqualMethod.invoke(null, expectedItem, actualItem));
        }
    }

    @Test
    public void testAddUserWhenUserAlreadyExists() {
        User[] users = prepareUsersForTest();

        sqliteHDDB.addUser(users[0]);

        assertEquals(SqliteHDDB.IdFailed, users[0].getId());
    }

    @Test
    public void testDeleteUserById() throws Exception {
        User[] users = prepareUsersForTest();
        deleteItemByIdTestTemplate(users, User.class);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User[] users = prepareUsersForTest();
        deleteItemTestTemplate(users, User.class);
    }

    @Test
    public void testUpdateUser() throws Exception {
        User[] users = prepareUsersForTest();
        String expectedName = "newName";
        users[0].setName(expectedName);

        sqliteHDDB.updateUser(users[0]);

        assertCollectionIsConsistentWithDb(users, User.class, "Id");
    }

    @Test
    public void testUpdateUserWhenUserDoesNotExist() throws Exception {
        User[] users = prepareUsersForTest();
        User notExistingUser = new UserEntity();
        ModelHelpers.setUserFields(notExistingUser, "foo", "foo", "foo");
        notExistingUser.setId(999);

        sqliteHDDB.updateUser(notExistingUser);
        assertEquals(SqliteHDDB.IdFailed, notExistingUser.getId());
    }


    @Test
    public void testGetUserByName() throws Exception {
        User[] users = prepareUsersForTest();
        assertCollectionIsConsistentWithDb(users, User.class, "Name");
    }

    @Test
    public void testGetUserByNameWhenNameDoesNotOccur() throws Exception {
        User[] users = prepareUsersForTest();

        User user = sqliteHDDB.getUserByName("notOccurringName");
        assertEquals(SqliteHDDB.IdFailed, user.getId());
    }

    public User[] prepareUsersForTest() {
        User[] users = {
                new UserEntity(),
                new UserEntity()
        };
        ModelHelpers.setUserFields(users[0], "testuser", "pass", "testuser_home");
        ModelHelpers.setUserFields(users[1], "testuser2", "pass2", "home_testuser2");
        for (User user : users) {
            sqliteHDDB.addUser(user);
        }
        return users;
    }

    @Test
    public void testAddTag() throws Exception {
        Tag [] tags = prepareTagsForTest();
        assertCollectionIsConsistentWithDb(tags, Tag.class, "Id");
    }

    @Test
    public void testDeleteTag() throws Exception {
        Tag[] tags = prepareTagsForTest();
        deleteItemTestTemplate(tags, Tag.class);
    }

    @Test
    public void testDeleteTagById() throws Exception {
        Tag[] tags = prepareTagsForTest();
        deleteItemByIdTestTemplate(tags, Tag.class);
    }

    @Test
    public void testUpdateTag() throws Exception {
        Tag[] tags = prepareTagsForTest();
        String expectedName = "newName";
        tags[0].setName(expectedName);

        sqliteHDDB.updateTag(tags[0]);

        assertCollectionIsConsistentWithDb(tags, Tag.class, "Id");
    }

    @Test
    public void testUpdateTagWhenTagDoesNotExist() throws Exception {
        Tag[] tags = prepareTagsForTest();
        Tag notExistingTag = new TagEntity();
        notExistingTag.setId(999);
        notExistingTag.setName("notExisting");

        sqliteHDDB.updateTag(notExistingTag);

        assertEquals(SqliteHDDB.IdFailed, notExistingTag.getId());
    }

    @Test
    public void testGetTagByName() throws Exception {
        Tag[] tags = prepareTagsForTest();
        assertCollectionIsConsistentWithDb(tags, Tag.class, "Name");
    }

    @Test
    public void testGetTagByNameWhenNameDoesNotOccur() throws Exception {
        Tag[] tags = prepareTagsForTest();

        Tag tag = sqliteHDDB.getTagByName("notOccurringName");
        assertEquals(SqliteHDDB.IdFailed, tag.getId());
    }

    public Tag[] prepareTagsForTest() {
        Tag[] tags = {
                new TagEntity(),
                new TagEntity()
        };
        tags[0].setName("testtag");
        tags[1].setName("testtag2");
        for (Tag tag: tags) {
            sqliteHDDB.addTag(tag);
        }
        return tags;
    }
}