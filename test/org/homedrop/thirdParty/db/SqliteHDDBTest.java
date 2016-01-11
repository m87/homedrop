package org.homedrop.thirdParty.db;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.table.TableUtils;
import org.apache.log4j.BasicConfigurator;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Identifiable;
import org.homedrop.core.utils.ModelHelpers;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.DependencyProvider;
import org.homedrop.testUtils.TestHelpers;
import org.homedrop.thirdParty.db.sqliteModels.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.sql.Date;
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
        BasicConfigurator.configure();
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop_test.cfg");
        dependencyProvider = DependencyProvider.getInstance();
        dependencyProvider.setConfig(config);
        connectionSource = dependencyProvider.getDbConnectionSource();
        sqliteHDDB = new SqliteHDDB(connectionSource);
        areItemsEqualMethodsMap = new HashMap<Class, Method>();
        for (Class theClass : new Class[] { User.class, Tag.class, File.class }){
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
        TableUtils.clearTable(connectionSource, FileEntity.class);
        TableUtils.clearTable(connectionSource, RuleEntity.class);
        TableUtils.clearTable(connectionSource, FileTagEntity.class);
    }

    @Test
    public void testOnCreate() throws Exception {
        Class<?>[] entityClasses = { UserEntity.class, TagEntity.class, FileEntity.class, RuleEntity.class, FileTagEntity.class };
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
        File[] files = prepareFilesForTest();
        assertCollectionIsConsistentWithDb(files, File.class, "Id");
    }

    @Test
    public void testDeleteFile() throws Exception {
        File[] files = prepareFilesForTest();
        deleteItemTestTemplate(files, File.class);
    }

    @Test
    public void testDeleteFileUnassignTags() throws Exception {
        File[] files = prepareFilesForTest();
        Tag[] tags = prepareTagsForTest();
        Map<Tag, File[]> fileTagMap = prepareFileTagsForTest(tags, files);

        long deletedFileId = files[0].getId();
        sqliteHDDB.deleteFile(files[0]);
        List<Tag> actualTagsOfDeletedFile = sqliteHDDB.getFileTagsById(deletedFileId);
        assertEquals(0, actualTagsOfDeletedFile.size());
    }

    @Test
    public void testDeleteFileById() throws Exception {
        File[] files = prepareFilesForTest();
        deleteItemByIdTestTemplate(files, File.class);
    }

    @Test
    public void testUpdateFile() throws Exception {
        File[] files = prepareFilesForTest();
        String expectedName = "newName";
        files[1].setName(expectedName);
        long expectedVersion = 820;
        files[1].setVersion(expectedVersion);
        User owner = new UserEntity();
        ModelHelpers.setUserFields(owner, "testuser2", "pass2", "home_testuser2");
        owner.setId(files[1].getOwnerId()+1);
        files[1].setOwner(owner);

        sqliteHDDB.updateFile(files[1]);

        assertCollectionIsConsistentWithDb(files, File.class, "Id");
    }

    @Test
    public void testUpdateFileWhenFileDoesNotExist() throws Exception {
        File[] files = prepareFilesForTest();
        String expectedName = "notExisting";
        files[1].setName(expectedName);
        files[1].setId(999);

        sqliteHDDB.updateFile(files[1]);
        assertEquals(SqliteHDDB.IdFailed, files[1].getId());
    }

    @Test
    public void testGetFilesByName() throws Exception {
        File[] files = prepareFilesForTest();
        File[] expectedFiles = { files[0], files[2] };

        List<File> actualFiles = sqliteHDDB.getFilesByName("fileName");

        assertEquals(expectedFiles.length, actualFiles.size());
        for (File expectedFile : expectedFiles) {
            TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
        }
    }

    @Test
    public void testGetFilesByNameWhenFileDoesNotExist() throws Exception {
        File[] files = prepareFilesForTest();

        List<File> actualFiles = sqliteHDDB.getFilesByName("notExistingName");

        assertEquals(0, actualFiles.size());
    }

    @Test
    public void testGetFilesByPath() throws Exception {
        File[] files = prepareFilesForTest();
        File[] expectedFiles = { files[0], files[1] };

        List<File> actualFiles = sqliteHDDB.getFilesByPath("testpath/");

        assertEquals(expectedFiles.length, actualFiles.size());
        for (File expectedFile : expectedFiles) {
            TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
        }
    }

    @Test
    public void testGetFilesByPathWhenFileDoesNotExist() throws Exception {
        File[] files = prepareFilesForTest();

        List<File> actualFiles = sqliteHDDB.getFilesByPath("notExistingPath");

        assertEquals(0, actualFiles.size());
    }

    public File[] prepareFilesForTest() {
        User[] owners = prepareUsersForTest();
        FileEntity[] files = {
                new FileEntity(),
                new FileEntity(),
                new FileEntity()
        };
        ModelHelpers.setFileFields(files[0], "fileName", 5621, Date.valueOf("2016-01-05"),
                owners[0], "testpath/", File.FileType.File, 2);
        ModelHelpers.setFileFields(files[1], "fileName2", 113, Date.valueOf("2016-01-04"),
                owners[0], "testpath/", File.FileType.File, 1);
        ModelHelpers.setFileFields(files[2], "fileName", 585, Date.valueOf("2016-01-05"),
                owners[1], "testpath2/", File.FileType.File, 4);

        for (File file : files) {
            sqliteHDDB.addFile(file);
        }

        return files;
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
    public void testDeleteTagUnassignFromFile() throws Exception {
        File[] files = prepareFilesForTest();
        Tag[] tags = prepareTagsForTest();
        Map<Tag, File[]> fileTagMap = prepareFileTagsForTest(tags, files);

        long deletedTagId = tags[0].getId();
        sqliteHDDB.deleteTag(tags[0]);
        List<File> actualFiles = sqliteHDDB.getFilesByTag(tags[0]);
        assertEquals(0, actualFiles.size());
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
                new TagEntity(),
                new TagEntity(),
                new TagEntity()
        };
        tags[0].setName("testtag");
        tags[1].setName("testtag2");
        tags[2].setName("testtag3");
        tags[3].setName("notUsedTag");
        for (Tag tag: tags) {
            sqliteHDDB.addTag(tag);
        }
        return tags;
    }

    @Test
    public void testAssignTag() throws Exception {
        File[] files = prepareFilesForTest();
        Tag[] tags = prepareTagsForTest();
        Map<Tag, File[]> tagToFileMap = prepareFileTagsForTest(tags, files);

        for (Map.Entry<Tag, File[]> tagFile : tagToFileMap.entrySet()) {
            File[] expectedFiles = tagFile.getValue();
            List<File> actualFiles = sqliteHDDB.getFilesByTag(tagFile.getKey());
            assertEquals(expectedFiles.length, actualFiles.size());
            for (File expectedFile : expectedFiles) {
                TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
            }
        }
    }

    @Test
    public void testUnassignTag() throws Exception {
        File[] files = prepareFilesForTest();
        Tag[] tags = prepareTagsForTest();
        Map<Tag, File[]> tagToFileMap = prepareFileTagsForTest(tags, files);

        sqliteHDDB.unassignTag(files[1], tags[2]);
        List<File> actualFiles = sqliteHDDB.getFilesByTag(tags[2]);
        assertEquals(0, actualFiles.size());
        List<Tag> actualTags = sqliteHDDB.getFileTags(files[1]);
        assertEquals(1, actualTags.size());
        TestHelpers.assertListContainsItemEqual(actualTags, tags[1]);

        sqliteHDDB.unassignTag(files[0], tags[0]);
        actualFiles = sqliteHDDB.getFilesByTag(tags[0]);
        assertEquals(1, actualFiles.size());
        TestHelpers.assertListContainsItemEqual(actualFiles, files[2]);
        actualTags = sqliteHDDB.getFileTags(files[0]);
        assertEquals(1, actualTags.size());
        TestHelpers.assertListContainsItemEqual(actualTags, tags[1]);
    }

    @Test
    public void testGetFileTags() throws Exception {
        File[] files = prepareFilesForTest();
        Tag[] tags = prepareTagsForTest();
        Map<Tag, File[]> tagToFileMap = prepareFileTagsForTest(tags, files);

        List<Tag> actualTags = sqliteHDDB.getFileTags(files[0]);
        assertEquals(2, actualTags.size());
        TestHelpers.assertListContainsItemEqual(actualTags, tags[0]);
        TestHelpers.assertListContainsItemEqual(actualTags, tags[1]);
    }

    @Test
    public void testGetFilesByTag() throws Exception {
        File[] files = prepareFilesForTest();
        Tag[] tags = prepareTagsForTest();
        Map<Tag, File[]> tagToFileMap = prepareFileTagsForTest(tags, files);

        List<File> actualFiles = sqliteHDDB.getFilesByTag(tags[0]);
        assertEquals(2, actualFiles.size());
        for (File expectedFile : tagToFileMap.get(tags[0])) {
            TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
        }
    }

    public Map<Tag, File[]> prepareFileTagsForTest(Tag[] tags, File[] files) {
        Map<Tag, File[]> tagFileMap = new HashMap<>();
        tagFileMap.put(tags[0], new File[] { files[0], files[2] });
        tagFileMap.put(tags[1], new File[] { files[0], files[1] });
        tagFileMap.put(tags[2], new File[] { files[1] });
        tagFileMap.put(tags[3], new File[] {});
        for (Map.Entry<Tag, File[]> tagFile : tagFileMap.entrySet()) {
            File[] filesAssignedToTag = tagFile.getValue();
            for (File fileAssignedToTag : filesAssignedToTag) {
                sqliteHDDB.assignTag(fileAssignedToTag, tagFile.getKey());
            }
        }
        return tagFileMap;
    }
}