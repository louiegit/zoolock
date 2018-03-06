package com.louiegit.zoolock.zookeeper;

import com.louiegit.zoolock.lock.Lock;
import com.louiegit.zoolock.lock.LockInfo;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.WatcherEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

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
            //初始化zkClient客户端
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
            //检查root路径是否存在
            Stat stat0 = zkClient.exists(LockInfo.ROOT, false);
            if (stat0 == null) {
                zkClient.create(LockInfo.ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //检查锁的路径是否存在
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
        return lock(0);
    }

    @Override
    public boolean lock(final long timeout) {
        try {
            //新建一个临时序列化节点
            currentNode = zkClient.create(lockInfo.getPath() + DEFAULT_COMP_NODE, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            String[] nodeStr = currentNode.split("/");
            //获取当前线程创造的临时节点
            String ownNode = nodeStr[nodeStr.length - 1];
            this.nodePath = ownNode;
            //循环争夺锁
            while (true) {
                //将该锁路径下的所有节点获取出
                List<String> allSubNode = zkClient.getChildren(lockInfo.getPath(), false);
                Collections.sort(allSubNode);
                String minNode = allSubNode.get(0);
                //将当前线程创造节点与最小节点比较
                if (minNode.equals(nodePath)) {
                    return true;
                }
                //如果不是最小节点,阻塞线程获取锁
                int ownIndex = allSubNode.indexOf(ownNode);
                //对比自己小的节点设置watcher,如果此节点被删除,countDown一次,继续循环判断
                while (true) {
                    setWatcher(allSubNode, ownIndex, timeout);
                }
            }
        } catch (Exception e) {

        }
        return true;
    }

    @Override
    public boolean unlock() {
        try {
            //删除该节点
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
            //检查当前线程是否获取获取锁
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

    /**
     * 设置watcher
     *
     * @param allSubNode
     * @param ownIndex
     */
    private void setWatcher(List<String> allSubNode, int ownIndex, final long timeout) {
        try {
            zkClient.exists(lockInfo.getPath() + "/" + allSubNode.get(ownIndex - 1), new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                    if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                        countDownLatch.countDown();
                    }
                    try {
                        //阻塞当前线程
                        if (timeout == 0) {
                            countDownLatch.await();
                        } else {
                            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {


    }

}
