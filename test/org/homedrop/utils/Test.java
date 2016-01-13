package org.homedrop.utils;

import org.homedrop.core.utils.DBHelper;
import org.junit.Assert;

public class Test {

    @org.junit.Test
    public void formatTest(){
        Assert.assertEquals(DBHelper.formatPath("/a/b/"),"/a/b");
        Assert.assertEquals(DBHelper.formatPath("/a/b/."),"/a/b");
    }
}
