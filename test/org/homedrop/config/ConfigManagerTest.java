package org.homedrop.config;

import org.homedrop.core.conifg.ConfigManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigManagerTest {


    @Test
    public void loadTest(){
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop.cfg");
        assertEquals(ConfigManager.ServerType.APACHE.name().toLowerCase(), config.getServerType().toLowerCase());
        assertEquals("/opt/server.cfg", config.getServerConfigPath());

    }
}
