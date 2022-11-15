package com.yuchao.community.service;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.mapper.DiscussPostMapper;
import com.yuchao.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 蒙宇潮
 * @create 2022-09-24  11:13
 */
@Service
public class DiscussPostService {

    @Resource
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;


    public List<DiscussPost> findDiscussPosts(Integer userId, Integer offset, Integer limit, int orderMode) {
        return discussPostMapper.selectDiscussPost(userId, offset, limit,orderMode);
    }

    public int findDiscussPostRows(Integer userId) {
        return discussPostMapper.selectDiscussPostTotal(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //敏感词过滤
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(Integer id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCountById(Integer entityId) {
        return discussPostMapper.updateCommentCountById( entityId);
    }

    public int updateType(int id, int type) {
       return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id,int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public void updateScore(int postId, double score) {
        discussPostMapper.updateScore(postId, score);
    }
}
