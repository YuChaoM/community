package com.yuchao.community.controller;

import com.fasterxml.jackson.databind.Module;
import com.yuchao.community.anntoation.LoginReuquired;
import com.yuchao.community.entity.User;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.service.UserSevice;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import com.yuchao.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Map;

/**
 * @author 蒙宇潮
 * @create 2022-09-28  9:10
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserSevice userSevice;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    @LoginReuquired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginReuquired
    @PostMapping("/upload")
    private String uploadAvatarUrl(MultipartFile avatar, Model model) {
        if (avatar == null) {
            model.addAttribute("error", "您还未没有选择图片");
            return "/site/setting";
        }
        String filename = avatar.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "您上传的文件格式不正确");
            return "/site/setting";
        }
        filename = CommunityUtil.generateUUID() + suffix;
        File file = new File(uploadPath + "/" + filename);

        try {
            avatar.transferTo(file);
        } catch (IOException e) {
            logger.error("文件上传失败:" + e.getMessage());
            throw new RuntimeException("文件上传失败，服务器发生异常" + e);
        }

        User user = hostHolder.getUser();
        String url = domain + contextPath + "/user/header/" + filename;
        userSevice.updateAvatarUrl(user.getId(), url);
        return "redirect:/index";
    }

    @LoginReuquired
    @GetMapping("/header/{filename}")
    public void getAvatarUrl(@PathVariable("filename") String filename, HttpServletResponse response) {
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/" + suffix);

        try (
                FileInputStream is = new FileInputStream(filename);
                ServletOutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = is.read(buffer)) != -1) {
                os.write(buffer);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:" + e.getMessage());
        }
    }

    @LoginReuquired
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String password, Model model) {
        User user = hostHolder.getUser();
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)) {
            model.addAttribute("passwordMsg", "密码不能为空");
            return "/site/setting";
        }
        //校验密码
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))) {
            model.addAttribute("passwordMsg", "密码不正确");
            return "/site/setting";
        }
        String salt = CommunityUtil.generateUUID().substring(0, 5);
        password = CommunityUtil.md5(password + salt);
        userSevice.updatePassword(user.getId(), password, salt);
        return "redirect:/index";
    }

    @GetMapping("/getCode")
    @ResponseBody
    public String getEmialCode(String email, HttpSession session) {
        if (StringUtils.isBlank(email)) {
            //返回json
            return CommunityUtil.getJSONString(ACCEPTED,"邮箱不能为空!");
        }
        String code = CommunityUtil.generateUUID().substring(0, 5);
        session.setAttribute("code",code);
        userSevice.sendEmailCode(email,code);
        //放回响应状态
        return CommunityUtil.getJSONString(SUCCESS, "发送给成功!");
    }

    @PostMapping("/forget")
    public String forgetPassword(String email,String code,String newPassword,
                                 HttpSession session, Model model) {
        //校验验证码
        String kaptcha = (String) session.getAttribute("code");
        if (StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "site/forget";
        }
        Map<String, Object> map = userSevice.forgetPassword(email, newPassword);
        if (map== null || map.isEmpty()) {
            model.addAttribute("msg", "密码找回成功");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "redirect:/login";
        }
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId,Model model) {
        User user = userSevice.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("user", user);
        model.addAttribute("likeCount", userLikeCount);
        return "/site/profile";
    }

}
