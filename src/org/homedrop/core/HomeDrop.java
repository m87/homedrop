package org.homedrop.core;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.homedrop.Command;
import org.homedrop.Plugin;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.model.User;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.core.utils.exceptions.HandlerException;
import org.homedrop.core.utils.exceptions.UnsupportedCommandException;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.DependencyProvider;
import org.homedrop.manager.PluginsManager;
import org.homedrop.manager.UsersManager;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.SqliteHDDB;
import org.homedrop.thirdParty.server.FtpServer;
import org.homedrop.thirdParty.server.ServerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Map;

/**
 * Main system class
 */
public class HomeDrop implements FtpHandler, Runnable {
    private FtpServer server;
    private ConfigManager config = ConfigManager.getInstance();
    private DependencyProvider dependencyProvider = DependencyProvider.getInstance();
    private HDDB db;
    private CommandHandlerFactory handlerFactory = new CommandHandlerFactory();

    /**
     * Restore session from file
     */
    public HomeDrop(String session) {
        server = ServerFactory.createServer(config.getServerType());
        server.setUp(config.getServerConfigPath(), this);
    }

    /**
     * Starts new session.
     */
    public HomeDrop() {
        //check if resumed
        prepareConfigurationAndDependencies("./test-env/homedrop.cfg");
        prepareServer();
        prepareDb();
    }

    public void prepareConfigurationAndDependencies(String configFilePath) {
        config.loadConfiguration(configFilePath);
        dependencyProvider.setConfig(config);
    }

    public void prepareServer() {
        server = ServerFactory.createServer(config.getServerType());
        server.setUpUsers(UsersManager.getInstance().getUsers().values());
        server.setUp(config.getServerConfigPath(), this);
    }

    public void prepareDb() {
        JdbcConnectionSource connectionSource = null;
        try {
            connectionSource = dependencyProvider.getDbConnectionSource();
            Constructor<? extends HDDB> ctor = dependencyProvider.getDbDriverConstructor();
            db = ctor.newInstance(new Object[]{connectionSource});
            db.onCreate();
        } catch (Exception e) {
            Log.d(LogTag.CONFIG, "A critical error occurred: " + e.getMessage());
        }
    }

    @Override
    public Result beforeCommand(Request request) {

        try {
            handlerFactory.create(request).handle();
        } catch (HandlerException | UnsupportedCommandException e) {
            Log.d(LogTag.HOMEDROP, "A critical error occurred: " + e.getMessage());
            e.printStackTrace();
        }


        //TODO PluginGuard.exectuePlugins();
        Map<String, Plugin> map = PluginsManager.getInstance().getPlugins();
        for (String p : map.keySet()) {
            System.out.print("[" + map.get(p) + "]: ");
            map.get(p).handleRequest(request);

        }
        return null;
    }

    @Override
    public Result afterCommand(Request request) {
        return null;
    }

    @Override
    public Result onConnect() {

        return null;
    }



    @Override
    public Result onDisconnect() {
        return null;
    }

    /**
     * Clean
     */
    public void onExit() {

    }

    /**
     * Main method
     */
    public void start() {
        server.start();
        new Thread(this).start();
        onExit();
    }


    @Override
    public void run() {
        boolean stop = true;
        String input;
        while (stop) {
            try {
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(System.in));

                input = br.readLine();
                switch (input){
                    case "exit" :{
                        server.stop();
                        stop = false;
                        break;
                    }
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}
