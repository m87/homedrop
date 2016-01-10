package org.homedrop.core.utils;

import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.model.User;
import org.homedrop.thirdParty.db.sqliteModels.UserEntity;

import java.sql.Date;

public class ModelHelpers {
    public static void setUserFields(User user, String name, String password, String home) {
        user.setName(name);
        user.setPassword(password);
        user.setHome(home);
    }

    public static void setFileFields(File file, String name, long checkSum, Date lastChange,
                                     User owner, String path, long version) {
        file.setName(name);
        file.setCheckSum(checkSum);
        file.setLastChange(lastChange);
        file.setOwner((UserEntity) owner);
        file.setPath(path);
        file.setVersion(version);
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
        if (false == file1.getName().equals(file2.getName())) {
            return false;
        }
        if (file1.getOwnerId() != file2.getOwnerId()) {
            return false;
        }
        if (false == file1.getPath().equals(file2.getPath())) {
            return false;
        }
        return file1.getVersion() == file2.getVersion();
    }

}
