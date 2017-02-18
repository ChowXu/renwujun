package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/16 下午5:22
 * 关键是做两件事情
 */
@Service
public class FollowService {

    @Autowired
    JedisAdapter jedisAdapter;


    //当前用户对某个实体关注
    public boolean follow(int userId, int entityType, int entityId) {

        // fans set
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        // 关注对象的set
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝 增加当前当前用户
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        // 当前用户对这类实体 关注+ 1
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }


    //当前用户取消对某个实体的关注
    public boolean unfollow(int userId, int entityType, int entityId) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //redis 事务
        Transaction tx = jedisAdapter.multi(jedis);
        // 实体的粉丝集 删除当前的用户
        tx.zrem(followerKey, String.valueOf(userId));
        // 用户的对这类的实体的关注集  删除对当前实体的关注
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long) ret.get(1) > 0;
    }

    //获取这类实体的所有粉丝
    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdFromSet(jedisAdapter.zrange(followerKey, 0, count));
    }

    //分页使用
    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdFromSet(jedisAdapter.zrange(followerKey, offset, count));
    }

    //获取用户对这类实体的所有关注
    public List<Integer> getFollowees(int userId, int entityType, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    //分页使用
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
    }


    //获取当前用户对这类实体的关注数
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    //获取这类实体的粉丝数目
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    //判断用户是否关注了某个实体
    public boolean isFollower(int userid, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userid)) != null;
    }

    private List<Integer> getIdFromSet(Set<String> idSet) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idSet) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }


}
