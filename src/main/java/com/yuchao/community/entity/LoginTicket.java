package com.yuchao.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-09-26  21:08
 */

@Data
@ToString
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
