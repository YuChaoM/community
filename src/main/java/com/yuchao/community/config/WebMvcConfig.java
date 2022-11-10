package com.yuchao.community.config;

import com.yuchao.community.controller.interceptor.LoginRequiredInterceptor;
import com.yuchao.community.controller.interceptor.LoginTicketInterceptor;
import com.yuchao.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 蒙宇潮
 * @create 2022-09-27  0:40
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    LoginRequiredInterceptor loginRequiredInterceptor;

    @Resource
    MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //执行顺序和注册顺序有关
        //默认是拦截全部/**
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
