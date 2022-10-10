package com.yuchao.community.util;

/**
 * @author 蒙宇潮
 * @create 2022-09-24  13:51
 */
public interface CommunityConstant {

    /**
     *激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    int REMEMBER_EXPIRED_SECONDS = 30 * 24 * 3600;

    int DEFAULT_EXPIRED_SECONDS = 12 * 3600;

    /**
     * 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
