package com.yuchao.community.mapper;

import com.yuchao.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-09-21  17:11
 */

public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPost(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    int selectDiscussPostTotal(@Param("userId") Integer userId);

}
