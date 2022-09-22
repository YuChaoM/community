package com.yuchao.community.controller;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.mapper.DiscussPostMapper;
import com.yuchao.community.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HomeController {

    @Autowired
    private UserMapper userMapper;
    @Resource
    private DiscussPostMapper discussPostMapper;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        //model 和page springMVC会自动实例化并传进来
        page.setRows(discussPostMapper.selectDiscussPostTotal(0));
        page.setPath("/index");
        ArrayList<Map<String, Object>> discussPosts = new ArrayList<>();
        List<DiscussPost> list = discussPostMapper.selectDiscussPost(0, page.getOffset(), page.getLimit());
        for (DiscussPost post : list) {
            HashMap<String, Object> map = new HashMap<>();
            User user = userMapper.selectById(post.getUserId());
            map.put("user", user);
            map.put("post", post);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }
}