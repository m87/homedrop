package org.homedrop.testUtils;

import org.homedrop.core.model.User;
import org.homedrop.core.utils.ModelHelpers;

import java.util.List;

import static org.junit.Assert.*;

public class TestHelpers {

    public static class HelperException extends Exception {
        public HelperException() { super(); }
    }

    public static final String MainConfigFilePath = "test-env/homedrop.cfg";

    public static <T> void assertListContainsItemEqual(List<T> list, T itemSearched) {
        boolean foundEqual = false;
        for (T item : list) {
            foundEqual |= areItemsEqual(itemSearched, item);
        }
        assertTrue(foundEqual);
    }

    public static <T> boolean areItemsEqual(T itemSearched, T itemWithinList) {
        if (itemSearched instanceof User) {
            return ModelHelpers.areItemsEqual((User)itemSearched, (User)itemWithinList);
        }
        return false;
    }
}
