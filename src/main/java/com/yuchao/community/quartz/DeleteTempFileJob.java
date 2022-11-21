package com.yuchao.community.quartz;


import com.mchange.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @author 蒙宇潮
 * @create 2022-11-18  18:59
 */
public class DeleteTempFileJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTempFileJob.class);

    @Value("${wk.image.storage}")
    String wkImageStorage;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        File file = new File(wkImageStorage);
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                try {
                    f.delete();
                } catch (Exception e) {
                    logger.error("文件删除失败:" + e);
                }
            }
        }
        logger.info("执行删除临时文件任务");
    }
}
