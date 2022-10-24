package com.yuchao.community.controller;

import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.FollowService;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @Resource
    private UserSevice userSevice;

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

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userSevice.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        page.setPath("/followees/" + userId);
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        User loginUser = hostHolder.getUser();
        if (followees != null) {
            for (Map<String, Object> followee : followees) {
                User u = (User) followee.get("user");
                boolean hasFollowed = loginUser == null ? false : followService.hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, u.getId());
                followee.put("hasFollowed", hasFollowed);
            }
        }
        model.addAttribute("followees", followees);
        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userSevice.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        page.setPath("/followers/" + userId);
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        User loginUser = hostHolder.getUser();
        if (followers != null) {
            for (Map<String, Object> followee : followers) {
                User u = (User) followee.get("user");
                boolean hasFollowed = loginUser == null ? false : followService.hasFollowed(loginUser.getId(), ENTITY_TYPE_USER, u.getId());
                followee.put("hasFollowed", hasFollowed);
            }
        }
        model.addAttribute("followers", followers);
        return "/site/follower";
    }

}
