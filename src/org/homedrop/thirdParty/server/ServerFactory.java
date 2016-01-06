package org.homedrop.thirdParty.server;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.homedrop.manager.ConfigManager;
import org.homedrop.manager.DependencyProvider;

/** Server factory */
public class ServerFactory {
    public static FtpServer createServer(String type){
        DependencyProvider dependencyProvider = DependencyProvider.getInstance();
        FtpServerFactory serverFactory = dependencyProvider.getServerFactory();
        ListenerFactory listenerFactory = dependencyProvider.getListenerFactory();
        if (isApacheServer(type))
            return new ApacheFtpServer(serverFactory, listenerFactory);
        return new ApacheFtpServer(serverFactory, listenerFactory);
    }

    public static boolean isApacheServer(String type) {
        return type.toLowerCase().equals(ConfigManager.ServerType.APACHE.name().toLowerCase());
    }
}
