package com.yuchao.community.mapper;

import com.yuchao.community.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-10-10  14:49
 */
public interface CommentMapper {

    List<Comment> selectCommentByEntity(@Param("entityId") Integer entityId, @Param("entityType") Integer entityType,
                                        @Param("offset") int offset,@Param("limit") int limit);

    int selectCountByEntity(@Param("entityId") Integer entityId, @Param("entityType") Integer entityType);

    int insertComment(Comment comment);

    Comment selectCommentById(Integer entityId);

}
