package com.yuchao.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-09-21  17:13
 */

@Data
@ToString
public class DiscussPost {

    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}
