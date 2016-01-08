package org.homedrop.core.utils;

import org.homedrop.core.model.User;

public class ModelHelpers {
    public static void setUserFields(User user, String name, String password, String home) {
        user.setName(name);
        user.setPassword(password);
        user.setHome(home);
    }

    public static boolean areFieldsEqual(User user1, User user2) {
        if (user1.getId() != user2.getId()) {
            return false;
        }
        if (false == user1.getHome().equals(user2.getHome())) {
            return false;
        }
        if (false == user1.getName().equals(user2.getName())) {
            return false;
        }
        if (false == user1.getPassword().equals(user2.getPassword())) {
            return false;
        }
        return true;
    }
}
