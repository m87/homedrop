package org.homedrop.thirdParty.server;


import org.homedrop.core.HomeDrop;

public interface FtpServer {
    void setUpUsers();
    void setUp(String path, HomeDrop parent);
    void start();

}
