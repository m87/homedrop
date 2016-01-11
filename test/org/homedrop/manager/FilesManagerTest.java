package org.homedrop.manager;

import org.homedrop.core.model.File;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.ModelHelpers;
import org.homedrop.testUtils.TestHelpers;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.SqliteHDDB;
import org.homedrop.thirdParty.db.sqliteModels.FileEntity;
import org.homedrop.thirdParty.db.sqliteModels.UserEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilesManagerTest {

    private HDDB dbMock;

    @Before
    public void setUp() throws Exception {
        dbMock = mock(SqliteHDDB.class);
        DBManager.getInstance().setDb(dbMock);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testList() throws Exception {
        User user = new UserEntity();
        user.setName("username");
        user.setHome("home_path");
        when(dbMock.getUserByName(user.getName())).thenReturn(user);
        File[] filesByPath = { new FileEntity(), new FileEntity() };
        ModelHelpers.setFileFields(filesByPath[0], "fileName", 5621, Date.valueOf("2016-01-05"),
                user, "testpath/", File.FileType.File, 2);
        ModelHelpers.setFileFields(filesByPath[1], "fileName2", 113, Date.valueOf("2016-01-04"),
                user, "testpath/", File.FileType.File, 1);
        List<File> expectedFilesByPath = Arrays.asList(filesByPath);
        String path = "example_path";
        when(dbMock.getFilesByPath(Paths.get(user.getHome(), path).toString())).thenReturn(expectedFilesByPath);

        List<File> actualFilesByPath = FilesManager.getInstance().list(user.getName(), path);

        for (File expectedFile : expectedFilesByPath) {
            TestHelpers.assertListContainsItemEqual(actualFilesByPath, expectedFile);
        }
    }

    @Test
    public void testGetHome() throws Exception {
        User user = new UserEntity();
        user.setName("username");
        user.setHome("home_path");
        when(dbMock.getUserByName(user.getName())).thenReturn(user);

        String actualHome = FilesManager.getInstance().getHome(user.getName());

        assertEquals(user.getHome(), actualHome);
    }
}