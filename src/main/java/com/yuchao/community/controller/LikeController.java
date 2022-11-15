package com.yuchao.community.controller;

import com.yuchao.community.entity.Event;
import com.yuchao.community.entity.User;
import com.yuchao.community.event.EventProducer;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.CommunityUtil;
import com.yuchao.community.util.HostHolder;
import com.yuchao.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author 蒙宇潮
 * @create 2022-10-20  15:01
 */
@Controller
public class LikeController implements CommunityConstant {

    @Resource
    private LikeService likeService;
    @Resource
    private HostHolder hostHolder;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int discussPostId) {
        User user = hostHolder.getUser();
        // 点赞/取消 这里可能发生空指针异常，同一处理了
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus = likeService.findEntityStatus(user.getId(), entityType, entityId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //触发点赞事件
        if (likeStatus == 1) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("discussPostId", discussPostId);
            Event event = Event.builder()
                    .topic(TOPIC_LIKE)
                    .userId(user.getId())
                    .entityType(entityType)
                    .entityId(entityId)
                    .entityUserId(entityUserId)
                    .data(data)
                    .build();
            eventProducer.fireEvent(event);
        }
        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, discussPostId);

        return CommunityUtil.getJSONString(SUCCESS, null, map);
    }


}
