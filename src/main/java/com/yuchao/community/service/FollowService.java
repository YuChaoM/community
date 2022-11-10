package com.yuchao.community.service;

import com.yuchao.community.entity.User;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 蒙宇潮
 * @create 2022-10-22  12:29
 */
@Service
public class FollowService implements CommunityConstant {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userSevice;

    public void follow(int entityId, int entityType, int userId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    public void unfollow(int entityId, int entityType, int userId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);

                return redisOperations.exec();
            }
        });
    }

    //查询某个用户关注的实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //查询实体的粉丝的数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    //查询某个用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        ArrayList<Map<String, Object>> followeeList = new ArrayList<>();
        //倒序
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (targetIds == null) {
            return null;
        }
        for (Integer targetId : targetIds) {
            HashMap<String, Object> map = new HashMap<>();
            User user = userSevice.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            Date date = new Date(score.longValue());
            map.put("followTime", date);
            followeeList.add(map);
        }
        return followeeList;
    }

    //查询某个用户的粉丝列表
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetId = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (targetId == null) {
            return null;
        }
        ArrayList<Map<String, Object>> followerList = new ArrayList<>();
        for (Integer target : targetId) {
            HashMap<String, Object> map = new HashMap<>();
            User user = userSevice.findUserById(target);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, target);
            map.put("followTime", new Date(score.longValue()));
            followerList.add(map);
        }
        return followerList;
    }

}
