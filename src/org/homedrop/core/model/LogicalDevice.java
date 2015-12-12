package org.homedrop.core.model;

import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.io.IOException;
/** Logical device representation */
public class LogicalDevice extends Device{
    public LogicalDevice(String name, String dev, String mount) {
        super(name, dev, mount);
    }

    @Override
    public void umount(){
         try {
            Runtime.getRuntime().exec("rm " + getMount());
            Log.i(LogTag.DEV, "removing symbolic link " + getDev());

        } catch (IOException e) {
            Log.d(LogTag.DEV, "Cannot remove symbolic link");
            e.printStackTrace();
        }
        Log.i(LogTag.DEV, "symbolic link " + getDev() + " removed");
    }
    @Override
    public void mount(){
        try {
            Runtime.getRuntime().exec("ln -s " + getDev() +" " + getMount());
            Log.i(LogTag.DEV, "creating symbolic linc " + getDev() + " on " + getMount());

        } catch (IOException e) {
            Log.d(LogTag.DEV, "Cannot create symbolic link");
            e.printStackTrace();
        }
        Log.i(LogTag.DEV, "symbolic linc " + getDev() + " on " + getMount() + " created");
    }
}
