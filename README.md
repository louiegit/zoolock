# ZooLock-prod
distributed lock based on curator framework 

#### Usage

ZLockInfo info = new ZLockInfo("louie",ZLockType.PRIVATE);

ZLock zLock = new CuratorLock(info);

zLock.lock();

Assert.assertTrue(zLock.isLock());

zLock.unlock();

Assert.assertFalse(zLock.isLock());
