# Lock 
distributed lock based on curator framework 

#### CuratorLock Usage
```java
        LockInfo info = new LockInfo("louie");
        Lock cLock = new CuratorLock(info);
        cLock.lock();
        Assert.assertTrue(cLock.isLock());
        cLock.unlock();
        Assert.assertFalse(cLock.isLock());
```

#### ZooLock Usage
```java
        LockInfo lockInfo = new LockInfo("louie");
        Lock zLock = new ZooLock(lockInfo);
        zLock.lock();
        zLock.lock();
        Assert.assertTrue(zLock.isLock());
        zLock.unlock();
        Assert.assertFalse(zLock.isLock());
```
