package com.louiegit.zoolock;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 15:20
 */
public class ZLockTest {

    @Test
    public void testLock(){
        ZLockInfo info = new ZLockInfo("louie",ZLockType.PRIVATE);
        ZLock zLock = new CuratorLock(info);
        zLock.lock();
        Assert.assertTrue(zLock.isLock());
        zLock.unlock();
        Assert.assertFalse(zLock.isLock());
    }


}
