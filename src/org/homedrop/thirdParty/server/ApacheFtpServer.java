package org.homedrop.thirdParty.server;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

/** Apache ftp server representation */
public class ApacheFtpServer implements FtpServer{
    private FtpServerFactory serverFactory;
    private ListenerFactory listenerFactory;


    public ApacheFtpServer(){
        serverFactory = new FtpServerFactory();
        listenerFactory = new ListenerFactory();
        listenerFactory.setPort(1234); //getFrom HomeDrop settings

    }

    @Override
    public void setUp(String path) {

    }

    @Override
    public void start() {
        org.apache.ftpserver.FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            Log.d(LogTag.SERVER, "Server FtpException");
            e.printStackTrace();
        }
    }
}
