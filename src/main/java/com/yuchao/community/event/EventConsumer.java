package com.yuchao.community.event;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.entity.Event;
import com.yuchao.community.entity.Message;
import com.yuchao.community.mapper.DiscussPostMapper;
import com.yuchao.community.service.ElasticsearchService;
import com.yuchao.community.service.MessageService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author 蒙宇潮
 * @create 2022-10-28  10:22
 */

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Resource
    private MessageService messageService;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${wk.image.command}")
    private String wkCommand;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Resource
    OSS ossClient;

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        //发送系统通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        HashMap<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (event.getData() != null && !event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePost(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        elasticsearchService.save(event.getEntityId());
    }

    //消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeletePost(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        elasticsearchService.delete(event.getEntityId());
    }

    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShare(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        File file = new File(wkImageStorage);
        if (!file.exists()) {
            file.mkdirs();
        }
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        StringBuilder sb = new StringBuilder();
        String cmd = sb.append(wkCommand)
                .append(" --quality 75 ")
                .append(htmlUrl)
                .append(" ")
                .append(wkImageStorage)
                .append("/")
                .append(fileName)
                .append(suffix).toString();

        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功: " + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败:" + e);
        }

        // 启用定时器,监视该图片,一旦生成了,则上传至oss.
        UploadTask task = new UploadTask(fileName, suffix);
        //500m后开始执行定时任务 间隔500ms，3次
        Future future = taskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }

    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值
        private Future future;
        // 开始时间
        private long startTime;
        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            // 生成失败
            if (System.currentTimeMillis() - startTime > 30000) {
                logger.error("执行时间过长,终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes >= 3) {
                logger.error("上传次数过多,终止任务:" + fileName);
                future.cancel(true);
                return;
            }

            String path = wkImageStorage + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                logger.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                String dir = "share/";
                // 上传文件名称,可以设置目录
                String objectName = dir + fileName + suffix;

                try {
                    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
                    // 开始上传图片
                    ossClient.putObject(putObjectRequest);
                    //取消任务
                    ossClient.shutdown();
                    future.cancel(true);
                } catch (OSSException e) {
                    logger.info(String.format("第%d次上传失败[%s],Code:%s,Message:%s,Request ID:%s,Host ID:%s.", uploadTimes, fileName,
                            e.getErrorCode(), e.getErrorMessage(), e.getRequestId(), e.getHostId()));
                } finally {
                    //如果上传失败，超过3次
                    if (ossClient != null && uploadTimes >= 3) {
                        ossClient.shutdown();
                        future.cancel(true);
                    }
                }
            } else {
                logger.info("等待图片生成[" + fileName + "].");
            }
        }
    }
}
