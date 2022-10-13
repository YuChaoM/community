package com.yuchao.community.service;

import com.yuchao.community.entity.Comment;
import com.yuchao.community.mapper.CommentMapper;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-10-10  15:04
 */
@Service
public class CommentService implements CommunityConstant {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;
    @Resource
    private DiscussPostService discussPostService;

    public List<Comment> findCommentByEntity(Integer entityId, Integer entityType, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityId, entityType, offset, limit);
    }

    public int findCountByEntity(Integer entityId, Integer entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED ,propagation = Propagation.REQUIRED)
    public synchronized int addComment(Comment comment) {
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);
        //对帖子的评论才更新
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            discussPostService.updateCommentCountById(comment.getEntityId());
        }
        return rows;
    }
}
