package org.homedrop.thirdParty.server;


import org.homedrop.core.HomeDrop;
import org.homedrop.core.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface FtpServer {
    void setUpUsers(Collection<User> users);
    void setUp(String path, HomeDrop parent);
    void start();
    void stop();

}
