package com.nowcoder.async;

import com.nowcoder.async.handler.EventType;

import java.util.List;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/18 下午9:50
 */

public interface EventHandler {

    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();

}
