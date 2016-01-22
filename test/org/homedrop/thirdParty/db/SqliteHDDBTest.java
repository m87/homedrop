package org.homedrop.thirdParty.db;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.table.TableUtils;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Identifiable;
import org.homedrop.core.utils.ModelHelpers;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.DependencyProvider;
import org.homedrop.testUtils.TestHelpers;
import org.homedrop.thirdParty.db.sqliteModels.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SqliteHDDBTest {

    static DependencyProvider dependencyProvider;
    static SqliteHDDB sqliteHDDB;
    static JdbcPooledConnectionSource connectionSource;

    static Map<Class, Method> areItemsEqualMethodsMap;
    static Map<String, Method> getRulesMethodsMap;
    static Map<String, Class[]> fieldTypesMap;

    @BeforeClass
    static public void setUpTest() throws Exception {
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop_test.cfg");
        dependencyProvider = DependencyProvider.getInstance();
        dependencyProvider.setConfig(config);
        connectionSource = dependencyProvider.getDbConnectionSource();
        sqliteHDDB = new SqliteHDDB(connectionSource);
        areItemsEqualMethodsMap = new HashMap<Class, Method>();
        for (Class theClass : new Class[] { User.class, Tag.class, File.class, Rule.class }){
            areItemsEqualMethodsMap.put(theClass, ModelHelpers.class
                    .getDeclaredMethod("areItemsEqual", theClass, theClass));
        }
        fieldTypesMap = new HashMap<String, Class[]>();
        fieldTypesMap.put("Id", new Class[] { Long.TYPE });
        fieldTypesMap.put("Name", new Class[] { String.class });
        fieldTypesMap.put("Path", new Class[] { String.class, User.class });

        getRulesMethodsMap = new HashMap<>();
        getRulesMethodsMap.put("getValidGlobalRules", SqliteHDDB.class
                .getDeclaredMethod("getValidGlobalRules", String.class));
        getRulesMethodsMap.put("getValidGlobalRulesByType", SqliteHDDB.class
                .getDeclaredMethod("getValidGlobalRulesByType", String.class, int.class));
        getRulesMethodsMap.put("getValidSpecificRulesByType", SqliteHDDB.class
                .getDeclaredMethod("getValidSpecificRulesByType", String.class, int.class, String.class));
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
        List<User> users = prepareUsersForTest();

        List<User> allUsers = sqliteHDDB.getAllUsers();

        assertEquals(allUsers.size(), users.size());
        for (User expectedUser : users) {
            TestHelpers.assertListContainsItemEqual(allUsers, expectedUser);
        }
    }

    @Test
    public void testGetAllRules() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);

        List<Rule> allRules = sqliteHDDB.getAllRules();

        assertEquals(descToRuleMap.size(), allRules.size());
        for (Rule expectedRule : descToRuleMap.values()) {
            TestHelpers.assertListContainsItemEqual(allRules, expectedRule);
        }
    }

    @Test
    public void testGetAllFiles() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> files = prepareFilesForTest(owners);

        List<File> allFiles = sqliteHDDB.getAllFiles();

        assertEquals(files.size(), allFiles.size());
        for (File expectedFile : files.values()) {
            TestHelpers.assertListContainsItemEqual(allFiles, expectedFile);
        }
    }

    @Test
    public void testAddFile() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        assertCollectionIsConsistentWithDb(pathToFileMap.values(), File.class, "Id");
    }

    @Test
    public void testRenameFileWhenDirectoryIsRenamed() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        String pathSrc = "testpath/location";
        String pathDest = "testpath/location_new";

        sqliteHDDB.renameFile(pathToFileMap.get(pathSrc).getOwner().getName(), pathSrc, pathDest);


        assertItemsProperlyRenamed(pathToFileMap.values(), pathSrc, pathDest, "file");
        assertItemsProperlyRenamed(descToRuleMap.values(), pathSrc, pathDest, "rule");
    }

    @Test
    public void testRenameFileWhenFileIsRenamed() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        String pathSrc = "testpath/location/name.ext";
        String pathDest = "testpath/location/newname.ext";

        sqliteHDDB.renameFile(pathToFileMap.get(pathSrc).getOwner().getName(), pathSrc, pathDest);

        assertItemsProperlyRenamed(pathToFileMap.values(), pathSrc, pathDest, "file");
        assertItemsProperlyRenamed(descToRuleMap.values(), pathSrc, pathDest, "rule");
    }

    void assertItemsProperlyRenamed(Collection<? extends Identifiable> items, String pathSrc, String pathDest, String type) {
        List<Identifiable> expectedItems = items.stream().map((item) -> {
            String oldPath = (item instanceof Rule) ? ((Rule)item).getFilePath() : ((File)item).getPath();
            if (null != oldPath) {
                String newPath = oldPath.replaceFirst(pathSrc + "(?=($|/))", pathDest);
                if (item instanceof Rule) {
                    ((Rule)item).setFilePath(newPath);
                }
                else {
                    ((File)item).setPath(newPath);
                    String oldParentPath = ((File)item).getParentPath();
                    ((File)item).setParentPath(oldParentPath.replaceFirst(pathSrc + "(?=($|/))", pathDest));
                    System.out.println(((File)item).getParentPath());
                }
            }
            return item;
        }).collect(toList());
        List<Identifiable> actualItems = new ArrayList<>();
        if (type.equals("rule")) {
            actualItems.addAll(sqliteHDDB.getAllRules());
        }
        else {
            actualItems.addAll(sqliteHDDB.getAllFiles());
        }
        assertEquals(expectedItems.size(), actualItems.size());
        for (Identifiable actualFile : actualItems) {
            if (type.equals("file")) {
                System.out.println(((File)actualFile).getParentPath());
            }
        }
        for (Identifiable expectedFile : expectedItems) {
            TestHelpers.assertListContainsItemEqual(actualItems, expectedFile);
        }
    }

    @Test
    public void testDeleteFileByPathWhenDirectoryIsDeleted() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Map<String, List<String>> tagAssignmentsToDelete = new HashMap<>();
        tagAssignmentsToDelete.put("testpath/location/name.ext", Arrays.asList("testtag", "testtag2"));
        tagAssignmentsToDelete.put("testpath/location", Arrays.asList("testtag"));
        Map<String, List<String>> tagAssignmentsToRemain = new HashMap<>();
        tagAssignmentsToRemain.put("testpath/location2/oname.ext", Arrays.asList("testtag2"));
        for (Map<String, List<String>> assignmentMap : Arrays.asList(tagAssignmentsToDelete, tagAssignmentsToRemain)) {
            for (Map.Entry<String, List<String>> pathToTag : assignmentMap.entrySet()) {
                for (String tagName : pathToTag.getValue()) {
                    File file = pathToFileMap.get(pathToTag.getKey());
                    sqliteHDDB.assignTag(file, nameToTagMap.get(tagName));
                }
            }
        }

        String path = "testpath/location";
        sqliteHDDB.deleteFileByPath(pathToFileMap.get(path).getOwner().getName(), path);

        assertItemsProperlyDeletedWhileFileDeleting(pathToFileMap.values(), sqliteHDDB.getAllFiles(), path);
        assertItemsProperlyDeletedWhileFileDeleting(descToRuleMap.values(), sqliteHDDB.getAllRules(), path);

        assertTagAssignmentsProperlyDeletedWhileFileDeleting(tagAssignmentsToDelete, nameToTagMap, false);
        assertTagAssignmentsProperlyDeletedWhileFileDeleting(tagAssignmentsToRemain, nameToTagMap, true);
    }

    @Test
    public void testDeleteFileByPathWhenSingleFileIsDeleted() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        String path = "testpath/location/otherdir/oname.ext";
        sqliteHDDB.assignTag(pathToFileMap.get(path), nameToTagMap.get("testtag"));

        sqliteHDDB.deleteFileByPath(pathToFileMap.get(path).getOwner().getName(), path);

        assertItemsProperlyDeletedWhileFileDeleting(pathToFileMap.values(), sqliteHDDB.getAllFiles(), path);
        assertItemsProperlyDeletedWhileFileDeleting(descToRuleMap.values(), sqliteHDDB.getAllRules(), path);

        assertEquals(0, sqliteHDDB.getFilesByTag(nameToTagMap.get("testtag")).size());
    }

    @Test
    public void testDeleteFileByPathWhenFileDoesNotExists() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        String path = "testpath/notExistingPath";

        try {
            sqliteHDDB.deleteFileByPath(owners.get(0).getName(), path);
            fail();
        }
        catch (ItemNotFoundException e) {}
        try {
            sqliteHDDB.deleteFileByPath("notExistingName", path);
            fail();
        }
        catch (ItemNotFoundException e) {}
    }

    void assertItemsProperlyDeletedWhileFileDeleting(Collection<? extends Identifiable> items, List<? extends Identifiable> actualItems, String path) {
        List<? extends Identifiable> expectedItems = items.stream().filter(item -> {
            if (item instanceof File) {
                return !doesPathBelongToTree(((File)item).getPath(), path);
            }
            else {
                return !doesRuleBelongToTree(((Rule)item), path);
            }
        }).collect(toList());
        assertEquals(expectedItems.size(), actualItems.size());
        List<Identifiable> typedActualItems = new ArrayList<Identifiable>();
        typedActualItems.addAll(actualItems);
        for (Identifiable expectedItem : expectedItems) {
            TestHelpers.assertListContainsItemEqual(typedActualItems, expectedItem);
        }
    }

    void assertTagAssignmentsProperlyDeletedWhileFileDeleting(Map<String, List<String>> tagAssignments,
                                                              Map<String, Tag> nameToTagMap, boolean assertValue) {
        for (Map.Entry<String, List<String>> pathToTags : tagAssignments.entrySet()) {
            for (String tagName : pathToTags.getValue()) {
                List<File> files = sqliteHDDB.getFilesByTag(nameToTagMap.get(tagName));
                assertEquals(assertValue, files.removeIf(file -> pathToTags.getKey().equals(file.getPath())));
            }
        }
    }

    boolean doesRuleBelongToTree(Rule rule, String rootPath) {
        return null != rule.getFilePath() && doesPathBelongToTree(rule.getFilePath(), rootPath);
    }

    boolean doesPathBelongToTree(String testedPath, String rootPath) {
        return testedPath.startsWith(rootPath + "/") || rootPath.equals(testedPath);
    }

    @Test
    public void testUpdateFile() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        String expectedPath = "testpath";
        pathToFileMap.get("testpath/location/name.ext").setPath(expectedPath);
        long expectedVersion = 820;
        pathToFileMap.get("testpath/location/name.ext").setVersion(expectedVersion);
        User owner = owners.get(1);
        pathToFileMap.get("testpath/location/name.ext").setOwner(owner);
        ModelHelpers.setUserFields(owner, "testuser2", "pass2", "home_testuser2");

        sqliteHDDB.updateFile(pathToFileMap.get("testpath/location/name.ext"));

        assertCollectionIsConsistentWithDb(pathToFileMap.values(), File.class, "Id");
    }

    @Test(expected=ItemNotFoundException.class)
    public void testUpdateFileWhenFileDoesNotExist() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        String expectedName = "notExistingPath";
        pathToFileMap.get("testpath/location").setPath(expectedName);
        pathToFileMap.get("testpath/location").setId(999);

        sqliteHDDB.updateFile(pathToFileMap.get("testpath/location"));
    }

    @Test
    public void testGetSubtreeMethods() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, File[]> methodNameToExpectedFilesMap = new HashMap<>();
        methodNameToExpectedFilesMap.put("getSubtreeWithRootDirectory",
                                         new File[] { pathToFileMap.get("testpath/location"), pathToFileMap.get("testpath/location/name.ext"),
                                                      pathToFileMap.get("testpath/location/otherdir"), pathToFileMap.get("testpath/location/otherdir/oname.ext") });
        methodNameToExpectedFilesMap.put("getSubtreeExcludingRootDirectory",
                                         new File[] { pathToFileMap.get("testpath/location/name.ext"),
                                                      pathToFileMap.get("testpath/location/otherdir"), pathToFileMap.get("testpath/location/otherdir/oname.ext") });
        assertSubtreeMethodWorksCorrectly(pathToFileMap.get("testpath/location").getOwner().getName(),
                                          "testpath/location", methodNameToExpectedFilesMap);

        methodNameToExpectedFilesMap.clear();
        methodNameToExpectedFilesMap.put("getSubtreeWithRootDirectory", new File[] { pathToFileMap.get("testpath/location2/oname.ext") });
        methodNameToExpectedFilesMap.put("getSubtreeExcludingRootDirectory", new File[] { pathToFileMap.get("testpath/location2/oname.ext") });

        assertSubtreeMethodWorksCorrectly(pathToFileMap.get("testpath/location2/oname.ext").getOwner().getName(),
                                          "testpath/location2", methodNameToExpectedFilesMap);
    }

    @Test
    public void testSubtreeMethodsWhenPrefixDoesNotExist() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, File[]> methodNameToExpectedFilesMap = new HashMap<>();
        methodNameToExpectedFilesMap.put("getSubtreeWithRootDirectory", new File[] {});
        methodNameToExpectedFilesMap.put("getSubtreeExcludingRootDirectory", new File[] {});

        assertSubtreeMethodWorksCorrectly(pathToFileMap.get("testpath/location").getOwner().getName(),
                                          "notExistingPrefix", methodNameToExpectedFilesMap);
    }

    @Test
    public void testGetSubtreeWithRootDirectoryWhenOwnerDoesNotFit() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, File[]> methodNameToExpectedFilesMap = new HashMap<>();
        methodNameToExpectedFilesMap.put("getSubtreeWithRootDirectory", new File[] {});
        methodNameToExpectedFilesMap.put("getSubtreeExcludingRootDirectory", new File[] {});

        assertSubtreeMethodWorksCorrectly("notExistingName", "testpath", methodNameToExpectedFilesMap);
        assertSubtreeMethodWorksCorrectly(pathToFileMap.get("test_parent_path2/testpath").getOwner().getName(),
                                          "testpath", methodNameToExpectedFilesMap);
    }

    private void assertSubtreeMethodWorksCorrectly(String username, String filePath, Map<String, File[]> methodToExpectedFileMap)
        throws Exception {
        for (Map.Entry<String, File[]> methodNameToExpectedFiles : methodToExpectedFileMap.entrySet()) {
            Method method = SqliteHDDB.class.getDeclaredMethod(methodNameToExpectedFiles.getKey(), String.class, String.class);
            List<File> actualFiles = (List<File>)method.invoke(sqliteHDDB, username, filePath);
            File[] expectedFiles = methodNameToExpectedFiles.getValue();
            assertEquals(expectedFiles.length, actualFiles.size());
            for (File expectedFile : expectedFiles) {
                TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
            }
        }
    }

    @Test
    public void testGetFileByPath() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        String[] fieldNames = new String[] {"Path", "Owner"};
        assertCollectionIsConsistentWithDb(pathToFileMap.values(), File.class, fieldNames);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testGetFileByPathWhenFileDoesNotOccur() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        sqliteHDDB.getFileByPath("notExistingPath", pathToFileMap.get("testpath/location").getOwner());
    }

    @Test(expected = ItemNotFoundException.class)
    public void testGetFileByPathWhenOwnerDoesNotOccur() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        User notExistingOwner = pathToFileMap.get("testpath/location/name.ext").getOwner();
        notExistingOwner.setId(9999);
        sqliteHDDB.getFileByPath("testpath/location/name.ext", notExistingOwner);
    }

    @Test
    public void testGetFilesByParentPath() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        String[] expectedFileNames = { "testpath/location/name.ext", "testpath/location/otherdir" };
        User owner = owners.get(0);

        List<File> actualFiles = sqliteHDDB.getFilesByParentPath("testuser", "testpath/location");

        assertEquals(expectedFileNames.length, actualFiles.size());
        for (String expectedFileName : expectedFileNames) {
            TestHelpers.assertListContainsItemEqual(actualFiles, pathToFileMap.get(expectedFileName));
        }
    }

    @Test
    public void testGetFilesByParentPathWhenFileDoesNotExist() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);

        List<File> actualFiles = sqliteHDDB.getFilesByParentPath("testuser", "notExistingPath");

        assertEquals(0, actualFiles.size());
    }

    @Test
    public void testGetFilesByParentPathWhenOwnerDoesNotOccur() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        sqliteHDDB.getFilesByParentPath("notExistingOwner", "testpath/location");
    }

    @Test
    public void testFileExists() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);

        assertTrue(sqliteHDDB.fileExists("testuser2", "test_parent_path2/testpath"));
        assertFalse(sqliteHDDB.fileExists("testuser2", "notExistingPath"));
    }

    public Map<String, File> prepareFilesForTest(List<User> owners) {
        Map<String, File> pathToFileMap = new HashMap<>();

        Date firstDate = ModelHelpers.makeDateFromLocalDate(LocalDate.of(2016, 1, 4));
        Date secondDate = ModelHelpers.makeDateFromLocalDate(LocalDate.of(2016, 1, 5));
        pathToFileMap.put("testpath", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath"), 8998, firstDate,
                owners.get(0), "/", "testpath", File.FileType.Directory, 3);

        pathToFileMap.put("testpath/location", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath/location"), 5621, secondDate,
                owners.get(0), "testpath", "testpath/location", File.FileType.Directory, 2);

        pathToFileMap.put("testpath/location/name.ext", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath/location/name.ext"), 531, secondDate,
                owners.get(0), "testpath/location", "testpath/location/name.ext", File.FileType.File, 2);

        pathToFileMap.put("testpath/location/otherdir", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath/location/otherdir"), 7823, firstDate,
                owners.get(0), "testpath/location", "testpath/location/otherdir", File.FileType.Directory, 4);

        pathToFileMap.put("testpath/location/otherdir/oname.ext", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath/location/otherdir/oname.ext"), 74, firstDate,
                owners.get(0), "testpath/location/otherdir", "testpath/location/otherdir/oname.ext", File.FileType.File, 1);

        pathToFileMap.put("testpath/location2/oname.ext", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath/location2/oname.ext"), 321, secondDate,
                owners.get(0), "testpath/location2/", "testpath/location2/oname.ext", File.FileType.File, 1);

        pathToFileMap.put("testpath2/location2.ext", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("testpath2/location2.ext"), 113, firstDate,
                owners.get(0), "testpath2", "testpath2/location2.ext", File.FileType.File, 1);

        pathToFileMap.put("test_parent_path2/testpath", new FileEntity());
        ModelHelpers.setFileFields(pathToFileMap.get("test_parent_path2/testpath"), 585, secondDate,
                owners.get(1), "test_parent_path2", "test_parent_path2/testpath", File.FileType.File, 4);

        for (File file : pathToFileMap.values()) {
            sqliteHDDB.addFile(file);
        }

        return pathToFileMap;
    }

    public static void deleteItemTestTemplate(Collection<? extends Identifiable> itemCollection, Class itemType) throws Exception {
        HDDB sqliteHDDBMock = mock(SqliteHDDB.class);
        Method deleteMethod = SqliteHDDB.class.getDeclaredMethod("delete" + itemType.getSimpleName(), itemType);

        List<? extends Identifiable> items = new ArrayList<>(itemCollection);
        deleteMethod.invoke(sqliteHDDBMock, items.get(0));

        HDDB verifyMock = verify(sqliteHDDBMock, times(1));
        deleteMethod.invoke(verifyMock, items.get(0));
        verifyNoMoreInteractions(sqliteHDDBMock);
    }

    public static void deleteItemByIdTestTemplate(List<? extends Identifiable> items, Class itemType) throws Exception {
        Method deleteMethod = SqliteHDDB.class.getDeclaredMethod("delete" + itemType.getSimpleName() + "ById", Long.TYPE);

        deleteMethod.invoke(sqliteHDDB, items.get(0).getId());

        Method getByIdMethod = SqliteHDDB.class.getDeclaredMethod("get" + itemType.getSimpleName() + "ById", Long.TYPE);
        try {
            Identifiable item = (Identifiable) getByIdMethod.invoke(sqliteHDDB, items.get(0).getId());
            fail("Expected an exception");
        }
        catch (InvocationTargetException e) {
            assertThat(e.getCause(), instanceOf(ItemNotFoundException.class));
        }
        Identifiable actualItem = (Identifiable) getByIdMethod.invoke(sqliteHDDB, items.get(1).getId());
        Method areFieldsEqualMethod = areItemsEqualMethodsMap.get(itemType);
        assertTrue((boolean)areFieldsEqualMethod.invoke(null, items.get(1), actualItem));
    }

    @Test
    public void testAddUser() throws Exception {
        List<User> users = prepareUsersForTest();
        assertCollectionIsConsistentWithDb(users, User.class, "Id");
    }

    public void assertCollectionIsConsistentWithDb(Collection<? extends Identifiable> items, Class itemType, String getterFieldName) throws Exception {
        assertCollectionIsConsistentWithDb(items, itemType, new String[] {getterFieldName});
    }

    public void assertCollectionIsConsistentWithDb(Collection<? extends Identifiable> items, Class itemType, String[] getterFieldNames) throws Exception {
        Method getByFieldMethod = SqliteHDDB.class
                .getDeclaredMethod("get" + itemType.getSimpleName() + "By" + getterFieldNames[0], fieldTypesMap.get(getterFieldNames[0]));
        List<Method> getFieldFromItemMethods = new ArrayList<>();
        for (String getterFieldName : getterFieldNames) {
            getFieldFromItemMethods.add(itemType.getMethod("get" + getterFieldName));
        }
        Method areFieldsEqualMethod = areItemsEqualMethodsMap.get(itemType);
        for (Identifiable expectedItem : items) {
            List<Object> expectedFieldValues = new ArrayList<>();
            for (Method getFieldFromItemMethod : getFieldFromItemMethods) {
                Object fieldValue = getFieldFromItemMethod.invoke(expectedItem);
                expectedFieldValues.add(fieldValue);
            }
            Identifiable actualItem = (Identifiable) getByFieldMethod.invoke(sqliteHDDB, expectedFieldValues.toArray());
            assertTrue((boolean)areFieldsEqualMethod.invoke(null, expectedItem, actualItem));
        }
    }

    @Test
    public void testAddUserWhenUserAlreadyExists() {
        List<User> users = prepareUsersForTest();

        sqliteHDDB.addUser(users.get(0));

        assertEquals(SqliteHDDB.IdFailed, users.get(0).getId());
    }

    @Test
    public void testDeleteUserById() throws Exception {
        List<User> users = prepareUsersForTest();
        deleteItemByIdTestTemplate(users, User.class);
    }

    @Test
    public void testDeleteUser() throws Exception {
        List<User> users = prepareUsersForTest();
        deleteItemTestTemplate(users, User.class);
    }

    @Test
    public void testUpdateUser() throws Exception {
        List<User> users = prepareUsersForTest();
        String expectedName = "newName";
        users.get(0).setName(expectedName);

        sqliteHDDB.updateUser(users.get(0));

        assertCollectionIsConsistentWithDb(users, User.class, "Id");
    }

    @Test(expected = ItemNotFoundException.class)
    public void testUpdateUserWhenUserDoesNotExist() throws Exception {
        List<User> users = prepareUsersForTest();
        User notExistingUser = new UserEntity();
        ModelHelpers.setUserFields(notExistingUser, "foo", "foo", "foo");
        notExistingUser.setId(999);

        sqliteHDDB.updateUser(notExistingUser);
    }

    @Test
    public void testGetUserByName() throws Exception {
        List<User> users = prepareUsersForTest();
        assertCollectionIsConsistentWithDb(users, User.class, "Name");
    }

    @Test(expected = ItemNotFoundException.class)
    public void testGetUserByNameWhenNameDoesNotOccur() throws Exception {
        List<User> users = prepareUsersForTest();

        User user = sqliteHDDB.getUserByName("notOccurringName");
    }

    public List<User> prepareUsersForTest() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            users.add(new UserEntity());
        }
        ModelHelpers.setUserFields(users.get(0), "testuser", "pass", "testuser_home");
        ModelHelpers.setUserFields(users.get(1), "testuser2", "pass2", "home_testuser2");
        for (User user : users) {
            sqliteHDDB.addUser(user);
        }
        return users;
    }

    @Test
    public void testAddTag() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        assertCollectionIsConsistentWithDb(nameToTagMap.values(), Tag.class, "Id");
    }

    @Test
    public void testAddTagWhenTagAlreadyExists() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Tag duplicateTag = new TagEntity();
        duplicateTag.setName("testtag");

        sqliteHDDB.addTag(duplicateTag);

        assertTrue(TestHelpers.areItemsEqual(nameToTagMap.get("testtag"), duplicateTag));
    }

    @Test
    public void testDeleteTag() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        deleteItemTestTemplate(nameToTagMap.values(), Tag.class);
    }

    @Test
    public void testDeleteTagUnassignFromFile() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Map<Tag, List<File>> fileTagMap = prepareFileTagsForTest(nameToTagMap, pathToFileMap);

        long deletedTagId = nameToTagMap.get("testtag").getId();
        sqliteHDDB.deleteTag(nameToTagMap.get("testtag"));
        List<File> actualFiles = sqliteHDDB.getFilesByTag(nameToTagMap.get("testtag"));
        assertEquals(0, actualFiles.size());
    }

    @Test
    public void testDeleteTagById() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        List<Tag> tagList = new ArrayList<Tag>();
        tagList.addAll(nameToTagMap.values());
        deleteItemByIdTestTemplate(tagList, Tag.class);
    }

    @Test
    public void testUpdateTag() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        String expectedName = "newName";
        nameToTagMap.get("testtag").setName(expectedName);

        sqliteHDDB.updateTag(nameToTagMap.get("testtag"));

        assertCollectionIsConsistentWithDb(nameToTagMap.values(), Tag.class, "Id");
    }

    @Test(expected = ItemNotFoundException.class)
    public void testUpdateTagWhenTagDoesNotExist() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Tag notExistingTag = new TagEntity();
        notExistingTag.setId(999);
        notExistingTag.setName("notExisting");

        sqliteHDDB.updateTag(notExistingTag);
    }

    @Test
    public void testGetTagByName() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        assertCollectionIsConsistentWithDb(nameToTagMap.values(), Tag.class, "Name");
    }

    @Test(expected=ItemNotFoundException.class)
    public void testGetTagByNameWhenNameDoesNotOccur() throws Exception {
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Tag tag = sqliteHDDB.getTagByName("notOccurringName");
    }

    public Map<String, Tag> prepareTagsForTest() {
        Map<String, Tag> nameToTagMap = new HashMap<>();
        String[] names = { "testtag", "testtag2", "testtag3", "notUsedTag" };
        for (int i = 0; i < 4; ++i) {
            Tag tag = new TagEntity();
            tag.setName(names[i]);
            nameToTagMap.put(names[i], tag);
            sqliteHDDB.addTag(tag);
        }
        return nameToTagMap;
    }

    @Test
    public void testAddRule() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);

        assertCollectionIsConsistentWithDb(descToRuleMap.values(), Rule.class, "Id");
    }

    @Test
    public void testUpdateRule() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        descToRuleMap.get("VN_testpath/location").setFilePath("testpath/location/name.ext");
        descToRuleMap.get("VN_testpath/location").setOwner(owners.get(1));
        descToRuleMap.get("VN_testpath/location").setBody("{field: value}");

        sqliteHDDB.updateRule(descToRuleMap.get("VN_testpath/location"));

        assertCollectionIsConsistentWithDb(descToRuleMap.values(), Rule.class, "Id");
    }

    @Test
    public void testGetValidRulesByFile() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);

        Map<File, List<Rule>> expectedRulesForFileMap = new HashMap<>();
        expectedRulesForFileMap.put(pathToFileMap.get("testpath/location"),
                                    Arrays.asList(descToRuleMap.get("VN_testpath/location"), descToRuleMap.get("VV_global_owner0"),
                                                  descToRuleMap.get("VV_global_owner01")));
        expectedRulesForFileMap.put(pathToFileMap.get("testpath/location2/oname.ext"),
                                    Arrays.asList(descToRuleMap.get("VV_testpath/location2/oname.ext"), descToRuleMap.get("VV_global_owner0"),
                                                  descToRuleMap.get("VV_global_owner01")));
        expectedRulesForFileMap.put(pathToFileMap.get("test_parent_path2/testpath"), Arrays.asList(descToRuleMap.get("VV_global_owner1")));

        for (Map.Entry<File, List<Rule>> expectedRulesForFile : expectedRulesForFileMap.entrySet()) {
            List<Rule> actualRulesForFile = sqliteHDDB.getValidRulesByFile(expectedRulesForFile.getKey());
            assertEquals(expectedRulesForFile.getValue().size(), actualRulesForFile.size());
            for (Rule expectedRule : expectedRulesForFile.getValue()) {
                TestHelpers.assertListContainsItemEqual(actualRulesForFile, expectedRule);
            }
        }
    }

    @Test
    public void testGetValidGlobalRules() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);

        Map<User, Rule[]> expectedRulesForUserMap = new HashMap<>();
        expectedRulesForUserMap.put(owners.get(0), new Rule[] { descToRuleMap.get("VV_global_owner0"),
                                                            descToRuleMap.get("VV_global_owner01") });
        expectedRulesForUserMap.put(owners.get(1), new Rule[] { descToRuleMap.get("VV_global_owner1") });

        List<Object> params = new ArrayList<>();
        assertCorrectRulesAreGot(expectedRulesForUserMap, "getValidGlobalRules", params);
    }

    @Test
    public void testGetValidGlobalRulesByType() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);

        Map<User, Rule[]> expectedRulesForUserMap = new HashMap<>();
        expectedRulesForUserMap.put(owners.get(0), new Rule[] { descToRuleMap.get("VV_global_owner01") });
        expectedRulesForUserMap.put(owners.get(1), new Rule[] { descToRuleMap.get("VV_global_owner1") });

        List<Object> params = new ArrayList<Object>();
        params.add(2);
        assertCorrectRulesAreGot(expectedRulesForUserMap, "getValidGlobalRulesByType", params);

        expectedRulesForUserMap.clear();
        expectedRulesForUserMap.put(owners.get(0), new Rule[] { descToRuleMap.get("VV_global_owner0") });
        expectedRulesForUserMap.put(owners.get(1), new Rule[] {});

        params.clear();
        params.add(8);
        assertCorrectRulesAreGot(expectedRulesForUserMap, "getValidGlobalRulesByType", params);
    }

    @Test
    public void testGetValidSpecificRulesByTypeWhen3Args() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);


        Map<User, Rule[]> expectedRulesForUserMap = new HashMap<>();
        expectedRulesForUserMap.put(owners.get(0), new Rule[] { descToRuleMap.get("VV_testpath/location2/oname.ext") });
        expectedRulesForUserMap.put(owners.get(1), new Rule[] {});

        List<Object> params = new ArrayList<Object>();
        params.add(2);
        params.add("testpath/location2/oname.ext");
        assertCorrectRulesAreGot(expectedRulesForUserMap, "getValidSpecificRulesByType", params);
    }

    @Test
    public void testRuleExists() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);

        assertTrue(sqliteHDDB.ruleExists(owners.get(0).getName(), "testpath/location"));
        assertFalse(sqliteHDDB.ruleExists("notExistingName", "testpath/location"));
        assertFalse(sqliteHDDB.ruleExists(owners.get(1).getName(), "notExistingPath"));
    }

    public void assertCorrectRulesAreGot(Map<User, Rule[]> expectedRulesForUserMap, String methodName, List<Object> params) throws Exception {
        params.add(0, "");
        for (Map.Entry<User, Rule[]> expectedRulesForUser : expectedRulesForUserMap.entrySet()) {
            String username = expectedRulesForUser.getKey().getName();
            params.set(0, username);
            Method getRulesMethod = getRulesMethodsMap.get(methodName);
            List<Rule> actualRulesForUser = (List<Rule>) getRulesMethod.invoke(sqliteHDDB, params.toArray());

            assertEquals(expectedRulesForUser.getValue().length, actualRulesForUser.size());
            for (Rule expectedRule : expectedRulesForUser.getValue()) {
                TestHelpers.assertListContainsItemEqual(actualRulesForUser, expectedRule);
            }
        }
    }

    @Test
    public void testDeleteRule() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        deleteItemTestTemplate(descToRuleMap.values(), Rule.class);
    }

    @Test
    public void testDeleteRuleById() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Rule> descToRuleMap = prepareRulesForTest(owners, pathToFileMap);
        List<Rule> actualRules = new ArrayList<>();
        actualRules.addAll(descToRuleMap.values());
        deleteItemByIdTestTemplate(actualRules, Rule.class);
    }

    public Map<String, Rule> prepareRulesForTest(List<User> owners, Map<String, File> pathToFileMap) {
        Map<String, Rule> descToRuleMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        Date validHoldsSince = ModelHelpers.makeDateFromLocalDate(LocalDate.parse("2016-01-01", formatter));
        Date invalidHoldsUntil = ModelHelpers.makeDateFromLocalDate(LocalDate.parse("2016-01-03", formatter));
        addRuleToMap(descToRuleMap, "testpath/location", "VN", "{}", validHoldsSince, null, 2, owners.get(0));
        addRuleToMap(descToRuleMap, "testpath/location", "VI",  "{}", validHoldsSince, invalidHoldsUntil, 2, owners.get(0));
        addRuleToMap(descToRuleMap, "testpath/location/name.ext", "VI", "{}", validHoldsSince, invalidHoldsUntil, 8, owners.get(0));
        Date tomorrow = ModelHelpers.makeDateFromLocalDate(LocalDateTime.now().plusDays(1).toLocalDate());
        addRuleToMap(descToRuleMap, "testpath/location/otherdir", "NV", "{}", null, tomorrow, 5, owners.get(0));
        Date yesterday = ModelHelpers.makeDateFromLocalDate(LocalDateTime.now().minusDays(1).toLocalDate());
        addRuleToMap(descToRuleMap, "testpath/location/otherdir/oname.ext", "VV", "{}", yesterday, tomorrow, 2, owners.get(0));
        addRuleToMap(descToRuleMap, "testpath/location2/oname.ext", "VV", "{}", validHoldsSince, tomorrow, 2, owners.get(0));
        // global rules
        addRuleToMap(descToRuleMap, "global_owner0", "VV", "{}", yesterday, tomorrow, 8, owners.get(0));
        descToRuleMap.get("VV_global_owner0").setFilePath(null);
        addRuleToMap(descToRuleMap, "global_owner01", "VV", "{}", yesterday, tomorrow, 2, owners.get(0));
        descToRuleMap.get("VV_global_owner01").setFilePath(null);
        addRuleToMap(descToRuleMap, "global_owner1", "VV", "{}", yesterday, tomorrow, 2, owners.get(1));
        descToRuleMap.get("VV_global_owner1").setFilePath(null);
        addRuleToMap(descToRuleMap, "global_owner02", "NI", "{}", null, yesterday, 2, owners.get(0));
        descToRuleMap.get("NI_global_owner02").setFilePath(null);

        for (Rule rule : descToRuleMap.values()) {
            sqliteHDDB.addRule(rule);
        }
        return descToRuleMap;
    }

    void addRuleToMap(Map<String, Rule> descRuleMap, String path, String desc, String body,
                      Date holdsSince, Date holdsUntil, int type, User owner) {
        String key = desc + "_" + path;
        descRuleMap.put(key, new RuleEntity());
        ModelHelpers.setRuleFields(descRuleMap.get(key), body, holdsSince, holdsUntil, type, path, owner);
    }


    @Test
    public void testAssignTag() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Map<Tag, List<File>> tagToFileMap = prepareFileTagsForTest(nameToTagMap, pathToFileMap);

        for (Map.Entry<Tag, List<File>> tagFile : tagToFileMap.entrySet()) {
            List<File> expectedFiles = tagFile.getValue();
            List<File> actualFiles = sqliteHDDB.getFilesByTag(tagFile.getKey());
            assertEquals(expectedFiles.size(), actualFiles.size());
            for (File expectedFile : expectedFiles) {
                TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
            }
        }
    }

    @Test
    public void testUnassignTag() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Map<Tag, List<File>> tagToFileMap = prepareFileTagsForTest(nameToTagMap, pathToFileMap);

        sqliteHDDB.unassignTag(pathToFileMap.get("testpath/location/otherdir"), nameToTagMap.get("testtag3"));
        List<File> actualFiles = sqliteHDDB.getFilesByTag(nameToTagMap.get("testtag3"));
        assertEquals(0, actualFiles.size());
        List<Tag> actualTags = sqliteHDDB.getFileTags(pathToFileMap.get("testpath/location/otherdir"));
        assertEquals(1, actualTags.size());
        TestHelpers.assertListContainsItemEqual(actualTags, nameToTagMap.get("testtag"));

        sqliteHDDB.unassignTag(pathToFileMap.get("testpath/location/name.ext"), nameToTagMap.get("testtag"));
        actualFiles = sqliteHDDB.getFilesByTag(nameToTagMap.get("testtag"));
        assertEquals(1, actualFiles.size());
        TestHelpers.assertListContainsItemEqual(actualFiles, pathToFileMap.get("testpath/location/otherdir"));
        actualTags = sqliteHDDB.getFileTags(pathToFileMap.get("testpath/location/name.ext"));
        assertEquals(1, actualTags.size());
        TestHelpers.assertListContainsItemEqual(actualTags, nameToTagMap.get("testtag2"));
    }

    @Test
    public void testGetFileTags() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Map<Tag, List<File>> tagToFileMap = prepareFileTagsForTest(nameToTagMap, pathToFileMap);

        List<Tag> actualTags = sqliteHDDB.getFileTags(pathToFileMap.get("testpath/location/name.ext"));
        assertEquals(2, actualTags.size());
        TestHelpers.assertListContainsItemEqual(actualTags, nameToTagMap.get("testtag"));
        TestHelpers.assertListContainsItemEqual(actualTags, nameToTagMap.get("testtag2"));
    }

    @Test
    public void testGetFilesByTag() throws Exception {
        List<User> owners = prepareUsersForTest();
        Map<String, File> pathToFileMap = prepareFilesForTest(owners);
        Map<String, Tag> nameToTagMap = prepareTagsForTest();
        Map<Tag, List<File>> tagToFileMap = prepareFileTagsForTest(nameToTagMap, pathToFileMap);

        List<File> actualFiles = sqliteHDDB.getFilesByTag(nameToTagMap.get("testtag"));
        assertEquals(2, actualFiles.size());
        for (File expectedFile : tagToFileMap.get(nameToTagMap.get("testtag"))) {
            TestHelpers.assertListContainsItemEqual(actualFiles, expectedFile);
        }
    }

    public Map<Tag, List<File>> prepareFileTagsForTest(Map<String, Tag> nameToTagMap, Map<String, File> pathToFileMap) {
        Map<Tag, List<File>> tagFileMap = new HashMap<>();
        tagFileMap.put(nameToTagMap.get("testtag"), Arrays.asList(pathToFileMap.get("testpath/location/name.ext"),
                                                                  pathToFileMap.get("testpath/location/otherdir")));
        tagFileMap.put(nameToTagMap.get("testtag2"), Arrays.asList(pathToFileMap.get("testpath/location/name.ext"),
                                                                   pathToFileMap.get("testpath/location/otherdir/oname.ext")));
        tagFileMap.put(nameToTagMap.get("testtag3"), Arrays.asList(pathToFileMap.get("testpath/location/otherdir")));
        tagFileMap.put(nameToTagMap.get("notUsedTag"), Collections.emptyList());
        for (Map.Entry<Tag, List<File>> tagFile : tagFileMap.entrySet()) {
            List<File> filesAssignedToTag = tagFile.getValue();
            for (File fileAssignedToTag : filesAssignedToTag) {
                sqliteHDDB.assignTag(fileAssignedToTag, tagFile.getKey());
            }
        }
        return tagFileMap;
    }
}