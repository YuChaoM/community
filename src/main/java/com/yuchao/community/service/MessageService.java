package com.yuchao.community.service;

import com.yuchao.community.entity.Message;
import com.yuchao.community.mapper.MessageMapper;
import com.yuchao.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-10-13  0:08
 */
@Service
public class MessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }


    public int updateStatus(List<Integer> ids, int status) {
        return messageMapper.updateStatus(ids, status);
    }

    public int deleteMessageById(int id) {
        return messageMapper.deleteMessageById(id);
    }

}
