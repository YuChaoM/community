package com.yuchao.community.controller;

import com.yuchao.community.entity.Comment;
import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Event;
import com.yuchao.community.entity.User;
import com.yuchao.community.event.EventProducer;
import com.yuchao.community.service.CommentService;
import com.yuchao.community.service.DiscussPostService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒙宇潮
 * @create 2022-10-11  11:38
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private EventProducer eventProducer;

//    discussPostId可能会有一个异常，可能是非数
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId")Integer discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);//其他属性在前端用隐藏域传
        commentService.addComment(comment);

        //触发评论事件
        Map<String, Object> map = new HashMap<>();
        map.put("discussPostId", discussPostId);
        Event event = Event.builder()
                .topic(TOPIC_COMMENT)
                .userId(user.getId())
                .entityType(comment.getEntityType())
                .entityId(comment.getEntityId())
                .data(map)
                .build();
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());//给作者发通知
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
//            event.setEntityUserId(comment.getTargetId());//不行，回复楼主时id为0
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
