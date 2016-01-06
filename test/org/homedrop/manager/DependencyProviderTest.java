package org.homedrop.manager;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.SqliteHDDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.instanceOf;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class DependencyProviderTest {

    static DependencyProvider dependencyProvider;

    @Before
    public void setUp() throws Exception {
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop.cfg");
        dependencyProvider = DependencyProvider.getInstance();
        dependencyProvider.setConfig(config);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetDbConnectionSource() throws Exception {
        JdbcConnectionSource connectionSource = dependencyProvider.getDbConnectionSource();
        assertNotNull(connectionSource);
    }

    @Test
    public void testGetDbDriver() throws Exception {
        JdbcConnectionSource connectionSource = dependencyProvider.getDbConnectionSource();
        Constructor<? extends HDDB> ctor = dependencyProvider.getDbDriverConstructor();
        HDDB db = ctor.newInstance(new Object[] {connectionSource});
        assertThat(db, instanceOf(SqliteHDDB.class));

    }
}