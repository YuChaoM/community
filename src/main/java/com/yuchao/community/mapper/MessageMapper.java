package com.yuchao.community.mapper;

import com.yuchao.community.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-10-12  23:07
 */

public interface MessageMapper {

    //针对每一个会话，返回最新一条
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    //会话列表的条数
    int selectConversationCount(int userId);

    List<Message> selectLetters(@Param("conversationId") String conversationId,
                                @Param("offset") int offset, @Param("limit")int limit);

    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectLetterUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

}

