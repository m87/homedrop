package org.homedrop.config;

import org.homedrop.manager.ConfigManager;
import org.homedrop.core.model.device.PrimaryDevice;
import org.homedrop.manager.DevicesManager;
import org.homedrop.manager.PluginsManager;
import org.homedrop.testUtils.TestHelpers;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigManagerTest {


    @Test
    public void loadTest(){
        ConfigManager config = ConfigManager.getInstance();
        config.loadConfiguration(TestHelpers.MainConfigFilePath);
        assertEquals(ConfigManager.ServerType.APACHE.name().toLowerCase(), config.getServerType().toLowerCase());
        assertEquals("./test-env/server.cfg", config.getServerConfigPath());
        assertEquals("./test-env/db.sql" , config.getDbPath());
        assertEquals("sqlite", config.getDbDriverName());

        DevicesManager devices = DevicesManager.getInstance();
        assertEquals("device", devices.getDevice().getName());
        assertTrue(devices.getDevice() instanceof PrimaryDevice);
        assertEquals("/dev/sda111", devices.getDevice().getDev());
        assertEquals("/home/homedrop/db1", devices.getDevice().getMount());

        // TODO: fix plugin case
        /*PluginsManager plugins  = PluginsManager.getInstance();
        assertNotNull(plugins.getPlugins().get("PrintPlugin"));*/


    }
}
