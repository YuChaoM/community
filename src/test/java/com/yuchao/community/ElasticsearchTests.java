package com.yuchao.community;


import com.yuchao.community.entity.DiscussPost;
import com.yuchao.community.mapper.elasticsearch.DiscussPostRepository;
import com.yuchao.community.mapper.DiscussPostMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;

import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;


import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
public class ElasticsearchTests {

    @Resource
    private DiscussPostMapper discussMapper;

    @Resource
    private DiscussPostRepository discussRepository;

    @Resource
    private RestHighLevelClient client;

    //过时,删除了这个类，会报错的
//    @Resource
//    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void tet() throws IOException {
        System.out.println(client);
        CountRequest countRequest = new CountRequest("discusspost");
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        System.out.println(countResponse.getCount());
    }

    @Test
    public void testInsert() {
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));

//        elasticsearchRestTemplate.save(discussMapper.selectDiscussPostById(244));
    }

    @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPost(101, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(102, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(103, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(111, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(112, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(131, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(132, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(133, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPost(134, 0, 100, 0));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人,使劲灌水.");
        discussRepository.save(post);
    }

    @Test
    public void testDelete() {
        // discussRepository.deleteById(231);
        discussRepository.deleteAll();
        //elasticsearchRestTemplate没有deleteAll， ElasticsearchRepository有，因为它是查出所有在调delete
//        elasticsearchRestTemplate.delete(242);
    }

    @Test
    public void testSearchByRepository() throws IOException {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(1, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // 底层获取得到了高亮显示的值, 但是没有返回.

//        Page<DiscussPost> page = discussRepository.search(searchQuery);
        SearchHits<DiscussPost> page = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        SearchRequest searchRequest = new SearchRequest("discusspost");
        System.out.println(page.getTotalHits());//结果的总行数
        ArrayList<DiscussPost> list = new ArrayList<>();
        //遍历一页
        for (SearchHit<DiscussPost> searchHit : page) {
//            System.out.println(searchHit);
//            DiscussPost discussPost = new DiscussPost();
            DiscussPost content = searchHit.getContent();
//            BeanUtils.copyProperties(content, discussPost);
            System.out.println(searchHit);

            //处理高亮
//            List<String> titleField = searchHit.getHighlightFields().get("title");
//            System.out.println(titleField);
//
//            List<String> contentField = searchHit.getHighlightFields().get("content");
//            System.out.println(contentField);

            List<String> titleField = searchHit.getHighlightField("title");
            if (!titleField.isEmpty()) {
                content.setTitle(titleField.get(0));
            }
            List<String> contentField = searchHit.getHighlightField("content");
            if (!contentField.isEmpty()) {
                content.setContent(contentField.get(0));
            }
            list.add(content);
        }
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }
        System.out.println(list.size());
    }

    @Test
    public void testSearchByTemplate() {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
//
//        Page<DiscussPost> page = elasticsearchRestTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
//            @Override
//            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
//                SearchHits hits = response.getHits();
//                if (hits.getTotalHits() <= 0) {
//                    return null;
//                }
//
//                List<DiscussPost> list = new ArrayList<>();
//                for (SearchHit hit : hits) {
//                    DiscussPost post = new DiscussPost();
//
//                    String id = hit.getSourceAsMap().get("id").toString();
//                    post.setId(Integer.valueOf(id));
//
//                    String userId = hit.getSourceAsMap().get("userId").toString();
//                    post.setUserId(Integer.valueOf(userId));
//
//                    String title = hit.getSourceAsMap().get("title").toString();
//                    post.setTitle(title);
//
//                    String content = hit.getSourceAsMap().get("content").toString();
//                    post.setContent(content);
//
//                    String status = hit.getSourceAsMap().get("status").toString();
//                    post.setStatus(Integer.valueOf(status));
//
//                    String createTime = hit.getSourceAsMap().get("createTime").toString();
//                    post.setCreateTime(new Date(Long.valueOf(createTime)));
//
//                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
//                    post.setCommentCount(Integer.valueOf(commentCount));
//
//                    // 处理高亮显示的结果
//                    HighlightField titleField = hit.getHighlightFields().get("title");
//                    if (titleField != null) {
//                        post.setTitle(titleField.getFragments()[0].toString());
//                    }
//
//                    HighlightField contentField = hit.getHighlightFields().get("content");
//                    if (contentField != null) {
//                        post.setContent(contentField.getFragments()[0].toString());
//                    }
//
//                    list.add(post);
//                }
//
//                return new AggregatedPageImpl(list, pageable,
//                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
//            }
//        });
//
//        System.out.println(page.getTotalElements());
//        System.out.println(page.getTotalPages());
//        System.out.println(page.getNumber());
//        System.out.println(page.getSize());
//        for (DiscussPost post : page) {
//            System.out.println(post);
//        }
    }

}
