package com.yuchao.community.controller;

import com.yuchao.community.entity.Comment;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.CommentService;
import com.yuchao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-10-11  11:38
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;

//    discussPostId可能会有一个异常，可能是非数
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId")Integer discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);//其他属性在前端用隐藏域传
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
