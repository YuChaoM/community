package com.yuchao.community.controller.interceptor;

import com.yuchao.community.anntoation.LoginReuquired;
import com.yuchao.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 蒙宇潮
 * @create 2022-09-28  19:03
 */

@Deprecated
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginReuquired loginReuquired = handlerMethod.getMethodAnnotation(LoginReuquired.class);
            if (loginReuquired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
