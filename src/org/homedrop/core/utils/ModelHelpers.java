package org.homedrop.core.utils;

import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class ModelHelpers {
    public static void setUserFields(User user, String name, String password, String home) {
        user.setName(name);
        user.setPassword(password);
        user.setHome(home);
    }

    public static void setFileFields(File file, long checkSum, Date lastChange,
                                     User owner, String parentPath, String path, File.FileType type, long version) {
        file.setCheckSum(checkSum);
        file.setLastChange(lastChange);
        file.setOwner(owner);
        file.setParentPath(parentPath);
        file.setPath(path);
        file.setType(type);
        file.setVersion(version);
    }

    public static void setRuleFields(Rule rule, String body, Date holdsSince,
                                     Date holdsUntil, int type, String filePath, User owner) {
        rule.setBody(body);
        rule.setHoldsSince(holdsSince);
        rule.setHoldsUntil(holdsUntil);
        rule.setType(type);
        rule.setFilePath(filePath);
        rule.setOwner(owner);
    }

    public static boolean areItemsEqual(User user1, User user2) {
        if (user1.getId() != user2.getId()) {
            return false;
        }
        if (false == user1.getHome().equals(user2.getHome())) {
            return false;
        }
        if (false == user1.getName().equals(user2.getName())) {
            return false;
        }
        return user1.getPassword().equals(user2.getPassword());
    }

    public static boolean areItemsEqual(Tag tag1, Tag tag2) {
        if (tag1.getId() != tag2.getId()) {
            return false;
        }
        return tag1.getName().equals(tag2.getName());
    }

    public static boolean areItemsEqual(File file1, File file2) {
        if (file1.getId() != file2.getId()) {
            return false;
        }
        if (file1.getCheckSum() != file2.getCheckSum()) {
            return false;
        }
        if (false == file1.getLastChange().equals(file2.getLastChange())) {
            return false;
        }
        if (file1.getOwnerId() != file2.getOwnerId()) {
            return false;
        }
        if (false == file1.getParentPath().equals(file2.getParentPath())) {
            return false;
        }
        if (false == file1.getPath().equals(file2.getPath())) {
            return false;
        }
        if (file1.getType() != file2.getType()) {
            return false;
        }
        return file1.getVersion() == file2.getVersion();
    }

    public static boolean areItemsEqual(Rule rule1, Rule rule2) {
        if (rule1.getId() != rule2.getId()) {
            return false;
        }
        if (false == rule1.getBody().equals(rule2.getBody())) {
            return false;
        }
        if (false == areBothNullableItemsEqual(rule1.getHoldsSince(), rule2.getHoldsSince())) {
            return false;
        }
        if (false == areBothNullableItemsEqual(rule1.getHoldsUntil(), rule2.getHoldsUntil())) {
            return false;
        }
        if (rule1.getType() != rule2.getType()) {
            return false;
        }
        if (false == areBothNullableItemsEqual(rule1.getFilePath(), rule2.getFilePath()))  {
            return false;
        }
        return rule1.getOwnerId() == rule2.getOwnerId();
    }

    public static <T> boolean areBothNullableItemsEqual(T item1, T item2) {
        return ((null == item1) && (null == item2)) || item1.equals(item2);
    }

    public static Date makeDateFromLocalDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
