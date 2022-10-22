package com.yuchao.community.controller;

import com.yuchao.community.entity.User;
import com.yuchao.community.service.FollowService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author 蒙宇潮
 * @create 2022-10-22  19:32
 */

@Controller
public class FollowController implements CommunityConstant {

    @Resource
    private FollowService followService;
    @Resource
    private HostHolder hostHolder;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(entityId, entityType, user.getId());
        return CommunityUtil.getJSONString(SUCCESS, "已关注!");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(entityId, entityType, user.getId());
        return CommunityUtil.getJSONString(SUCCESS, "一以取消关注!");
    }


}
