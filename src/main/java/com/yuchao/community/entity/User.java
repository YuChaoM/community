package com.yuchao.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-09-19  15:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String avatarUrl;
    private Date createTime;


}
