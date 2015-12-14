package org.homedrop.core;

import org.homedrop.Command;
import org.homedrop.Plugin;
import org.homedrop.Result;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.PluginsManager;
import org.homedrop.thirdParty.server.FtpServer;
import org.homedrop.thirdParty.server.ServerFactory;

/** Main system class*/
public class HomeDrop implements FtpHandler{
    private FtpServer server;
    private ConfigManager config = ConfigManager.getInstance();

    /** Restore session from file*/
    public HomeDrop(String session){

        server = ServerFactory.createServer(config.getServerType());
        server.setUp(config.getServerConfigPath(), this);
    }
    /** Starts new session.*/
    public HomeDrop(){
        config.loadConfiguration("./test-env/homedrop.cfg");
        server = ServerFactory.createServer(config.getServerType());
        server.setUpUsers();
        server.setUp(config.getServerConfigPath(), this);

    }
    @Override
    public Result beforeCommand(Command command){
        return null;
    }
    @Override
    public Result afterCommand(Command command){
        return null;
    }
    @Override
    public Result onConnect(){
        return null;
    }
    @Override
    public Result onDisconnect(){
        return null;
    }

    /** Clean */
    public void onExit(){

    }

    /** Main method */
    public void start(){
        server.start();

        onExit();
    }



}
