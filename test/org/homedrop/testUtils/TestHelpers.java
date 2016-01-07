package org.homedrop.testUtils;

import org.homedrop.core.model.User;

import static org.junit.Assert.*;

public class TestHelpers {

    public static class HelperException extends Exception {
        public HelperException() { super(); }
    }

    public static final String MainConfigFilePath = "test-env/homedrop.cfg";

    public static void assertUsersAreEqual(User expected, User actual) {
        String expectedName =  expected.getName();
        String expectedPassword =  expected.getPassword();
        String expectedHome =  expected.getHome();
        long expectedId = expected.getId();
        assertEquals(expectedName, actual.getName());
        assertEquals(expectedPassword, actual.getPassword());
        assertEquals(expectedHome, actual.getHome());
        assertEquals(expectedId, actual.getId());
    }
}
