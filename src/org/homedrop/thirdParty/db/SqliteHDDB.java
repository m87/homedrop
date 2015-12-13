package org.homedrop.thirdParty.db;

import org.homedrop.core.model.File;

import java.sql.Connection;
import java.util.List;

public class SqliteHDDB implements HDDB{
    private Connection open(){
        return null;
    }
    private  void close(Connection connection){}


    @Override
    public void onCreate() {

    }

    @Override
    public void onUpgrade() {

    }

    @Override
    public void onDrop() {

    }

    @Override
    public List<File> getAllFiles() {
        return null;
    }

    @Override
    public void addFile(File file) {

    }
}
