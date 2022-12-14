package com.yuchao.community.controller;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.DiscussPostService;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.service.UserService;
import com.yuchao.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private UserService userSevice;
    @Autowired
    private LikeService likeService;


    @GetMapping({"/index","/"})
    public String getIndexPage(Model model, Page page, @RequestParam(name = "orderMode",defaultValue = "0")int orderMode) {
        //model 和page springMVC会自动实例化并传进来
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        ArrayList<Map<String, Object>> discussPosts = new ArrayList<>();
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);
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
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error/500";
    }

    @GetMapping("/denied")
    public String getDinedPage() {
        return "error/404";
    }
}
