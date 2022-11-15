package com.yuchao.community.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author 蒙宇潮
 * @create 2022-11-11  10:18
 */
@Service
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Async //让该方法在多线程环境下，被异步的调用
    public void executel() {
        logger.debug("hello");
    }

    //定时任务
//    @Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void executel2() {
        logger.debug("hello");
    }
}
