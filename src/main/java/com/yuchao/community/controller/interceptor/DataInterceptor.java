package com.yuchao.community.controller.interceptor;

import com.yuchao.community.entity.User;
import com.yuchao.community.service.DataService;
import com.yuchao.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 蒙宇潮
 * @create 2022-11-10  18:02
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Resource
    private DataService dataService;
    @Resource
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //记录uv
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
