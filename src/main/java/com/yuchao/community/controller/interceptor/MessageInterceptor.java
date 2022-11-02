package com.yuchao.community.controller.interceptor;

import com.yuchao.community.entity.User;
import com.yuchao.community.service.MessageService;
import com.yuchao.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 蒙宇潮
 * @create 2022-10-31  20:25
 */

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Resource
    private HostHolder hostHolder;
    @Resource
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int id = user.getId();
            int letterUnreadCount = messageService.findLetterUnreadCount(id, null);
            int noticesUnreadCount = messageService.findNoticesUnreadCount(id, null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticesUnreadCount);
        }
    }


}
