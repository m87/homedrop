package org.homedrop.thirdParty.db;

import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;

import java.util.List;

public interface HDDB {
    void onCreate();
    void onUpgrade();
    void onDrop();
    List<File> getAllFiles();

    void addFile(File file);
    void deleteFile(File file);
    void editFile(File file);

    void addUser(User user);
    void deleteUser(User user);
    void editUser(User user);

    void addTag(Tag tag);
    void deleteTag(Tag tag);
    void editTag(Tag tag);

    void assignTag(File file, Tag tag);
    void unassignTag(File file, Tag tag);

}
