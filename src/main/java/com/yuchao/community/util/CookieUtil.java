package com.yuchao.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 蒙宇潮
 * @create 2022-09-27  0:21
 */
public class CookieUtil {

    public static String getCookieValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null){//增加非空判断
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
