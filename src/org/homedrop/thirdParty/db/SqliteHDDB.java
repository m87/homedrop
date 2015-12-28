package org.homedrop.thirdParty.db;

import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;

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

    @Override
    public void deleteFile(File file) {

    }

    @Override
    public void editFile(File file) {

    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void editUser(User user) {

    }

    @Override
    public void addTag(Tag tag) {

    }

    @Override
    public void deleteTag(Tag tag) {

    }

    @Override
    public void editTag(Tag tag) {

    }

    @Override
    public void assignTag(File file, Tag tag) {

    }

    @Override
    public void unassignTag(File file, Tag tag) {

    }
}
