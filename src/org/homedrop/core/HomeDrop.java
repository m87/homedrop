package org.homedrop.core;

import org.homedrop.core.conifg.ConfigManager;
import org.homedrop.thirdParty.server.FtpServer;
import org.homedrop.thirdParty.server.ServerFactory;

public class HomeDrop {
    private FtpServer server;
    private ConfigManager config = ConfigManager.getInstance();

    public HomeDrop(){
        server = ServerFactory.createServer(config.getServerType());
        server.setUp(config.getServerConfigPath());
    }

    public void start(){}


}
