package org.homedrop.thirdParty.server;

import org.homedrop.core.conifg.ConfigManager;

public class ServerFactory {
    public static FtpServer createServer(String type){
        if(type.toLowerCase().equals(ConfigManager.ServerType.APACHE.name().toLowerCase())) return new ApacheFtpServer();
        return new ApacheFtpServer();
    }
}
