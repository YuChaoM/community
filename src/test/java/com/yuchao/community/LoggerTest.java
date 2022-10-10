package com.yuchao.community;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author 蒙宇潮
 * @create 2022-09-22  22:17
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {

     private static Logger logger = LoggerFactory.getLogger(LoggerTest.class);

     @Test
     void contextLoads() {
          // lambda表达式写法应为这个接口只有一个实现类
          logger.trace("这是trace日志");
          logger.debug("这是debug日志");
          logger.info("这是info日志");
          logger.warn("这是warn日志");
          logger.error("这是error日志");
     }
}
