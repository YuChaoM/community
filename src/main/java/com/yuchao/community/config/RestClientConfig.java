package com.yuchao.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @author 蒙宇潮
 * @create 2022-11-02  20:14
 */
//@Configuration
//public class RestClientConfig  extends AbstractElasticsearchConfiguration {
//
//    @Value("${spring.elasticsearch.rest.uris}")
//    private String uris;
//
//    @Bean
//    @Override
//    public RestHighLevelClient elasticsearchClient() {
//        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo(uris)
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
//    }
//}
