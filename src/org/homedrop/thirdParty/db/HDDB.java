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

    /**
     * Get list of all users
     * @return list of all users
     */
    List<User> getAllUsers();

    /**
     * Get list of all rules
     * @return list of all rules
     */
    List<Rule> getAllRules();

    /**
     * Add new file to database. If success change file id for created entity id.
     * If fail change file id for IdFailed
     * @param file
     */
    void addFile(File file);

    /**
     * Delete file from database.
     * @param file
     */
    void deleteFile(File file);

    /**
     * Delete file with given id from database.
     * @param id
     */
    void deleteFileById(long id);

    /**
     * Update file with id equal to id of given file object.
     * @param file
     */
    void updateFile(File file);

    /**
     * Get all files of given name.
     * @param name
     * @return All files of given name
     */
    List<File> getFilesByName(String name);

    /**
     * Get all files of given path.
     * @param path
     * @return All files of given path
     */
    List<File> getFilesByPath(String path);

    /**
     * Get file of given id
     * @param id
     * @return File of given id
     */
    File getFileById(long id);

    /**
     * Add new user to database. If success change user id for created entity id.
     * If fail change user id for IdFailed.
     * @param user
     */
    void addUser(User user);

    /**
     * Delete user with given id from database.
     * @param user
     */
    void deleteUser(User user);

    /**
     * Delete user with given id from database.
     * @param id
     */
    void deleteUserById(long id);

    /**
     * Update user with id equal to id of given user object.
     * @param user
    */
    void updateUser(User user);

    /**
     * Get all users of given name.
     * @param name
     * @return All users of given name
     */
    User getUserByName(String name); //unique

    /**
     * Get user of given id.
      * @param id
     * @return User of given id
     */
    User getUserById(long id);

    /**
     * Add new tag to database. If success change tag id for created entity id.
     * If fail change tag id for IdFailed
     * @param tag
     */
    void addTag(Tag tag);

    /**
     * Delete tag with given id from database.
     * @param tag
     */
    void deleteTag(Tag tag);

    /**
     * Update tag with id equal to id of given tag object.
     * @param id
     */
    void deleteTagById(long id);

    /**
     * Update tag with id equal to id of given tag object.
     * @param tag
     */
    void updateTag(Tag tag);

    /**
     * Get tag of given name. Name is unique.
     * @param name
     * @return Tag of given name
     */
    Tag getTagByName(String name);

    /**
     * Get tag of given id.
     * @param id
     * @return Tag of given id
     */
    Tag getTagById(long id);

    /**
     * Assign tag to file
     * @param file
     * @param tag
     */
    void assignTag(File file, Tag tag);

    /**
     * Unassign tag from file
     * @param file
     * @param tag
     */
    void unassignTag(File file, Tag tag);

    /**
     * Get all tags of given file
     * @param file
     * @return All tags of given file
     */
    List<Tag> getFileTags(File file);

    /**
     * Get all tags of file of given id
     * @param id
     * @return All tags of file of given if
     */
    List<Tag> getFileTagsById(long id);

    /**
     * Get all files having given tag
     * @param tag
     * @return All file having given tag
     */
    List<File> getFilesByTag(Tag tag);

    /**
     * Get all files having tag of given id
     * @param id
     * @return All files having tag of given id
     */
    List<File> getFilesByTagId(long id);

}
