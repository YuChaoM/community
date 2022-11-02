package com.yuchao.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-10-10  14:44
 */

@Data
@ToString
public class Comment {

    private Integer id;
    private Integer userId;
    private Integer entityType;
    private Integer entityId;
    private int targetId;//回复谁
    private String content;
    private int status;
    private Date createTime;
}
