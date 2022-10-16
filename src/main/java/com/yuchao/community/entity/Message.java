package com.yuchao.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-10-12  23:02
 */
@Data
@ToString
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
