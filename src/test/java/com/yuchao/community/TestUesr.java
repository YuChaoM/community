package com.yuchao.community;

import com.yuchao.community.entity.User;
import com.yuchao.community.mapper.UserMapper;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒙宇潮
 * @create 2022-09-20  11:09
 */

@SpringBootTest
public class TestUesr {

    @Autowired
    private UserMapper userMapper;
    @Resource
    private UserSevice userSevice;

    @Test
    public void testSelect() {
        userSevice.findUserById(101);
        User user = userMapper.selectById(101);
        System.out.println(user);
        System.out.println(userMapper.selectByName("liubei"));
        System.out.println(userMapper.selectByEmail("nowcoder101@sina.com"));
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setUsername("yuchao");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("123@zbc.com");
        user.setStatus(1);
        user.setType(1);
        user.setAvatarUrl("http://abc.jpg");
        user.setCreateTime(new Date());
        Integer i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate() {
//        System.out.println(userMapper.updateStatus(151, 1));
//        System.out.println(userMapper.updateAvatarUrl(151,"http://yuchao/abc.jpg"));
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        String password = CommunityUtil.md5("123456" + salt);
        System.out.println(userMapper.updatePassword(158, password, salt));
    }


}
