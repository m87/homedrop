package org.homedrop.config;

import org.homedrop.core.conifg.Common;
import org.homedrop.core.conifg.ConfigManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigManagerTest {


    @Test
    public void loadTest(){
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop.cfg");
        assertEquals(Common.APACHE, config.getServerType());
        assertEquals("/opt/server.cfg", config.getServerConfigPath());

    }
}
