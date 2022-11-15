package com.yuchao.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 蒙宇潮
 * @create 2022-11-11  9:56
 */
@Configuration
@EnableScheduling //启动定时任务
@EnableAsync  //让Async多线程注解生效
public class ThreadPoolConfig {


}
