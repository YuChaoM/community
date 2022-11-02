package com.yuchao.community.event;

import com.alibaba.fastjson.JSONObject;
import com.yuchao.community.entity.Event;
import com.yuchao.community.util.CommunityUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 蒙宇潮
 * @create 2022-10-28  10:17
 */

@Component
public class EventProducer {

    @Resource
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event) {
        //将事件发布到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
