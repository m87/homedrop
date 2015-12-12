package org.homedrop.config;

import org.homedrop.core.conifg.ConfigManager;
import org.homedrop.core.model.PrimaryDevice;
import org.homedrop.manager.DevicesManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigManagerTest {


    @Test
    public void loadTest(){
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration("test-env/homedrop.cfg");
        assertEquals(ConfigManager.ServerType.APACHE.name().toLowerCase(), config.getServerType().toLowerCase());
        assertEquals("/opt/server.cfg", config.getServerConfigPath());


        DevicesManager devices = DevicesManager.getInstance();
        assertEquals("a", devices.getDevices().get((short)0).getName());
        assertTrue(devices.getDevices().get((short)0) instanceof PrimaryDevice);
        assertEquals("/dev/sda111", devices.getDevices().get((short)0).getDev());
        assertEquals("/home/homedrop/db1", devices.getDevices().get((short)0).getMount());

        assertEquals(devices.getDevices().size(), 2);

    }
}
