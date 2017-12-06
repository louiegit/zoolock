package com.louiegit.zoolock;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 14:49
 */
public enum  ZLockType {

    PRIVATE("private"),
    PUBLIC("public");

    ZLockType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
