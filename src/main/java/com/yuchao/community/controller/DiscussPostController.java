package com.yuchao.community.controller;

import com.yuchao.community.entity.*;
import com.yuchao.community.event.EventProducer;
import com.yuchao.community.service.CommentService;
import com.yuchao.community.service.DiscussPostService;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.service.UserService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import com.yuchao.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    private UserService userSevice;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(FORBIDDEN, "还没有登陆哦");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发发帖事件 存到es
        Event event = Event.builder()
                .topic(TOPIC_PUBLISH)
                .userId(user.getId())
                .entityType(ENTITY_TYPE_POST)
                .entityId(post.getId())
                .build();
        eventProducer.fireEvent(event);

        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,post.getId());

        //后面再对错误统一处理
        return CommunityUtil.getJSONString(SUCCESS, "发布成功!");
    }

    @GetMapping("/detail/{id}")
    public String getDiscussPost(@PathVariable("id") Integer id, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(id);
        if (post.getStatus() == 2) {
            throw new RuntimeException();
        }
        User user = userSevice.findUserById(post.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);//是否需要dto呢？
        //点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);
        model.addAttribute("likeCount", likeCount);

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
                commentVo.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));
                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);
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
                        replyVo.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);

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

    //置顶/取消置顶
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int postId) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        int type = discussPost.getType() ^ 1;//1^1=0,0^1=1
        discussPostService.updateType(postId, type);

        //触发发帖事件 存到es
        Event event = Event.builder()
                .topic(TOPIC_PUBLISH)
                .userId(hostHolder.getUser().getId())
                .entityType(ENTITY_TYPE_POST)
                .entityId(postId)
                .build();
        eventProducer.fireEvent(event);

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        return CommunityUtil.getJSONString(SUCCESS, null,map);
    }

    //加精，取消加精
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int postId) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        int status = discussPost.getStatus() ^ 1;

        discussPostService.updateStatus(postId, status);

        Event event = Event.builder()
                .topic(TOPIC_PUBLISH)
                .userId(hostHolder.getUser().getId())
                .entityType(ENTITY_TYPE_POST)
                .entityId(postId)
                .build();
        eventProducer.fireEvent(event);
        HashMap<String, Object> map = new HashMap<>();
        map.put("status",status);

        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, postId);

        return CommunityUtil.getJSONString(SUCCESS, null, map);
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deletePost(int postId) {
        discussPostService.updateStatus(postId, 2);

        //触发删贴事件
        Event event = Event.builder()
                .topic(TOPIC_DELETE)
                .userId(hostHolder.getUser().getId())
                .entityType(ENTITY_TYPE_POST)
                .entityId(postId)
                .build();
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(SUCCESS);
    }

}
