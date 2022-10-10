package com.yuchao.community.controller.interceptor;


import com.yuchao.community.entity.LoginTicket;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CookieUtil;
import com.yuchao.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-09-27  0:18
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserSevice userSevice;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getCookieValue(request, "ticket");
        if (ticket != null) {
            //检查凭证是否有效
            LoginTicket loginTicket = userSevice.findLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                User user = userSevice.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //执行controller方法之后
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    //请求结束，DispatcherServlet渲染视图之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
