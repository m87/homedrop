package org.homedrop.core.utils;

import org.homedrop.core.model.User;

public class ModelHelpers {
    public static void setUserFields(User user, String name, String password, String home) {
        user.setName(name);
        user.setPassword(password);
        user.setHome(home);
    }

}
