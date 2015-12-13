package org.homedrop.manager;

public class TransferManager {
    private static TransferManager ourInstance = new TransferManager();

    public static TransferManager getInstance() {
        return ourInstance;
    }

    private TransferManager() {
    }
}
