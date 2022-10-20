package com.yuchao.community.util;

import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * @author 蒙宇潮
 * @create 2022-10-20  14:43
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    //某个实体的赞
    //like:entity:entityType:entityId set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

}
