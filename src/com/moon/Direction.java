package com.moon;

/**
 * 方向枚举类
 * Created by 12919 on 2018/5/8.
 */
public enum Direction {
    U("方向上"),
    D("方向下"),
    L("方向左"),
    R("方向右");

    private String desc;

    private Direction(String desc) {
        this.desc = desc;
    }
}
