package com.yuchao.community;

import com.yuchao.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author 蒙宇潮
 * @create 2022-10-04  22:47
 */
@SpringBootTest
public class SensitiveFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive() {
        String text = "这里可以赌博,可以嫖娼,可以吸毒,可以开票,哈哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以☆赌☆博☆,可以☆嫖☆娼☆,可以☆吸☆毒☆,可以☆开☆票☆,哈哈哈!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "fabc,abc,aafabc";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
