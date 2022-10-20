package com.yuchao.community.controller;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.mapper.DiscussPostMapper;
import com.yuchao.community.mapper.UserMapper;
import com.yuchao.community.service.DiscussPostService;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.RedisKeyUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒙宇潮
 * @create 2022-09-21  17:39
 */
@Controller
public class HomeController implements CommunityConstant {

    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private UserSevice userSevice;
    @Autowired
    private LikeService likeService;


    @GetMapping({"/index","/"})
    public String getIndexPage(Model model, Page page) {
        //model 和page springMVC会自动实例化并传进来
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        ArrayList<Map<String, Object>> discussPosts = new ArrayList<>();
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        for (DiscussPost post : list) {
            HashMap<String, Object> map = new HashMap<>();
            User user = userSevice.findUserById(post.getUserId());
            map.put("user", user);
            map.put("post", post);
            Long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
            map.put("likeCount", likeCount);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error/500";
    }
}
