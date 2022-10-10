package com.yuchao.community.util;

import com.yuchao.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author 蒙宇潮
 * @create 2022-09-27  0:28
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        //用完就remove防止内存泄漏
        users.remove();
    }
}
