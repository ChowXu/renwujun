package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.handler.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: wenda
 * Author: Chow xi
 * Email: zhouxu_1994@163.com
 * Time: 17/2/17 下午11:25
 */
@Controller
public class FollowController {

    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/followUser", method = RequestMethod.POST)
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        //添加一个 关注 事件  视频9 38:00
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).
                setActorId(hostHolder.getUser().getId()).
                setEntityType(EntityType.ENTITY_USER).
                setEntityId(userId));


        //返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(value = "/unfollowUser", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        //添加一个 关注 事件  视频9 38:00

        //这里有点问题
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }


    @RequestMapping(value = "/followQuestion", method = RequestMethod.POST)
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        User hostHolderUser = hostHolder.getUser();
        if (hostHolderUser == null) {
            return WendaUtil.getJSONString(999);
        }

        //检查questionId
        Question question = questionService.selectById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "question is not exists");
        }

        boolean ret = followService.follow(hostHolderUser.getId(), EntityType.ENTITY_QUESTION, questionId);
        //添加一个 关注 事件  视频9 38:00


        //添加关注着的基本信息
        Map<String, Object> infos = new HashMap<>();
        infos.put("headUrl", hostHolderUser.getHeadUrl());
        infos.put("name", hostHolderUser.getName());
        infos.put("id", hostHolderUser.getId());
        infos.put("count", followService.getFolloweeCount(hostHolderUser.getId(), EntityType.ENTITY_QUESTION));

        //返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, infos);
    }

    @RequestMapping(value = "/unfollowQuestion", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        User hostHolderUser = hostHolder.getUser();
        if (hostHolderUser == null) {
            return WendaUtil.getJSONString(999);
        }

        //检查questionId
        Question question = questionService.selectById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "question is not exists");
        }

        boolean ret = followService.unfollow(hostHolderUser.getId(), EntityType.ENTITY_QUESTION, questionId);
        //添加一个 关注 事件  视频9 38:00

        //添加关注着的基本信息
        Map<String, Object> infos = new HashMap<>();
        infos.put("headUrl", hostHolderUser.getHeadUrl());
        infos.put("name", hostHolderUser.getName());
        infos.put("id", hostHolderUser.getId());
        infos.put("count", followService.getFolloweeCount(hostHolderUser.getId(), EntityType.ENTITY_QUESTION));

        //返回关注人数
        return WendaUtil.getJSONString(ret ? 0 : 1, infos);
    }

    //  关注的两个页面
    //  当前用户的关注者
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        //这里的想法???
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUserInfos(hostHolder.getUser().getId(), followers));
        } else {
            model.addAttribute("followers", getUserInfos(0, followers));
        }

        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    // 当前用户关注的内容
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followees = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        //这里的想法???
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUserInfos(hostHolder.getUser().getId(), followees));
        } else {
            model.addAttribute("followees", getUserInfos(0, followees));
        }

        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }


    //从数据库中捞出数据

    // 我关注了哪些人


    private List<ViewObject> getUserInfos(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (int uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            //此人评论了多少条
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            //有多少人关注了他
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            //这个人关注了多少人
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                // localUserId 是否关注了 uid
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
