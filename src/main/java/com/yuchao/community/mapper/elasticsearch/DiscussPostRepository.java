package com.yuchao.community.mapper.elasticsearch;

import com.yuchao.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 蒙宇潮
 * @create 2022-11-02  18:13
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
