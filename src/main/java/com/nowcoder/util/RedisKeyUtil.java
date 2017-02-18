package com.nowcoder.util;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/16 下午5:24
 */
public class RedisKeyUtil {

    //fans
    private static String SPLIT = ":";

    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    // 关注A的有哪些
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // A关注了哪些人
    private static String BIZ_FOLLOWEE = "FOLLOWEE";

    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }


    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getBizEventqueue() {
        return BIZ_EVENTQUEUE;
    }


}
