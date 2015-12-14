package org.homedrop.thirdParty.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.homedrop.Command;
import org.homedrop.core.HomeDrop;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void setUpUsers() {

    }

    @Override
    public void setUp(String path, final HomeDrop parent) {
        try {
            YamlReader reader = new YamlReader(new FileReader(path));
            Object object = reader.read();
            Map map = (Map)object;

            listenerFactory.setPort(Integer.parseInt((String)map.get("port")));

            serverFactory.addListener("default", listenerFactory.createListener());
            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            userManagerFactory.setFile(new File("/home/rt/users.props"));
            userManagerFactory.setPasswordEncryptor(new PasswordEncryptor() {
                @Override
                public String encrypt(String s) {
                    return s;
                }

                @Override
                public boolean matches(String s, String s1) {
                    return s.equals(s1);
                }
            });

            BaseUser user = new BaseUser();
            user.setName("test");
            user.setPassword("test");
            user.setHomeDirectory("/home/rt/testuser");
            List<Authority> authorities = new ArrayList<Authority>();
            authorities.add(new WritePermission());
            user.setAuthorities(authorities);
            UserManager um = userManagerFactory.createUserManager();
            try{
                um.save(user);
            }catch (FtpException e){

            }
            serverFactory.setUserManager(um);
            Map<String, Ftplet> m = new HashMap<>();
            m.put("ftplet1", new Ftplet() {
                @Override
                public void init(FtpletContext ftpletContext) throws FtpException {

                }

                @Override
                public void destroy() {

                }

                @Override
                public FtpletResult beforeCommand(FtpSession ftpSession, FtpRequest ftpRequest) throws FtpException, IOException {
                    System.out.print(ftpRequest.getCommand());
                    parent.beforeCommand(new Command("a",new String[]{"a"}));
                    return FtpletResult.DEFAULT;
                }

                @Override
                public FtpletResult afterCommand(FtpSession ftpSession, FtpRequest ftpRequest, FtpReply ftpReply) throws FtpException, IOException {
                    parent.afterCommand(new Command("a",new String[]{"a"}));
                    return FtpletResult.DEFAULT;
                }

                @Override
                public FtpletResult onConnect(FtpSession ftpSession) throws FtpException, IOException {
                    parent.onConnect();
                    return FtpletResult.DEFAULT;
                }

                @Override
                public FtpletResult onDisconnect(FtpSession ftpSession) throws FtpException, IOException {
                    parent.onDisconnect();
                    return FtpletResult.DEFAULT;
                }
            });

            serverFactory.setFtplets(m);


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
