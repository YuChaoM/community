package com.yuchao.community;

import com.yuchao.community.util.MailClient;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author 蒙宇潮
 * @create 2022-09-23  22:30
 */
@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testTextEmail(){
        mailClient.sendMail("mengyuchao2001@163.com","TEST","你好，这是一封测试邮件2");
        mailClient.sendMail("1904246005@qq.com","TEST","你好，这是一封测试邮件2");
    }

    @Test
    public void testHtmlEmail() {
        Context context = new Context();
        context.setVariable("email","Ycuchao");
        context.setVariable("url","http://localhost:8080/community/activation/153c3730429-6ae6-4a42-a85d-527247b68176");

        String content = templateEngine.process("/mail/activation", context);
        System.out.println(content);
        mailClient.sendMail("mengyuchao2001@163.com","HTML",content);
    }



}
