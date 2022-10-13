package com.yuchao.community;

import com.yuchao.community.entity.Message;
import com.yuchao.community.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-10-13  9:01
 */
@SpringBootTest
public class MessageTest {

    @Resource
    private MessageMapper messageMapper;
    @Test
    public void messageTest() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        messages.forEach(System.out::println);
        System.out.println(messageMapper.selectConversationCount(111));
        List<Message> letters = messageMapper.selectLetters("111_112", 0, 100);
        letters.forEach(System.out::println);
        System.out.println(messageMapper.selectLetterUnreadCount(111, "111_112"));
        System.out.println(messageMapper.selectLetterUnreadCount(111,null));
        System.out.println(messageMapper.selectLetterCount("111_112"));
    }
}
