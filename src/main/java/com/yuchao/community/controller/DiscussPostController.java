package com.yuchao.community.controller;

import com.yuchao.community.entity.Comment;
import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.CommentService;
import com.yuchao.community.service.DiscussPostService;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author 蒙宇潮
 * @create 2022-10-08  10:32
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserSevice userSevice;
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "还没有登陆哦");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //后面再对错误统一处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @GetMapping("/detail/{id}")
    public String getDiscussPost(@PathVariable("id") Integer id, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(id);
        User user = userSevice.findUserById(post.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);//是否需要dto呢？

        //设置分页数据
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(post.getCommentCount());
        //查询帖子评论
        List<Comment> commentList = commentService.findCommentByEntity(post.getId(), ENTITY_TYPE_POST, page.getOffset(), page.getLimit());
        //评论VO列表
        ArrayList<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                //对帖子评论的VO
                HashMap<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userSevice.findUserById(comment.getUserId()));
                //对该条评论的回复列表
                ArrayList<Map<String, Object>> replyVoList = new ArrayList<>();
                List<Comment> replyList = commentService.findCommentByEntity(comment.getId(), ENTITY_TYPE_COMMENT, 0, Integer.MAX_VALUE);
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        //回复的Vo
                        HashMap<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        //发回复的人
                        replyVo.put("user", userSevice.findUserById(reply.getUserId()));
                        //回复谁
                        User target = reply.getTargetId() == 0 ? null : userSevice.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                int replyCount = commentService.findCountByEntity(comment.getId(), ENTITY_TYPE_COMMENT);
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
