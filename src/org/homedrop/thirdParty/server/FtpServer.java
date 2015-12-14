package org.homedrop.thirdParty.server;


public interface FtpServer {
    void setUpUsers();
    void setUp(String path);
    void start();
}
