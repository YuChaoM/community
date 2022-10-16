package com.yuchao.community.controller;

import com.yuchao.community.entity.Message;
import com.yuchao.community.entity.Page;
import com.yuchao.community.entity.User;
import com.yuchao.community.mapper.MessageMapper;
import com.yuchao.community.service.MessageService;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 蒙宇潮
 * @create 2022-10-13  0:16
 */
@Controller
@RequestMapping("/message")
public class MessageController implements CommunityConstant {

    @Resource
    private MessageService messageService;
    @Resource
    private HostHolder hostHolder;
    @Resource
    private UserSevice userSevice;

    @GetMapping("/list")
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setPath("/message/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //获取对方的信息
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userSevice.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //总的未读消息
        model.addAttribute("letterUnreadCount", messageService.findLetterUnreadCount(user.getId(), null));
        return "site/letter";
    }

    @GetMapping("/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        User user = hostHolder.getUser();
        page.setPath("/detail/" + conversationId);
        page.setLimit(5);
        page.setRows(messageService.findLetterCount(conversationId));

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userSevice.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.updateStatus(ids, 1);
        }
        return "/site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList) {
        ArrayList<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId) {
        String[] s = conversationId.split("_");
        User user = hostHolder.getUser();
        int id0 = Integer.parseInt(s[0]);
        int id1 = Integer.parseInt(s[1]);
        if (user.getId() == id0) {
            return userSevice.findUserById(id1);
        } else {
            return userSevice.findUserById(id0);
        }

    }

    @PostMapping("/add")
    @ResponseBody
    public String addMessage(String toName, String content) {
        User fromUser = hostHolder.getUser();
        User toUser = userSevice.findUserByName(toName);
        if (toUser == null) {
            return CommunityUtil.getJSONString(ACCEPTED, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());
        message.setContent(content);
        message.setConversationId(getConversationId(fromUser, toUser));
        message.setCreateTime(new Date());
        message.setStatus(0);
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(SUCCESS);
    }

    private String getConversationId(User fromUser, User toUser) {
        if (fromUser.getId() < toUser.getId()) {
            return fromUser.getId() + "_" + toUser.getId();
        } else {
            return toUser.getId() + "_" + fromUser.getId();
        }
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public String deleteMessageById(int id) {
        messageService.deleteMessageById(id);
        return CommunityUtil.getJSONString(SUCCESS);
    }


}
