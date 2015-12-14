package org.homedrop.core.model.device;

import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;

import java.io.IOException;
/** Real device representation */
public class PrimaryDevice extends Device{

    public PrimaryDevice(String name, String dev, String mount) {
        super(name, dev, mount);
    }

    @Override
    public void umount(){
         try {
            Runtime.getRuntime().exec("umount " + getDev());
            Log.i(LogTag.DEV, "unmounting " + getDev());

        } catch (IOException e) {
            Log.d(LogTag.DEV, "Cannot unmount devices");
            e.printStackTrace();
        }
        Log.i(LogTag.DEV, getDev() + " unmounted");
    }
    @Override
    public void mount(){
        try {
            Runtime.getRuntime().exec("mount " + getDev() +" " + getMount());
            Log.i(LogTag.DEV, "mounting " + getDev() + " on " + getMount());

        } catch (IOException e) {
            Log.d(LogTag.DEV, "Cannot mount devices");
            e.printStackTrace();
        }
        Log.i(LogTag.DEV, getDev() + " mounted");
    }
}
