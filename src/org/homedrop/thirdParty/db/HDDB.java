package org.homedrop.thirdParty.db;

import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.core.model.Rule;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.core.utils.exceptions.ItemWithValueAlreadyExistsException;

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
     * Get list of all files
     * @return list of all files
     */
    List<File> getAllFiles();

    /**
     * Add new file to database. If success change file id for created entity id.
     * If fail change file id for IdFailed
     * @param file
     */
    void addFile(File file);

    /**
     * Delete file with given path from database.
     * If it is directory then delete all items of
     * parentPath equal to path
     * @param username
     * @param filePath
     */
    void deleteFileByPath(String username, String filePath) throws ItemNotFoundException;

    /**
     * Update file with id equal to id of given file object.
     * @param file
     */
    void updateFile(File file) throws ItemNotFoundException;

    /**
     * Rename file if exists to path not owned by any file.
     * @param username
     * @param pathSrc
     * @param pathDest
     * @throws ItemWithValueAlreadyExistsException
     */
    void renameFile(String username, String pathSrc, String pathDest) throws ItemNotFoundException,
        ItemWithValueAlreadyExistsException;

    /**
     * Remove subtree with pathDest if necessary and then rename.
     * @param username
     * @param pathSrc
     * @param pathDest
     * @throws ItemNotFoundException
     */
    void renameFileReplaceIfNecessary(String username, String pathSrc, String pathDest)
            throws ItemNotFoundException;
    /**
     * Get all files of given parent path.
     * @param path
     * @param username - path is relative, we want files of specific user
     * @return All files of given path
     */
    List<File> getFilesByParentPath(String username, String parentPath);

    /**
     * Get all files belonging to the subtree
     * @param username - path is relative, we want files of specific user
     * @param prefix
     * @return All files of given path prefix
     */
    List<File> getSubtreeWithRootDirectory(String username, String prefix);

    /**
     * Gets all files belonging to the subtree, yet without its root.
     * @param username
     * @param prefix
     * @return All files belonging to the subtree without its root
     * @throws ItemNotFoundException
     */
    List<File>getSubtreeExcludingRootDirectory(String username, String prefix);
    /**
     * Checks if file of owner with username and given path exists
     * @param username
     * @param path
     * @return true if file exists and false if doesn't exists
     */
    boolean fileExists(String username, String path);

    /**
     * Get file of given path.
     * @param path
     * @param owner - path is relative, so it's unique for user
     * @return File of given path
     */
    File getFileByPath(String path, User owner) throws ItemNotFoundException;

    /**
     * Get file of given id
     * @param id
     * @return File of given id
     * @throws ItemNotFoundException
     */
    File getFileById(long id) throws ItemNotFoundException;

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
    void updateUser(User user) throws ItemNotFoundException;

    /**
     * Get all users of given name.
     * @param name
     * @return All users of given name
     * @throws ItemNotFoundException
     */
    User getUserByName(String name) throws ItemNotFoundException;

    /**
     * Get user of given id.
     * @param id
     * @return User of given id
     * @throws ItemNotFoundException
     */
    User getUserById(long id) throws ItemNotFoundException;

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
    void deleteTag(Tag tag) throws ItemNotFoundException;

    /**
     * Delete tag with id equal to id of given tag object.
     * @param id
     */
    void deleteTagById(long id) throws ItemNotFoundException;

    /**
     * Update tag with id equal to id of given tag object.
     * @param tag
     */
    void updateTag(Tag tag) throws ItemNotFoundException;

    /**
     * Get tag of given name. Name is unique.
     * @param name
     * @return Tag of given name
     * @throws ItemNotFoundException
     */
    Tag getTagByName(String name) throws ItemNotFoundException;

    /**
     * Get tag of given id.
     * @param id
     * @return Tag of given id
     * @throws ItemNotFoundException
     */
    Tag getTagById(long id) throws ItemNotFoundException;

    /**
     * Add new rule to database. If success change rule id for created entity id.
     * If fail change rule id for IdFailed
     * @param rule
     */
    void addRule(Rule rule);

    /**
     * Update rule with id equal to id of given rule object.
     * @param rule
     * @throws ItemNotFoundException
     */
    void updateRule(Rule rule) throws ItemNotFoundException;

    /**
     * Get rule of given id.
     * @param id
     * @return Rule of given id
     * @throws ItemNotFoundException
     */
    Rule getRuleById(long id) throws ItemNotFoundException;

    /**
     * Delete rule with id equal to id of given rule object.
     * @param rule
     * @throws ItemNotFoundException
     */
    void deleteRule(Rule rule) throws ItemNotFoundException;

    /**
     * Delete rule with given id from database.
     * @param id
     * @throws ItemNotFoundException
     */
    void deleteRuleById(long id) throws ItemNotFoundException;

    /**
     * Get all valid rules of given file
     * @param file
     * @return All valid rules of given file
     */
    List<Rule> getValidRulesByFile(File file);

    /**
     * Get all valid global rules of user with given username
     * @param username
     * @return All valid global rules of user with given username
     */
    List<Rule> getValidGlobalRules(String username);

    /**
     * Get all valid global rules of given type of user with given username
     * @param username
     * @param type
     * @return All valid global rules of given type of user with given username
     */
    List<Rule> getValidGlobalRulesByType (String username, int type);

    /**
     * Get all valid specific rules of given type of file whose owner is user with given username
     * @param username
     * @param type
     * @param filePath
     * @return All valid specific rules of given type of file whose owner is user with given username
     */
    List<Rule> getValidSpecificRulesByType(String username, int type, String filePath);

    /**
     * Check if rule assigned to file with owner of username and filePath exists
     * @param username
     * @param filePath
     * @return true if rule exists, false if it doesn't exist
     */
    boolean ruleExists(String username, String filePath);

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
