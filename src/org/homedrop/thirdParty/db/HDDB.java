package org.homedrop.thirdParty.db;

import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.model.Rule;

import java.util.List;

public interface HDDB {
    void onCreate();
    void onUpgrade();
    void onDrop();
    List<User> getAllUsers();
    List<Rule> getAllRules();

    void addFile(File file);
    void deleteFile(File file);
    void deleteFileById(long id);
    void updateFile(File file);
    List<File> getFilesByName(String name);
    List<File> getFilesByPath(String path);
    File getFileById(long id);

    void addUser(User user);
    void deleteUser(User user);
    void deleteUserById(long id);
    void updateUser(User user);
    User getUserByName(String name); //unique
    User getUserById(long id);

    void addTag(Tag tag);
    void deleteTag(Tag tag);
    void deleteTagById(long id);
    void updateTag(Tag tag);
    Tag getTagByName(String name); //unique
    Tag getTagById(long id);

    void assignTag(File file, Tag tag);
    void unassignTag(File file, Tag tag);
    List<Tag> getFileTags(File file);
    List<Tag> getFileTagsById(long id);
    List<File> getFilesByTag(Tag tag);
    List<File> getFilesByTagId(long id);

}
