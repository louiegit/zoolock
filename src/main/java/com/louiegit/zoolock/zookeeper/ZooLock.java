package com.louiegit.zoolock.zookeeper;

import com.louiegit.zoolock.common.Lock;
import com.louiegit.zoolock.common.LockInfo;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author tianxiang.luo
 * @version 2017/12/7 09:49
 */
public class ZooLock implements Lock {

    private LockInfo lockInfo;

    private String currentNode = null;

    private static final String DEFAULT_COMP_NODE = "/competition";

    private ZooKeeper zkClient = null;

    private String nodePath = null;

    public ZooLock(LockInfo lockInfo) {
        this.lockInfo = lockInfo;
        init(lockInfo);
    }

    private void init(LockInfo lockInfo) {
        try {
            zkClient = new ZooKeeper(LockInfo.DEFAULT_SOCKET, Integer.MAX_VALUE, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("zookeeper init success...");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Stat stat0 = zkClient.exists(LockInfo.ROOT, false);
            if (stat0 == null) {
                zkClient.create(LockInfo.ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            Stat stat = zkClient.exists(lockInfo.getPath(), false);
            if (stat == null) {
                zkClient.create(lockInfo.getPath(), new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean lock() {
        try {
            currentNode = zkClient.create(lockInfo.getPath() + DEFAULT_COMP_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            String[] nodeStr = currentNode.split("/");
            String ownNode = nodeStr[nodeStr.length - 1];
            this.nodePath = ownNode;
            while (true) {
                List<String> allSubNode = zkClient.getChildren(lockInfo.getPath(), false);
                Collections.sort(allSubNode);
                String minNode = allSubNode.get(0);
                if (minNode.equals(nodePath)) {
                    return true;
                }
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                int ownIndex = allSubNode.indexOf(ownNode);
                zkClient.exists(lockInfo.getPath() + "/" + allSubNode.get(ownIndex - 1), new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        countDownLatch.countDown();
                    }
                });
                countDownLatch.await();
            }
        } catch (Exception e) {

        }
        return true;
    }

    @Override
    public boolean lock(long timeout) {
        return false;
    }

    @Override
    public boolean unlock() {
        try {
            zkClient.delete(lockInfo.getPath() + "/" + nodePath, 0);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isLock() {
        try {
            Stat stat = zkClient.exists(lockInfo.getPath() + "/" + nodePath, false);
            if (stat != null) {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

}
