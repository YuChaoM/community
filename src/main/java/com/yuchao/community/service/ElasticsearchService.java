package com.yuchao.community.service;

import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.mapper.DiscussPostMapper;
import com.yuchao.community.mapper.elasticsearch.DiscussPostRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 蒙宇潮
 * @create 2022-11-04  10:30
 */
@Service
public class ElasticsearchService {

    @Resource
    ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    DiscussPostRepository discussPostRepository;
    @Resource
    DiscussPostMapper discussPostMapper;

    public void save(int postId) {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(postId));
    }

    public void delete(int postId) {
        discussPostRepository.deleteById(postId);
    }

    public Map<String, Object> search(String keyWord,int current,int limit) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyWord, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        HashMap<String, Object> res = new HashMap<>();
        long rows = searchHits.getTotalHits();
        res.put("rows",rows);
        //处理高亮
        ArrayList<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPost discussPost = searchHit.getContent();
            List<String> title = searchHit.getHighlightField("title");
            if (!title.isEmpty()){
                discussPost.setTitle(title.get(0));
            }
            List<String> content = searchHit.getHighlightField("content");
            if (!content.isEmpty()) {
                discussPost.setContent(content.get(0));
            }
            list.add(discussPost);
        }
        res.put("page", list);
        return res;
    }

}
