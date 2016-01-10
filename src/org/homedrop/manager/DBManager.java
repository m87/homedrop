package org.homedrop.manager;

import org.homedrop.core.LifeCycle;
import org.homedrop.thirdParty.db.HDDB;

public class DBManager implements LifeCycle{
    private static DBManager ourInstance = new DBManager();

    private HDDB db;

    public HDDB getDb() {
        return db;
    }

    public static DBManager getInstance() {
        return ourInstance;
    }

    private DBManager() {
    }


    public void setDb(HDDB db) {
        this.db = db;
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
