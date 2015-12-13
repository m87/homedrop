package org.homedrop.thirdParty.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/** Apache ftp server representation */
public class ApacheFtpServer implements FtpServer{
    private FtpServerFactory serverFactory;
    private ListenerFactory listenerFactory;


    public ApacheFtpServer(){
        serverFactory = new FtpServerFactory();
        listenerFactory = new ListenerFactory();

    }

    @Override
    public void setUp(String path) {
        try {
            YamlReader reader = new YamlReader(new FileReader(path));
            Object object = reader.read();
            Map map = (Map)object;

            listenerFactory.setPort((int)map.get("port"));

        } catch (FileNotFoundException e) {
            Log.d(LogTag.SERVER, "config file not found");
            e.printStackTrace();
        } catch (YamlException e) {
            Log.d(LogTag.SERVER, "YAML file error");
            e.printStackTrace();
        }

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
