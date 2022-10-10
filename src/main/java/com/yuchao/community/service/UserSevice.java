package com.yuchao.community.service;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.yuchao.community.entity.LoginTicket;
import com.yuchao.community.entity.User;
import com.yuchao.community.mapper.LoginTicketMapper;
import com.yuchao.community.mapper.UserMapper;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author 蒙宇潮
 * @create 2022-09-24  11:13
 */
@Service
public class UserSevice implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Resource
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    public Map<String, Object> register(User user) {
        HashMap<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "邮箱已被注册");
            return map;
        }
        //开始注册
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);//0普通用户 1超级管理员 0版主
        user.setStatus(0);//0未激活，1以激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setAvatarUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //http://localhost:8080/community/activation/userId/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    public int activation(Integer userId, String code) {
        if (userId == null || StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        User user = userMapper.selectById(userId);
        if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        //查数据库，验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "用户不存在!");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "账号为未激活!");
        }
        password = CommunityUtil.md5(password + user.getSalt());
        if (!password.equals(user.getPassword())) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);//0-有效 1-无效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000l));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }


    public void logout(String ticket) {
        loginTicketMapper.updateLoginTicket(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateAvatarUrl(Integer userId, String avatarUrl) {
        return userMapper.updateAvatarUrl(userId, avatarUrl);
    }

    public int updatePassword(Integer userId,String password,String salt) {
        return userMapper.updatePassword(userId, password,salt);
    }

    public void sendEmailCode(String email, String code) {
        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("code", code);
        String process = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email,"【忘记密码验证】",process);
    }

    public Map<String,Object> forgetPassword(String email,String newPassword) {
        HashMap<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "邮箱不存在");
            return map;
        }
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        newPassword = CommunityUtil.md5(newPassword + salt);
        updatePassword(user.getId(), newPassword, salt);
        return map;
    }


}
