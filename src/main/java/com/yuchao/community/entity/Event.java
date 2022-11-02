package com.yuchao.community.entity;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒙宇潮
 * @create 2022-10-28  10:06
 */

@Data
@Builder
public class Event {

    private String topic;
    private int userId; //触发事件的人
    private int entityId;
    private int entityType;
    private int entityUserId;//系统给他发通知
    private Map<String, Object> data ;//其他一些数据，比如帖子id

}
