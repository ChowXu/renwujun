package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/18 下午10:04
 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String key = RedisKeyUtil.getBizEventqueue();
            String value = JSONObject.toJSONString(eventModel);
            jedisAdapter.lpush(key, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
