package org.homedrop.core;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.homedrop.Request;
import org.homedrop.Result;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.manager.*;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.server.FtpServer;
import org.homedrop.thirdParty.server.ServerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;

/**
 * Main system class
 */
public class HomeDrop implements FtpHandler, Runnable {
    private FtpServer server;
    private ConfigManager config = ConfigManager.getInstance();
    private DependencyProvider dependencyProvider = DependencyProvider.getInstance();

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
        prepareDb();
        prepareServer();


    }

    public void prepareConfigurationAndDependencies(String configFilePath) {
        config.loadConfiguration(configFilePath);
        dependencyProvider.setConfig(config);
    }

    public void prepareServer() {
        server = ServerFactory.createServer(config.getServerType());
        UsersManager.getInstance().loadUsers();
        server.setUpUsers(UsersManager.getInstance().getUsers().values());
        server.setUp(config.getServerConfigPath(), this);
    }

    public void prepareDb() {
        JdbcConnectionSource connectionSource = null;
        try {
            connectionSource = dependencyProvider.getDbConnectionSource();
            Constructor<? extends HDDB> ctor = dependencyProvider.getDbDriverConstructor();
            HDDB db = ctor.newInstance(new Object[]{connectionSource});
            db.onCreate();
            DBManager.getInstance().setDb(db);
        } catch (Exception e) {
            Log.d(LogTag.CONFIG, "A critical error occurred: " + e.getMessage());
        }
    }

    @Override
    public Result beforeCommand(Request request) {
        return CommandManager.getInstance().beforeCommand(request);
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
                switch (input.split(" ")[0]){
                    case "exit" :{
                        server.stop();
                        stop = false;
                        break;
                    }
                    case "indexall":{
                        FilesManager.getInstance().indexAll(input.split(" ")[1]);
                        break;
                    }
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}
