package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.handler.EventType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/18 下午10:25
 */
@Component
public class LikeHandler implements EventHandler {
    @Override
    public void doHandle(EventModel model) {
        System.out.println("like");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }


}
