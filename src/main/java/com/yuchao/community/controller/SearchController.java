package com.yuchao.community.controller;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.ElasticsearchService;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityConstant;
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
 * @create 2022-11-04  11:12
 */
@Controller
public class SearchController implements CommunityConstant {

    @Resource
    private ElasticsearchService elasticsearchService;
    @Resource
    private UserSevice userSevice;
    @Resource
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, Page page, Model model) {
        Map<String, Object> search = elasticsearchService.search(keyword, page.getCurrent() - 1, page.getLimit());
        ArrayList<Map<String, Object>> discussPosts = new ArrayList<>();
        if (search != null) {
            List<DiscussPost> searchResult = (List<DiscussPost>) search.get("page");
            int rows = ((Long)search.get("rows")).intValue();
            //分页信息
            page.setPath("/search/?keyword=" + keyword);
            page.setRows(rows);

            //聚合数据
            for (DiscussPost discussPost : searchResult) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("discussPost", discussPost);
                //作者
                User user = userSevice.findUserById(discussPost.getUserId());
                map.put("user", user);
                //点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));
                discussPosts.add(map);
            }
            model.addAttribute("discussPosts", discussPosts);
            model.addAttribute("keyword", keyword);

        }
        return "site/search";
    }
}
