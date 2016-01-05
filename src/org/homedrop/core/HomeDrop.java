package org.homedrop.core;

import org.homedrop.Command;
import org.homedrop.Plugin;
import org.homedrop.Result;
import org.homedrop.core.model.User;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.PluginsManager;
import org.homedrop.manager.UsersManager;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.SqliteHDDB;
import org.homedrop.thirdParty.server.FtpServer;
import org.homedrop.thirdParty.server.ServerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Main system class
 */
public class HomeDrop implements FtpHandler, Runnable {
    private FtpServer server;
    private ConfigManager config = ConfigManager.getInstance();
    private HDDB db;

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
        config.loadConfiguration("./test-env/homedrop.cfg");
        server = ServerFactory.createServer(config.getServerType());
        server.setUpUsers(UsersManager.getInstance().getUsers().values());
        server.setUp(config.getServerConfigPath(), this);

        db = new SqliteHDDB();
        db.onCreate();

    }

    @Override
    public Result beforeCommand(Command command) {
        return null;
    }

    @Override
    public Result afterCommand(Command command) {
        return null;
    }

    @Override
    public Result onConnect() {
        Map<String, Plugin> map = PluginsManager.getInstance().getPlugins();
        for (String p : map.keySet()) {
            System.out.print("[" + map.get(p) + "]: ");
            map.get(p).handleCommand("aaa", null);
        }
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
