package com.yuchao.community.service;

import com.yuchao.community.entity.Comment;
import com.yuchao.community.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-10-10  15:04
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> findCommentByEntity(Integer entityId, Integer entityType, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityId, entityType, offset, limit);
    }

    public int findCountByEntity(Integer entityId, Integer entityType) {
        return commentMapper.selectCountByEntity(entityId, entityType);
    }
}
