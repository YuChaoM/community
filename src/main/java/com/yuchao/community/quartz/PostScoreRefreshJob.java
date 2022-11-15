package com.yuchao.community.quartz;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.service.DiscussPostService;
import com.yuchao.community.service.ElasticsearchService;
import com.yuchao.community.service.LikeService;
import com.yuchao.community.util.CommunityConstant;
import com.yuchao.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 蒙宇潮
 * @create 2022-11-14  15:37
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private LikeService likeService;
    @Resource
    private ElasticsearchService elasticsearchService;
    // 牛客纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-10-1 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化纪元年份失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }
        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    private void refresh(Integer postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null || post.getStatus() == 2) {
            logger.error("该帖子不存在：id=" + postId);
            return;
        }
        //是否加精
        boolean wonderful = post.getStatus() == 1;
        //评论数量
        int commentCount = post.getCommentCount();
        //点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        //计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //分数
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        //更新帖子分数
        discussPostService.updateScore(postId, score);
        //同步es
        elasticsearchService.save(postId);

    }
}
