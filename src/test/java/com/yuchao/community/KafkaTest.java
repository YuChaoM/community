package com.yuchao.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 蒙宇潮
 * @create 2022-10-26  15:44
 */

@SpringBootTest
public class KafkaTest {

    @Resource
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() throws InterruptedException {
        kafkaProducer.sendMessage("test", "你好");
        Thread.sleep(3*1000);
        kafkaProducer.sendMessage("test", "hello yuchao");

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//消费者不用自己调，阻塞的线程
@Component
class KafkaProducer {

    @Resource
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}


@Component
class KafkaConsumer {

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}