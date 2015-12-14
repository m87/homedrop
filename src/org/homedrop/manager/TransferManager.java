package org.homedrop.manager;

import org.homedrop.core.LifeCycle;

public class TransferManager implements LifeCycle{
    private static TransferManager ourInstance = new TransferManager();

    public static TransferManager getInstance() {
        return ourInstance;
    }

    private TransferManager() {
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onExit() {

    }
}
