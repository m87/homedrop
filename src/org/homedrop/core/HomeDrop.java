package org.homedrop.core;

import org.homedrop.core.conifg.ConfigManager;
import org.homedrop.core.model.Device;
import org.homedrop.thirdParty.server.FtpServer;
import org.homedrop.thirdParty.server.ServerFactory;

/** Main system class*/
public class HomeDrop {
    private FtpServer server;
    private ConfigManager config = ConfigManager.getInstance();

    /** Restore session from file*/
    public HomeDrop(String session){

        server = ServerFactory.createServer(config.getServerType());
        server.setUp(config.getServerConfigPath());
    }
    /** Starts new session.*/
    public HomeDrop(){
        Device.create("logical","main", "/home/rt/testMount", "/home/rt/testMount1").mount();
        //server = ServerFactory.createServer(config.getServerType());
        //server.setUp(config.getServerConfigPath());
    }


    /** Clean */
    public void onExit(){

    }

    /** Main method */
    public void start(){


        onExit();
    }



}
