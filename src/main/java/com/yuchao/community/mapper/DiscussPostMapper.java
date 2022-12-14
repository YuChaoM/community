package com.yuchao.community.mapper;

import com.yuchao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-09-21  17:11
 */

public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPost(@Param("userId") Integer userId, @Param("offset") Integer offset,
                                        @Param("limit") Integer limit,@Param("orderMode") int orderMode);

    int selectDiscussPostTotal(@Param("userId") Integer userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(Integer id);

    int updateCommentCountById( @Param("entityId") Integer entityId);

    int updateType(@Param("id") int id, @Param("type") int type);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    void updateScore(@Param("id")int postId,@Param("score") double score);
}
