package org.homedrop.manager;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.SqliteHDDB;

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DependencyProvider {
    private static DependencyProvider ourInstance = new DependencyProvider();
    private ConfigManager config;
    private final Map<String, Class<? extends HDDB>> dbDriversTypes = new HashMap<>();

    public static DependencyProvider getInstance() {
        return ourInstance;
    }

    private DependencyProvider() {
        dbDriversTypes.put("sqlite", SqliteHDDB.class);
    }

    public void setConfig(ConfigManager config) {
        this.config = config;
    }

    public JdbcPooledConnectionSource getDbConnectionSource() throws SQLException {
        String dbConnectionString = "jdbc:" + config.getDbDriverName() + ":" + config.getDbPath();
        return new JdbcPooledConnectionSource(dbConnectionString);
    }

    public Constructor<? extends  HDDB> getDbDriverConstructor() throws Exception {
        String driverName = config.getDbDriverName();
        Constructor<? extends HDDB> ctor = dbDriversTypes.get(driverName).getConstructor(JdbcPooledConnectionSource.class);
        return ctor;
    }

    public FtpServerFactory getServerFactory() {
        return new FtpServerFactory();
    }

    public ListenerFactory getListenerFactory() {
        return new ListenerFactory();
    }

}
