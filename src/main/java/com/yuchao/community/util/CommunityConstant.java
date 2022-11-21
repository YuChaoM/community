package com.yuchao.community.util;

/**
 * @author 蒙宇潮
 * @create 2022-09-24  13:51
 */
public interface CommunityConstant {

    /**
     * 激活成功
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

    /**
     * 用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 成功
     */
    int SUCCESS = 200;

    /**
     * 请求被接收处理，但是该处理不完整
     */
    int ACCEPTED = 202;

    /**
     * 禁止访问
     */
    int FORBIDDEN = 403;

    int SERVER_ERROR = 500;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 主题：删除
     */
    String TOPIC_DELETE= "delete";

    /**
     * 主题：分享
     */
    String TOPIC_SHARE= "share";


    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";


    /**
     * 权限: 管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限: 版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
