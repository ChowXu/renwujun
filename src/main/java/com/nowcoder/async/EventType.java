package com.nowcoder.async.handler;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/18 下午8:34
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    ADD_QUESTION(6);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
